package com.example.apcproject.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exams")
public class Exam {
    @Id
    private String id;
    private String title;
    private String course;
    private String date;
    private Integer duration;
    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
}
