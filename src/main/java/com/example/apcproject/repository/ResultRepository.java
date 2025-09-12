package com.example.apcproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.apcproject.model.Result;

public interface ResultRepository extends MongoRepository<Result, String> {
    List<Result> findByExamId(String examId);
    Optional<Result> findByStudentIdAndExamId(String studentId, String examId);
    List<Result> findByStudentId(String studentId);
}

