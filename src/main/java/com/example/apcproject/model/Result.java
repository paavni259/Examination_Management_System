package com.example.apcproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "results")
public class Result {
    @Id
    private String id;
    private String studentId;
    private String examId;
    private Integer marks;
    private String status;

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
