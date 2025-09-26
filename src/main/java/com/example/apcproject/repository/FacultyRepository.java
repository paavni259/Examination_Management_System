package com.example.apcproject.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.apcproject.model.Faculty;

@Repository
public interface FacultyRepository extends MongoRepository<Faculty, String> {

    // Find faculty by email
    Optional<Faculty> findByEmail(String email);

    // Check if a faculty already exists by email
    boolean existsByEmail(String email);
}
