package com.example.apcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.apcproject.model.Student;
import com.example.apcproject.repository.StudentRepository;

@Controller
@RequestMapping("/students")
@SessionAttributes("student")
public class ChangePasswordController {

    private final StudentRepository studentRepo;

    public ChangePasswordController(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    @GetMapping("/change-password")
    public String changePasswordForm(@SessionAttribute(value = "student", required = false) Student student) {
        if (student == null) {
            return "redirect:/"; // Not logged in
        }
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@SessionAttribute(value = "student", required = false) Student student,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 SessionStatus status,
                                 Model model) {
        if (student == null) {
            return "redirect:/"; // Not logged in
        }

        if (!student.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Old password is incorrect");
            return "change-password";
        }

        student.setPassword(newPassword);
        studentRepo.save(student);

        status.setComplete(); 
        return "redirect:/"; 
}
}