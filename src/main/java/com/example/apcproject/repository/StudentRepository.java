package com.example.apcproject.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.apcproject.model.Student;

public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByRollNumber(String rollNumber);
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
}


