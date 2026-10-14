<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Directory - Student Profile Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/students" class="nav-brand">
            <span class="brand-icon">🎓</span>
            <span>StudentProfilePortal</span>
        </a>

        <div class="nav-user">
            <!-- Role-Differentiated User Badge in Navbar -->
            <c:choose>
                <c:when test="${sessionScope.role == 'admin'}">
                    <span class="user-badge badge-admin">
                        <span>🛡️</span>
                        <span>ADMIN: <strong><c:out value="${sessionScope.user}"/></strong></span>
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="user-badge badge-faculty">
                        <span>🎓</span>
                        <span>FACULTY: <strong><c:out value="${sessionScope.user}"/></strong></span>
                    </span>
                </c:otherwise>
            </c:choose>

            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">
                <span>🚪</span>
                <span>Logout</span>
            </a>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container">

        <!-- Status Alerts -->
        <c:if test="${param.added == 'true'}">
            <div class="alert alert-success">
                <span>✅</span>
                <span>Student record successfully added to the database!</span>
            </div>
        </c:if>

        <c:if test="${param.deleted == 'true'}">
            <div class="alert alert-success">
                <span>🗑️</span>
                <span>Student record was deleted successfully.</span>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <span>⚠️</span>
                <span><c:out value="${errorMessage}"/></span>
            </div>
        </c:if>

        <!-- Role-Specific Header Banner -->
        <c:choose>
            <c:when test="${sessionScope.role == 'admin'}">
                <!-- ================= ADMIN HEADER ================= -->
                <div class="page-header header-admin">
                    <div>
                        <div class="role-indicator-tag admin-tag">🛡️ FULL ADMINISTRATOR ACCESS</div>
                        <h1>System Administration Portal</h1>
                        <p>You have full privileges to view, add, edit, and delete student records and verification files.</p>
                    </div>
                    <div class="header-right-actions">
                        <button type="button" class="btn-add-student" onclick="openAddModal()">
                            <span>➕</span> Add New Student
                        </button>
                        <div class="student-count-pill">
                            <span>Total Students: </span>
                            <strong><c:out value="${studentList != null ? studentList.size() : 0}"/></strong>
                        </div>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <!-- ================= FACULTY HEADER ================= -->
                <div class="page-header header-faculty">
                    <div>
                        <div class="role-indicator-tag faculty-tag">🎓 FACULTY ADVISOR VIEW (READ-ONLY)</div>
                        <h1>Faculty Academic Dashboard</h1>
                        <p>Academic profile review mode. Student records and documents are viewable in read-only mode.</p>
                    </div>
                    <div class="header-right-actions">
                        <div class="faculty-notice-chip">
                            <span>🔒 Record Editing Restricted to Admin</span>
                        </div>
                        <div class="student-count-pill">
                            <span>Total Students: </span>
                            <strong><c:out value="${studentList != null ? studentList.size() : 0}"/></strong>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Conditional Check: If Student List is Empty -->
        <c:choose>
            <c:when test="${empty studentList}">
                <div class="empty-state">
                    <div class="empty-icon">📂</div>
                    <h3>No Student Records Available</h3>
                    <p>No students were retrieved from the database. Please ensure your sample SQL data is inserted.</p>
                </div>
            </c:when>

            <c:otherwise>
                <!-- Dynamic Loop over Student Records -->
                <div class="student-grid">
                    <c:forEach var="student" items="${studentList}">
                        <div class="student-card">
                            
                            <!-- Student Photo Section -->
                            <div class="card-photo-wrapper">
                                <span class="roll-badge"><c:out value="${student.rollNo}"/></span>
                                
                                <c:choose>
                                    <c:when test="${not empty student.photoPath}">
                                        <!-- Photo retrieved securely via FileServlet -->
                                        <img src="${pageContext.request.contextPath}/file?name=${student.photoPath}&type=photo" 
                                             alt="Photo of ${student.name}" 
                                             class="student-photo"
                                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';"/>
                                        <div class="photo-placeholder" style="display: none;">
                                            <span class="icon">👤</span>
                                            <span class="missing-text">Photo Not Found</span>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Fallback when student has no photo in database -->
                                        <div class="photo-placeholder">
                                            <span class="icon">👤</span>
                                            <span class="missing-text">No Photo Uploaded</span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Student Details Body -->
                            <div class="card-body">
                                <h2 class="student-name"><c:out value="${student.name}"/></h2>
                                
                                <div class="info-item">
                                    <span class="icon">🏛️</span>
                                    <span><strong>Dept:</strong> <c:out value="${student.department}"/></span>
                                </div>

                                <div class="info-item">
                                    <span class="icon">✉️</span>
                                    <span><strong>Email:</strong> <a href="mailto:<c:out value="${student.email}"/>"><c:out value="${student.email}"/></a></span>
                                </div>
                            </div>

                            <!-- Document Link Section -->
                            <div class="card-footer">
                                <c:choose>
                                    <c:when test="${not empty student.documentPath}">
                                        <!-- Secure link pointing to FileServlet for PDF streaming -->
                                        <a href="${pageContext.request.contextPath}/file?name=${student.documentPath}&type=doc" 
                                           target="_blank" 
                                           rel="noopener noreferrer"
                                           class="btn-doc">
                                            <span>📄</span>
                                            <span>View PDF Document</span>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Missing document message -->
                                        <span class="badge-missing">
                                            <span>⚠️</span>
                                            <span>No document attached</span>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- ROLE-SPECIFIC CARD ACTIONS (ADMIN VS FACULTY) -->
                            <c:choose>
                                <c:when test="${sessionScope.role == 'admin'}">
                                    <!-- Admin Controls: Can Delete Record -->
                                    <div class="card-admin-strip">
                                        <span class="admin-label">🛡️ Admin Control:</span>
                                        <form action="${pageContext.request.contextPath}/delete-student" method="POST" style="margin:0;"
                                              onsubmit="return confirm('Are you sure you want to delete student: ${student.name} (${student.rollNo})?');">
                                            <input type="hidden" name="id" value="${student.id}"/>
                                            <button type="submit" class="btn-delete">
                                                <span>🗑️</span> Delete
                                            </button>
                                        </form>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- Faculty Controls: Read-Only Badge -->
                                    <div class="card-faculty-strip">
                                        <span>🔒 Read-Only Record (Faculty Mode)</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <!-- ADMIN ONLY: Modal to Add New Student -->
    <c:if test="${sessionScope.role == 'admin'}">
        <div id="addStudentModal" class="modal-overlay" style="display: none;">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>➕ Add New Student Record</h3>
                    <button type="button" class="btn-close-modal" onclick="closeAddModal()">&times;</button>
                </div>
                <form action="${pageContext.request.contextPath}/add-student" method="POST">
                    <div class="form-group">
                        <label for="roll_no">Roll Number *</label>
                        <input type="text" id="roll_no" name="roll_no" class="form-control" placeholder="e.g. 22CS104" required>
                    </div>
                    <div class="form-group">
                        <label for="name">Full Name *</label>
                        <input type="text" id="name" name="name" class="form-control" placeholder="e.g. David Miller" required>
                    </div>
                    <div class="form-group">
                        <label for="department">Department *</label>
                        <input type="text" id="department" name="department" class="form-control" placeholder="e.g. Mechanical Engineering" required>
                    </div>
                    <div class="form-group">
                        <label for="email">College Email *</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="e.g. david.miller@example.edu" required>
                    </div>
                    <div class="form-group">
                        <label for="photo_path">Photo Filename (Optional)</label>
                        <input type="" id="photo_path" name="photo_path" class="form-control" placeholder="e.g. alice_photo.png or leave blank">
                    </div>
                    <div class="form-group">
                        <label for="document_path">PDF Document Filename (Optional)</label>
                        <input type="text" id="document_path" name="document_path" class="form-control" placeholder="e.g. alice_id_card.pdf or leave blank">
                    </div>
                    <div class="modal-actions">
                        <button type="button" class="btn-secondary" onclick="closeAddModal()">Cancel</button>
                        <button type="submit" class="btn-primary" style="margin-top:0; width:auto; padding:0.65rem 1.5rem;">Save Student</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openAddModal() {
                document.getElementById('addStudentModal').style.display = 'flex';
            }
            function closeAddModal() {
                document.getElementById('addStudentModal').style.display = 'none';
            }
            window.onclick = function(event) {
                var modal = document.getElementById('addStudentModal');
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            };
        </script>
    </c:if>

    <footer class="footer">
        <p>&copy; 2026 StudentProfilePortal — Java Servlets &amp; JSP Web Technologies Assignment</p>
    </footer>

</body>
</html>
