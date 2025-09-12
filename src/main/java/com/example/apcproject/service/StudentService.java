package com.example.apcproject.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public StudentService(StudentRepository studentRepo,
                          AnswerRepository answerRepo,
                          QuestionRepository questionRepo,
                          ResultRepository resultRepo) {
        this.studentRepo = studentRepo;
        this.answerRepo = answerRepo;
        this.questionRepo = questionRepo;
        this.resultRepo = resultRepo;
    }

    /* ------------ Auth & Registration ------------ */

    public Student register(Student s) {
        if (s.getEmail() != null && studentRepo.existsByEmail(s.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (s.getRollNumber() != null && studentRepo.existsByRollNumber(s.getRollNumber())) {
            throw new IllegalArgumentException("Roll number already registered");
        }
        return studentRepo.save(s);
    }

    /** Login by email OR roll number (identifier). */
    public Student loginByIdentifier(String identifier, String password) {
        Optional<Student> opt = identifier != null && identifier.contains("@")
                ? studentRepo.findByEmail(identifier)
                : studentRepo.findByRollNumber(identifier);

        return opt.filter(u -> Objects.equals(u.getPassword(), password)).orElse(null);
    }

    /** Keep this only if you still use email-only login in some controller paths. */
    public Student login(String email, String password) {
        return studentRepo.findByEmail(email)
                .filter(u -> Objects.equals(u.getPassword(), password))
                .orElse(null);
    }

    /* ------------ Exam Submission & Results ------------ */

    /**
     * Submit answers (list binding from Thymeleaf form).
     * - Ensures examId and studentId on each answer
     * - Auto-grades (5 points per correct)
     * - Upserts Result for (studentId, examId)
     */
    /* ------------ Exam Submission & Results ------------ */

/**
 * Overload used by REST controller: you already know examId & studentId.
 */
// public List<Answer> submitAnswers(List<Answer> answers, String examId, String studentId) {
//     if (answers == null || answers.isEmpty()) return List.of();

//     // Normalize IDs
//     for (Answer a : answers) {
//         if (a == null) continue;
//         a.setExamId(examId);
//         a.setStudentId(studentId);
//     }

//     // Fetch all referenced questions once
//     List<String> qIds = answers.stream()
//             .filter(Objects::nonNull)
//             .map(Answer::getQuestionId)
//             .filter(Objects::nonNull)
//             .distinct()
//             .toList();

//     Map<String, Question> questionById = questionRepo.findAllById(qIds).stream()
//             .collect(Collectors.toMap(Question::getId, q -> q));

//     // Grade: 5 points per exact (case-insensitive, trimmed) match
//     int total = 0;
//     for (Answer a : answers) {
//         if (a == null) continue;
//         Question q = questionById.get(a.getQuestionId());
//         int mark = 0;
//         if (q != null) {
//             String expected = safe(q.getCorrectAnswer());
//             String actual   = safe(a.getAnswerText());
//             if (!expected.isEmpty() && expected.equalsIgnoreCase(actual)) {
//                 mark = 5;
//             }
//         }
//         a.setMarks(mark);
//         total += mark;
//     }

//     // Save answers
//     List<Answer> saved = answerRepo.saveAll(answers);

//     // Upsert result
//     Result result = resultRepo.findByStudentIdAndExamId(studentId, examId).orElseGet(Result::new);
//     result.setStudentId(studentId);
//     result.setExamId(examId);
//     result.setMarks(total);
//     result.setStatus(total >= 40 ? "Pass" : "Fail");
//     resultRepo.save(result);

//     return saved;
// }

/**
 * Overload used by Thymeleaf form: studentId is inside each Answer (set in controller).
 * Returns saved answers.
 */
public List<Answer> submitAnswers(List<Answer> answers, String examId, String studentId) {
    if (answers == null || answers.isEmpty()) return List.of();

    // Normalize IDs
    for (Answer a : answers) {
        if (a == null) continue;
        a.setExamId(examId);
        a.setStudentId(studentId);
    }

    // Fetch all questions for this exam
    List<Question> questions = questionRepo.findByExamId(examId);
    Map<String, Question> questionById = questions.stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

    int correctCount = 0;
    for (Answer a : answers) {
        if (a == null) continue;
        Question q = questionById.get(a.getQuestionId());
        int mark = 0;
        if (q != null) {
            String expected = safe(q.getCorrectAnswer());
            String actual   = safe(a.getAnswerText());
            if (!expected.isEmpty() && expected.equalsIgnoreCase(actual)) {
                mark = 1; // give 1 mark for each correct
                correctCount++;
            }
        }
        a.setMarks(mark);
    }

    // Save all answers
    List<Answer> saved = answerRepo.saveAll(answers);

    // Calculate pass/fail dynamically based on total questions
    int totalQuestions = questions.size();
    double percentage = (correctCount * 100.0) / totalQuestions;

    Result result = resultRepo.findByStudentIdAndExamId(studentId, examId).orElseGet(Result::new);
    result.setStudentId(studentId);
    result.setExamId(examId);
    result.setMarks(correctCount);
    result.setStatus(percentage >= 40 ? "Pass" : "Fail");
    resultRepo.save(result);

    return saved;
}


/* ------------ Helpers ------------ */

private static String safe(String s) {
    return s == null ? "" : s.trim();
}


    public Optional<Result> getResult(String studentId, String examId) {
        return resultRepo.findByStudentIdAndExamId(studentId, examId);
    }

    public List<Result> getResultsForStudent(String studentId) {
        return resultRepo.findByStudentId(studentId);
    }

}
