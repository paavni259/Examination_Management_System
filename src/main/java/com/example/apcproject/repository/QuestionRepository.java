package com.example.apcproject.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.apcproject.model.Question;

public interface QuestionRepository extends MongoRepository<Question, String> {

List<Question> findByExamId(String examId);


}
