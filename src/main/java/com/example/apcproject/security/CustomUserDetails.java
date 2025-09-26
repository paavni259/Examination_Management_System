package com.example.apcproject.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Student;

/**
 * Simple bridge from your Mongo documents to Spring Security.
 */
public class CustomUserDetails implements UserDetails {
    private final String id;
    private final String username; // email or rollNumber (for students) / email (for faculty)
    private final String passwordHash;
    private final String role;     // "ROLE_STUDENT" or "ROLE_FACULTY"

    public CustomUserDetails(Student s) {
        this.id = s.getId();
        this.username = s.getEmail() != null ? s.getEmail() : s.getRollNumber();
        this.passwordHash = s.getPassword();
        this.role = "ROLE_STUDENT";
    }

    public CustomUserDetails(Faculty f) {
        this.id = f.getId();
        this.username = f.getEmail();
        this.passwordHash = f.getPassword();
        this.role = "ROLE_FACULTY";
    }

    public String getId() { return id; }
    public String getRole() { return role; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return username; }

    // Basic happy-path flags
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
