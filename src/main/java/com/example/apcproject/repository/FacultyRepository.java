package com.example.apcproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.apcproject.model.Faculty;

@Repository
public interface FacultyRepository extends MongoRepository<Faculty, String> {
    Faculty findByEmail(String email);
}
