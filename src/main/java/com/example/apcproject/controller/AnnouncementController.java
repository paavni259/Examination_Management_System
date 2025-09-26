package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.apcproject.model.Announcement;
import com.example.apcproject.service.AnnouncementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcements")
@Validated
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Announcement> create(@Valid @RequestBody Announcement a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(a));
    }

    @GetMapping
    public List<Announcement> list() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public Announcement update(@PathVariable Long id, @Valid @RequestBody Announcement a) {
        return service.update(id, a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


