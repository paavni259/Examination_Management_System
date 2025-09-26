package com.example.apcproject.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.repository.ExamRepository;
import com.example.apcproject.repository.FacultyRepository;
import com.example.apcproject.repository.QuestionRepository;
import com.example.apcproject.repository.ResultRepository;

@Service
public class FacultyService {

    private final ExamRepository examRepo;
    private final QuestionRepository questionRepo;
    private final ResultRepository resultRepo;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(ExamRepository examRepo,
                          QuestionRepository questionRepo,
                          ResultRepository resultRepo,
                          FacultyRepository facultyRepository,
                          PasswordEncoder passwordEncoder) {
        this.examRepo = examRepo;
        this.questionRepo = questionRepo;
        this.resultRepo = resultRepo;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= Exam Management ================= */

    public Exam createExam(Exam e) {
        return examRepo.save(e);
    }

    public Question addQuestion(String examId, Question q) {
        q.setId(null);
        q.setExamId(examId);
        return questionRepo.save(q);
    }

    public List<Question> addQuestions(String examId, List<Question> questions) {
        List<Question> prepared = new ArrayList<>();
        for (Question q : questions) {
            q.setId(null);
            q.setExamId(examId);
            prepared.add(q);
        }
        return questionRepo.saveAll(prepared);
    }

    public List<Exam> getExams() {
        return examRepo.findAll();
    }

    public List<Result> getResults(String examId) {
        return resultRepo.findByExamId(examId);
    }

    public Exam updateExam(String examId, Exam updatedExam) {
        return examRepo.findById(examId)
                .map(exam -> {
                    exam.setTitle(updatedExam.getTitle());
                    exam.setCourse(updatedExam.getCourse());
                    exam.setDate(updatedExam.getDate());
                    exam.setDuration(updatedExam.getDuration());
                    return examRepo.save(exam);
                })
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }

    public Question getQuestionById(String questionId) {
        return questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public Question updateQuestion(String questionId, Question updated) {
        return questionRepo.findById(questionId)
                .map(q -> {
                    q.setQuestionText(updated.getQuestionText());
                    q.setOptions(updated.getOptions());
                    q.setCorrectAnswer(updated.getCorrectAnswer());
                    return questionRepo.save(q);
                })
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public boolean examHasQuestions(String examId) {
        return !questionRepo.findByExamId(examId).isEmpty();
    }

    public void deleteExam(String examId) {
        examRepo.deleteById(examId);
        questionRepo.deleteAll(questionRepo.findByExamId(examId));
    }

    public void deleteQuestion(String questionId) {
        questionRepo.deleteById(questionId);
    }

    /* ================= Faculty Auth ================= */

    public Faculty register(Faculty faculty) {
        if (facultyRepository.existsByEmail(faculty.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        faculty.setPassword(passwordEncoder.encode(faculty.getPassword()));
        return facultyRepository.save(faculty);
    }

    public Faculty findByEmail(String email) {
        return facultyRepository.findByEmail(email)
                .orElse(null);
    }

    public Faculty login(String email, String rawPassword) {
        Faculty faculty = findByEmail(email);
        if (faculty != null && passwordEncoder.matches(rawPassword, faculty.getPassword())) {
            return faculty;
        }
        return null;
    }

    public boolean passwordMatches(Faculty f, String raw) {
        return f != null && passwordEncoder.matches(raw, f.getPassword());
    }

    public String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    public Faculty save(Faculty f) {
        return facultyRepository.save(f);
    }

    /* ================= Admin Helpers ================= */

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Optional<Faculty> findById(String id) {
        return facultyRepository.findById(id);
    }

    public void deleteById(String id) {
        facultyRepository.deleteById(id);
    }
}
