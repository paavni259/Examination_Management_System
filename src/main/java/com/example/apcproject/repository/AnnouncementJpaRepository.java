package com.example.apcproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.apcproject.model.Announcement;

public interface AnnouncementJpaRepository extends JpaRepository<Announcement, Long> {
}


