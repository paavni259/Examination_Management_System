package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.service.FacultyService;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }
    // restful api start
    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody Exam e) {
        return ResponseEntity.ok(facultyService.createExam(e));
    }

    @PostMapping("/exams/{id}/questions")
    public ResponseEntity<Object> addQuestion(@PathVariable String id, @RequestBody Question q) {
        return ResponseEntity.ok(facultyService.addQuestion(id, q));
    }

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getExams() {
        return ResponseEntity.ok(facultyService.getExams());
    }

    @GetMapping("/results/{examId}")
    public ResponseEntity<List<Result>> getResults(@PathVariable String examId) {
        return ResponseEntity.ok(facultyService.getResults(examId));
    }

    // view controller part and server function in the frontend
    //  @GetMapping("/exams/create-and-add")
    // public String showCreateExamPage(Model model) {
    //     model.addAttribute("exam", new Exam());
    //     model.addAttribute("question", new Question());
    //     return "exam-create-and-question";
    // }

    // @PostMapping("/exams")
    // public String createExam(@ModelAttribute Exam exam, Model model) {
    //     Exam savedExam = facultyService.createExam(exam);

    //     model.addAttribute("examId", savedExam.getId());
    //     model.addAttribute("exam", savedExam);
    //     model.addAttribute("question", new Question());

    //     return "exam-create-and-question"; // reload same page with examId
    // }

    // @PostMapping("/exams/{id}/questions")
    // public String addQuestion(@PathVariable String id,
    //                           @ModelAttribute Question question,
    //                           Model model) {
    //     facultyService.addQuestion(id, question);

    //     model.addAttribute("examId", id);
    //     model.addAttribute("exam", facultyService.getExams().stream()
    //                                .filter(e -> e.getId().equals(id)).findFirst().orElse(null));
    //     model.addAttribute("question", new Question());

    //     return "exam-create-and-question"; // reload same page so faculty can add more
    // }
}