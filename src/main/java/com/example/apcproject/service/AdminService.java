package com.example.apcproject.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.apcproject.model.Admin;
import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Student;
import com.example.apcproject.repository.AdminRepository;

@Service
public class AdminService {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(StudentService studentService,
                        FacultyService facultyService,
                        AdminRepository adminRepository,
                        PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ---------- Student Management ---------- */

    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    public Student getStudentById(String id) {
        return studentService.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        return studentService.save(student);
    }

    public void deleteStudent(String id) {
        studentService.deleteById(id);
    }

    public void resetStudentPassword(String id, String newPassword) {
        Student s = studentService.findById(id).orElse(null);
        if (s != null) {
            s.setPassword(studentService.encodePassword(newPassword));
            studentService.save(s);
        }
    }

    /* ---------- Faculty Management ---------- */

    public List<Faculty> getAllFaculty() {
        return facultyService.getAllFaculty();
    }

    public Faculty getFacultyById(String id) {
        return facultyService.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        return facultyService.save(faculty);
    }

    public void deleteFaculty(String id) {
        facultyService.deleteById(id);
    }

    public void resetFacultyPassword(String id, String newPassword) {
        Faculty f = facultyService.findById(id).orElse(null);
        if (f != null) {
            f.setPassword(facultyService.encodePassword(newPassword));
            facultyService.save(f);
        }
    }

    /* ---------- Admin Management ---------- */

   public Admin register(Admin admin) {
    if (adminRepository.existsByEmail(admin.getEmail())) {
        throw new IllegalArgumentException("Email already registered");
    }
    admin.setPassword(passwordEncoder.encode(admin.getPassword())); // ✅ encode
    admin.setRole("ROLE_ADMIN");
    return adminRepository.save(admin);
}


    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    /* ---------- Stats ---------- */

    public long countStudents() {
        return studentService.getAllStudents().size();
    }

    public long countFaculty() {
        return facultyService.getAllFaculty().size();
    }
}
