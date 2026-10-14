package com.portal.model;

import java.io.Serializable;

/**
 * Model representing a Student Profile in the college portal.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String rollNo;
    private String name;
    private String department;
    private String email;
    private String photoPath;
    private String documentPath;

    public Student() {
    }

    public Student(int id, String rollNo, String name, String department, String email, String photoPath, String documentPath) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.email = email;
        this.photoPath = photoPath;
        this.documentPath = documentPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    /**
     * Helper method to check if the student has an assigned photo.
     */
    public boolean hasPhoto() {
        return photoPath != null && !photoPath.trim().isEmpty();
    }

    /**
     * Helper method to check if the student has an assigned PDF document.
     */
    public boolean hasDocument() {
        return documentPath != null && !documentPath.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", rollNo='" + rollNo + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", email='" + email + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", documentPath='" + documentPath + '\'' +
                '}';
    }
}
