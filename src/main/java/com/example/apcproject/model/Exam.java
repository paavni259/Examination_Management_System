package com.example.apcproject.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "exams")
public class Exam {

    @Id
    private String id;

    private String title;
    private String description;
    private LocalDate date;
    private String course;

    private Integer duration;     // duration in minutes
    private Integer totalMarks;   // optional, calculated as number of questions

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }
}
