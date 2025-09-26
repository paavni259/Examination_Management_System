package com.example.apcproject.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
public class Student {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Indexed(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    private String password;

    @Indexed(unique = true)
    @NotBlank(message = "Roll number is required")
    @Size(max = 50, message = "Roll number must be at most 50 characters")
    private String rollNumber;

    private List<String> enrolledCourses;

    // ✅ New fields for dashboard
    @CreatedDate
    private LocalDate createdAt = LocalDate.now();  

    private String status = "Active";  

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<String> enrolledCourses) { this.enrolledCourses = enrolledCourses; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
