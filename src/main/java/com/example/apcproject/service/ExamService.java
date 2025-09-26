package com.example.apcproject.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.repository.ExamRepository;
import com.example.apcproject.repository.QuestionRepository;
import com.example.apcproject.repository.ResultRepository;

@Service
public class ExamService {

    private final ExamRepository examRepo;
    private final QuestionRepository questionRepo;
    private final ResultRepository resultRepo;

    public ExamService(ExamRepository examRepo,
                       QuestionRepository questionRepo,
                       ResultRepository resultRepo) {
        this.examRepo = examRepo;
        this.questionRepo = questionRepo;
        this.resultRepo = resultRepo;
    }

    /* ================= Exam Management ================= */

    public Exam createExam(Exam e) {
        return examRepo.save(e);
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

    public List<Exam> getExams() {
        return examRepo.findAll();
    }

    public void deleteExam(String examId) {
        examRepo.deleteById(examId);
        questionRepo.deleteAll(questionRepo.findByExamId(examId));
    }

    /* ================= Question Management ================= */

    public Question addQuestion(String examId, Question q) {
        q.setExamId(examId);
        return questionRepo.save(q);
    }

    public List<Question> addQuestions(String examId, List<Question> questions) {
        List<Question> toSave = new ArrayList<>();
        for (Question q : questions) {
            q.setExamId(examId);
            toSave.add(q);
        }
        return questionRepo.saveAll(toSave);
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

    public void deleteQuestion(String questionId) {
        questionRepo.deleteById(questionId);
    }

    public boolean examHasQuestions(String examId) {
        return !questionRepo.findByExamId(examId).isEmpty();
    }

    /* ================= Results ================= */

    public List<Result> getResults(String examId) {
        return resultRepo.findByExamId(examId);
    }
}
