package com.example.apcproject.repository;

import com.example.apcproject.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface AnswerRepository extends MongoRepository<Answer, String> {


}
