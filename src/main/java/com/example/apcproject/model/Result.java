package com.example.apcproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "results")
public class Result {
    @Id
    private String id;

    private String studentId;
    private String examId;
    private int marks;       // number of correct answers
    private int totalMarks;  // total number of questions
    private String status;   // Pass / Fail

    // --- Getters & Setters ---
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExamId() {
        return examId;
    }
    public void setExamId(String examId) {
        this.examId = examId;
    }

    public int getMarks() {
        return marks;
    }
    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }
    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
