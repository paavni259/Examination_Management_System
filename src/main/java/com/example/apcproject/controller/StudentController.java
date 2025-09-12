// Updated StudentController.java
package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.apcproject.model.Answer;
import com.example.apcproject.model.Result;
import com.example.apcproject.model.Student;
import com.example.apcproject.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student s) {
        try {
            return ResponseEntity.ok(studentService.register(s));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String identifier,
                                   @RequestParam String password) {
        Student student = studentService.loginByIdentifier(identifier, password);
        if (student != null) {
            return ResponseEntity.ok(student);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/{studentId}/exams/{examId}/submit")
    public ResponseEntity<List<Answer>> submit(@PathVariable String studentId,
                                               @PathVariable String examId,
                                               @RequestBody List<Answer> answers) {
        return ResponseEntity.ok(studentService.submitAnswers(answers, examId, studentId));
    }

    @GetMapping("/{studentId}/results/{examId}")
    public ResponseEntity<Result> getResult(@PathVariable String studentId,
                                            @PathVariable String examId) {
        return studentService.getResult(studentId, examId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{studentId}/results")
    public ResponseEntity<List<Result>> allResults(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getResultsForStudent(studentId));
    }
}
