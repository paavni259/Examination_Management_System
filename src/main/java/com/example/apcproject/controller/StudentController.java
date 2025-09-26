package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // -------------------- Registration --------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student s) {
        try {
            return ResponseEntity.ok(studentService.register(s));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------------------- Login --------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String identifier,
                                   @RequestParam String password) {
        Student student = studentService.loginByIdentifier(identifier, password);
        if (student != null) {
            return ResponseEntity.ok(student);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // -------------------- Submit Exam --------------------
    @PostMapping("/{studentId}/exams/{examId}/submit")
    public ResponseEntity<Result> submit(@PathVariable String studentId,
                                         @PathVariable String examId,
                                         @RequestBody List<Answer> answers) {
        Result result = studentService.submitAnswers(answers, examId, studentId);
        return ResponseEntity.ok(result);
    }

    // -------------------- Results --------------------
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
