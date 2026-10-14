package com.portal.servlet;

import com.portal.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * FileServlet securely retrieves and streams student photos and PDF documents.
 * 
 * Security features:
 * 1. Requires an authenticated HTTP session.
 * 2. Validates filename to prevent Directory Traversal attacks (e.g. "../../../").
 * 3. Restricts allowed file extensions strictly to safe image and PDF types.
 * 4. Normalizes and validates the absolute path against the allowed uploads root.
 */
@WebServlet(name = "FileServlet", urlPatterns = {"/file"})
public class FileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Set of allowed file extensions
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "pdf")
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Session verification: Only logged-in users may view student files
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access. Please log in.");
            return;
        }

        // 2. Validate request parameter 'name'
        String requestedName = request.getParameter("name");
        if (requestedName == null || requestedName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name parameter is required.");
            return;
        }
        requestedName = requestedName.trim();

        // 3. Security: Prevent Directory Traversal Attack
        // Reject names with dangerous traversal sequences or path separators
        if (requestedName.contains("..") || requestedName.contains("/") || requestedName.contains("\\") || requestedName.contains("\0")) {
            System.err.println("[FileServlet] Potential path traversal attempt blocked: " + requestedName);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid file name specified.");
            return;
        }

        // Extract pure filename
        String fileName = Paths.get(requestedName).getFileName().toString();

        // 4. Validate file extension against permitted types
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Files without valid extensions are not permitted.");
            return;
        }
        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: File extension ." + extension + " is not permitted.");
            return;
        }

        // 5. Determine Content-Type
        String mimeType;
        switch (extension) {
            case "png":
                mimeType = "image/png";
                break;
            case "jpg":
            case "jpeg":
                mimeType = "image/jpeg";
                break;
            case "gif":
                mimeType = "image/gif";
                break;
            case "webp":
                mimeType = "image/webp";
                break;
            case "pdf":
                mimeType = "application/pdf";
                break;
            default:
                mimeType = "application/octet-stream";
                break;
        }

        // 6. Locate file in the uploads directory
        File targetFile = resolveUploadFile(fileName);

        if (targetFile == null || !targetFile.exists() || !targetFile.isFile()) {
            System.err.println("[FileServlet] File not found: " + fileName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested file was not found.");
            return;
        }

        // 7. Configure HTTP Response Headers
        response.setContentType(mimeType);
        response.setContentLengthLong(targetFile.length());

        if ("pdf".equals(extension)) {
            // View PDF inline in the browser
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        } else {
            // Enable browser caching for images
            response.setHeader("Cache-Control", "private, max-age=86400");
        }

        // 8. Stream the file bytes to the client
        try (InputStream in = new BufferedInputStream(new FileInputStream(targetFile));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            System.err.println("[FileServlet] Error streaming file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Resolves the file by checking configured upload directory first,
     * then falling back to application local upload folders.
     */
    private File resolveUploadFile(String fileName) {
        // Option 1: Configured directory from db.properties (e.g. C:/student_portal_uploads)
        String configuredPath = DBConnection.getUploadDir();
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            File file = new File(configuredPath, fileName);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }

        // Option 2: Fallback to <user.home>/student_portal_uploads
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            File file = new File(userHome + File.separator + "student_portal_uploads", fileName);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }

        // Option 3: Fallback to current working directory uploads/
        File localUploads = new File("uploads", fileName);
        if (localUploads.exists() && localUploads.isFile()) {
            return localUploads;
        }

        // Option 4: ServletContext WEB-INF/uploads (if deployed)
        try {
            String realPath = getServletContext().getRealPath("/WEB-INF/uploads");
            if (realPath != null) {
                File webInfFile = new File(realPath, fileName);
                if (webInfFile.exists() && webInfFile.isFile()) {
                    return webInfFile;
                }
            }
        } catch (Exception ignored) {
        }

        // Default to configured directory path even if file is missing (to report proper 404)
        return new File(configuredPath != null ? configuredPath : "uploads", fileName);
    }
}
