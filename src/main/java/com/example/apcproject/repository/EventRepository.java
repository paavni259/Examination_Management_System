package com.example.apcproject.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.apcproject.model.Event;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByIsPublicTrueOrderByStartTimeAsc();
    List<Event> findByOrganizerEmailOrderByStartTimeAsc(String organizerEmail);
}
