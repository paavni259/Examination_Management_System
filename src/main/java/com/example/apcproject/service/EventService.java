package com.example.apcproject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.apcproject.model.Event;
import com.example.apcproject.repository.EventRepository;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
    
    public List<Event> getPublicEvents() {
        return eventRepository.findByIsPublicTrueOrderByStartTimeAsc();
    }
    
    public List<Event> getEventsByOrganizer(String organizerEmail) {
        return eventRepository.findByOrganizerEmailOrderByStartTimeAsc(organizerEmail);
    }
    
    public Event getEventById(String id) {
        return eventRepository.findById(id).orElse(null);
    }
    
    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
}
