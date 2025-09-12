package com.example.apcproject.service;

import java.util.List;

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

    public FacultyService(ExamRepository examRepo,
            QuestionRepository questionRepo,
            ResultRepository resultRepo,
            FacultyRepository facultyRepository) {
        this.examRepo = examRepo;
        this.questionRepo = questionRepo;
        this.resultRepo = resultRepo;
        this.facultyRepository = facultyRepository;
    }

    // Exam Management
    public Exam createExam(Exam e) {
        return examRepo.save(e);
    }

    // keep only this one addQuestion method
    public Question addQuestion(String examId, Question q) {
        q.setExamId(examId);
        return questionRepo.save(q); // now it will always create new because id=null
    }
    

    public List<Exam> getExams() {
        return examRepo.findAll();
    }

    public List<Result> getResults(String examId) {
        return resultRepo.findByExamId(examId);
    }

    // Faculty Management
    public Faculty register(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty login(String email, String password) {
        Faculty faculty = facultyRepository.findByEmail(email);
        if (faculty != null && faculty.getPassword().equals(password)) {
            return faculty;
        }
        return null;
    }

}
