package com.example.apcproject.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.apcproject.model.Announcement;

@Repository
public class JdbcAnnouncementDao {

    private final JdbcTemplate jdbc;

    public JdbcAnnouncementDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Announcement> findAll() {
        return jdbc.query("SELECT id, title, message FROM announcements", new BeanPropertyRowMapper<>(Announcement.class));
    }
}


