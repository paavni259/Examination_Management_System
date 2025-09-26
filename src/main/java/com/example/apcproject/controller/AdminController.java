package com.example.apcproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Student;
import com.example.apcproject.service.AdminService;
import com.example.apcproject.service.FacultyService;
import com.example.apcproject.service.StudentService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final AdminService adminService;

    public AdminController(StudentService studentService,
                           FacultyService facultyService,
                           AdminService adminService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.adminService = adminService;
    }

    // ---------------- Dashboard ----------------
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        model.addAttribute("totalStudents", studentService.getAllStudents().size());
        model.addAttribute("totalFaculty", facultyService.getAllFaculty().size());
        model.addAttribute("adminName", auth != null ? auth.getName() : "Admin");
        model.addAttribute("recentStudents", studentService.getAllStudents());
        return "admin-dashboard";
    }

    // ---------------- Students ----------------
    @GetMapping("/students")
    public String manageStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin-students";
    }

    @GetMapping("/students/add")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "admin-student-form";
    }

    @PostMapping("/students/add")
    public String saveStudent(@Valid @ModelAttribute Student student, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("student", student);
            return "admin-student-form";
        }
        studentService.save(student);
        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable String id, Model model) {
        model.addAttribute("student", studentService.findById(id).orElse(new Student()));
        return "admin-student-form";
    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable String id, @Valid @ModelAttribute Student student, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("student", student);
            return "admin-student-form";
        }
        student.setId(id);
        studentService.save(student);
        return "redirect:/admin/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable String id) {
        studentService.deleteById(id);
        return "redirect:/admin/students";
    }

    // ---------------- Faculty ----------------
    @GetMapping("/faculty")
    public String manageFaculty(Model model) {
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        return "admin-faculty";
    }

    @GetMapping("/faculty/add")
    public String addFacultyForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "admin-faculty-form";
    }

    @PostMapping("/faculty/add")
    public String saveFaculty(@Valid @ModelAttribute Faculty faculty, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("faculty", faculty);
            return "admin-faculty-form";
        }
        facultyService.save(faculty);
        return "redirect:/admin/faculty";
    }

    @GetMapping("/faculty/edit/{id}")
    public String editFacultyForm(@PathVariable String id, Model model) {
        model.addAttribute("faculty", facultyService.findById(id).orElse(new Faculty()));
        return "admin-faculty-form";
    }

    @PostMapping("/faculty/edit/{id}")
    public String updateFaculty(@PathVariable String id, @Valid @ModelAttribute Faculty faculty, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("faculty", faculty);
            return "admin-faculty-form";
        }
        faculty.setId(id);
        facultyService.save(faculty);
        return "redirect:/admin/faculty";
    }

    @GetMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable String id) {
        facultyService.deleteById(id);
        return "redirect:/admin/faculty";
    }
}
