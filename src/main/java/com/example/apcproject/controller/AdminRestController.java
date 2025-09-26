package com.example.apcproject.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Student;
import com.example.apcproject.service.FacultyService;
import com.example.apcproject.service.StudentService;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    public AdminRestController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    /* -------- Students CRUD -------- */

    @GetMapping("/students")
    public List<Student> listStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student s) {
        Student saved = studentService.save(s);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student s) {
        s.setId(id);
        return ResponseEntity.ok(studentService.save(s));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /* -------- Faculty CRUD -------- */

    @GetMapping("/faculty")
    public List<Faculty> listFaculty() {
        return facultyService.getAllFaculty();
    }

    @PostMapping("/faculty")
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty f) {
        Faculty saved = facultyService.save(f);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/faculty/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable String id, @RequestBody Faculty f) {
        f.setId(id);
        return ResponseEntity.ok(facultyService.save(f));
    }

    @DeleteMapping("/faculty/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable String id) {
        facultyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


