package com.example.apcproject.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.apcproject.model.Answer;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.model.Student;
import com.example.apcproject.repository.AnswerRepository;
import com.example.apcproject.repository.QuestionRepository;
import com.example.apcproject.repository.ResultRepository;
import com.example.apcproject.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepo;
    private final AnswerRepository answerRepo;
    private final QuestionRepository questionRepo;
    private final ResultRepository resultRepo;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepo,
                          AnswerRepository answerRepo,
                          QuestionRepository questionRepo,
                          ResultRepository resultRepo,
                          PasswordEncoder passwordEncoder) {
        this.studentRepo = studentRepo;
        this.answerRepo = answerRepo;
        this.questionRepo = questionRepo;
        this.resultRepo = resultRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= Registration ================= */
    public Student register(Student s) {
        if (s.getEmail() != null && studentRepo.existsByEmail(s.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (s.getRollNumber() != null && studentRepo.existsByRollNumber(s.getRollNumber())) {
            throw new IllegalArgumentException("Roll number already registered");
        }
        s.setPassword(passwordEncoder.encode(s.getPassword()));
        return studentRepo.save(s);
    }

    /* ================= Login ================= */
    public Student loginByIdentifier(String identifier, String rawPassword) {
        Optional<Student> opt = (identifier != null && identifier.contains("@"))
                ? studentRepo.findByEmail(identifier)
                : studentRepo.findByRollNumber(identifier);

        return opt.filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                  .orElse(null);
    }

    /* ================= Spring Security Helpers ================= */
    public Student findByEmailOrRoll(String username) {
        if (username == null) return null;
        if (username.contains("@")) {
            return studentRepo.findByEmail(username).orElse(null);
        } else {
            return studentRepo.findByRollNumber(username).orElse(null);
        }
    }

    public boolean passwordMatches(Student s, String raw) {
        return s != null && passwordEncoder.matches(raw, s.getPassword());
    }

    public String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    /* ================= Exam Submission & Results ================= */
    public Result submitAnswers(List<Answer> answers, String examId, String studentId) {
        // Check if student has already taken this exam
        if (resultRepo.findByStudentIdAndExamId(studentId, examId).isPresent()) {
            throw new IllegalStateException("You have already taken this exam. Each student can only take an exam once.");
        }
        
        int correctCount = 0;
        int totalQuestions = answers.size();

        for (Answer ans : answers) {
            Question q = questionRepo.findById(ans.getQuestionId()).orElse(null);

            if (q != null && q.getCorrectAnswer().equalsIgnoreCase(ans.getAnswerText())) {
                ans.setCorrect(true);
                ans.setMarks(1);
                correctCount++;
            } else {
                ans.setCorrect(false);
                ans.setMarks(0);
            }

            ans.setStudentId(studentId);
            ans.setExamId(examId);
            answerRepo.save(ans);
        }

        Result result = new Result();
        result.setStudentId(studentId);
        result.setExamId(examId);
        result.setMarks(correctCount);
        result.setTotalMarks(totalQuestions);
        result.setStatus(correctCount >= (totalQuestions / 2) ? "Pass" : "Fail");

        return resultRepo.save(result);
    }

    public Optional<Result> getResult(String studentId, String examId) {
        return resultRepo.findByStudentIdAndExamId(studentId, examId);
    }

    public List<Result> getResultsForStudent(String studentId) {
        return resultRepo.findByStudentId(studentId);
    }

    /* ================= Admin Helpers ================= */
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    public Optional<Student> findById(String id) {
        return studentRepo.findById(id);
    }

    public void deleteById(String id) {
        studentRepo.deleteById(id);
    }

    public Student save(Student s) {
        return studentRepo.save(s);
    }
}
