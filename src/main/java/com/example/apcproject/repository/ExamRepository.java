package com.example.apcproject.repository;

import com.example.apcproject.model.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface ExamRepository extends MongoRepository<Exam, String> {


}
