package com.example.apcproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "answers")
public class Answer {
    @Id
    private String id;
    private String studentId;
    private String examId;
    private String questionId;
    private String answerText;
    private Integer marks;

    private boolean correct; // ✅ add this field

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}

