package com.example.apcproject.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.apcproject.model.Announcement;
import com.example.apcproject.repository.AnnouncementJpaRepository;

@Service
public class AnnouncementService {

    private final AnnouncementJpaRepository repo;

    public AnnouncementService(AnnouncementJpaRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Announcement create(Announcement a) {
        return repo.save(a);
    }

    public List<Announcement> findAll() {
        return repo.findAll();
    }

    @Transactional
    public Announcement update(Long id, Announcement updated) {
        return repo.findById(id)
                .map(a -> {
                    a.setTitle(updated.getTitle());
                    a.setMessage(updated.getMessage());
                    return repo.save(a);
                })
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}


