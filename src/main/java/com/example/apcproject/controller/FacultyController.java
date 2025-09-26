package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.service.FacultyService;
import com.example.apcproject.service.QuestionImportService;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;
    private final QuestionImportService importService;

    public FacultyController(FacultyService facultyService, QuestionImportService importService) {
        this.facultyService = facultyService;
        this.importService = importService;
    }

    /* ===================== Exams ===================== */

    // Create new exam
    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody Exam e) {
        Exam saved = facultyService.createExam(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Get all exams
    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getExams() {
        return ResponseEntity.ok(facultyService.getExams());
    }

    // Update existing exam
    @PutMapping("/exams/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable String id, @RequestBody Exam updated) {
        try {
            return ResponseEntity.ok(facultyService.updateExam(id, updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete exam
    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        facultyService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    /* ===================== Questions ===================== */

    // Add question to exam
    @PostMapping("/exams/{id}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable String id, @RequestBody Question q) {
        Question saved = facultyService.addQuestion(id, q);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Bulk upload questions from file (Excel/CSV)
    @PostMapping(value = "/exams/{id}/questions/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<List<Question>> uploadQuestions(
            @PathVariable String id,
            @RequestPart("file") MultipartFile file) {
        List<Question> parsed = importService.parseFile(file);
        List<Question> saved = facultyService.addQuestions(id, parsed);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update question
    @PutMapping("/questions/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable String id, @RequestBody Question updated) {
        try {
            Question saved = facultyService.updateQuestion(id, updated);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete question
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        facultyService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    /* ===================== Results ===================== */

    // Get results of a specific exam
    @GetMapping("/results/{examId}")
    public ResponseEntity<List<Result>> getResults(@PathVariable String examId) {
        return ResponseEntity.ok(facultyService.getResults(examId));
    }
}
