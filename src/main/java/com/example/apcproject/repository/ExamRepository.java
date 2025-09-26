package com.example.apcproject.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.apcproject.model.Exam;

public interface ExamRepository extends MongoRepository<Exam, String> {

    // Optional: find exams by date
    List<Exam> findByDate(LocalDate date);

    // Optional: find exams after a certain date
    List<Exam> findByDateAfter(LocalDate date);
}
