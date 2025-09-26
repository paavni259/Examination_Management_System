package com.example.apcproject.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.apcproject.model.Admin;
import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Student;
import com.example.apcproject.repository.AdminRepository;
import com.example.apcproject.repository.FacultyRepository;
import com.example.apcproject.repository.StudentRepository;

/**
 * Custom implementation of UserDetailsService for Spring Security.
 *
 * Supports:
 *  - Students using either email or roll number
 *  - Faculty using email
 *  - Admin using email
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final StudentRepository studentRepo;
    private final FacultyRepository facultyRepo;
    private final AdminRepository adminRepo;

    public CustomUserDetailsService(StudentRepository studentRepo,
                                    FacultyRepository facultyRepo,
                                    AdminRepository adminRepo) {
        this.studentRepo = studentRepo;
        this.facultyRepo = facultyRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try Admin first
        Admin admin = adminRepo.findByEmail(username).orElse(null);
        if (admin != null) {
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")  // maps to ROLE_ADMIN
                    .build();
        }

        // Try Faculty
        Faculty faculty = facultyRepo.findByEmail(username).orElse(null);
        if (faculty != null) {
            return User.builder()
                    .username(faculty.getEmail())
                    .password(faculty.getPassword())
                    .roles("FACULTY")
                    .build();
        }

        // Try Student
        Student student = studentRepo.findByEmail(username)
                .orElseGet(() -> studentRepo.findByRollNumber(username).orElse(null));
        if (student != null) {
            return User.builder()
                    .username(student.getEmail() != null ? student.getEmail() : student.getRollNumber())
                    .password(student.getPassword())
                    .roles("STUDENT")
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
