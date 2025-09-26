package com.example.apcproject.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.apcproject.model.Answer;
import com.example.apcproject.model.AnswerListWrapper;
import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.model.Student;
import com.example.apcproject.repository.QuestionRepository;
import com.example.apcproject.service.FacultyService;
import com.example.apcproject.service.QuestionImportService;
import com.example.apcproject.service.StudentService;

@Controller
public class ViewController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final QuestionRepository questionRepository;
    private final QuestionImportService importService;

    public ViewController(StudentService studentService,
                          FacultyService facultyService,
                          QuestionRepository questionRepository,
                          QuestionImportService importService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.questionRepository = questionRepository;
        this.importService = importService;
    }

    /* ---------- Common ---------- */

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /** After successful Spring Security login, redirect by role */
    @GetMapping("/role-redirect")
    public String redirectByRole(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isFaculty = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        } else if (isFaculty) {
            return "redirect:/faculty/exams/createExam";
        }

        // student role
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        Student current = studentService.findByEmailOrRoll(username);
        if (current != null) {
            model.addAttribute("student", current);
        }

        return "redirect:/students/exams";
    }

    /* ---------- Student Registration ---------- */

    @GetMapping("/students/register-form")
    public String registerForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-register";
    }

    @PostMapping("/students/register-form")
    public String registerStudent(@ModelAttribute Student student, Model model) {
        Student saved = studentService.register(student);
        model.addAttribute("student", saved);
        return "register-success";
    }

    /* ---------- Change Password ---------- */

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleChangePassword(
            Authentication auth,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Model model) {

        String username = auth.getName();
        boolean isFaculty = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY"));

        if (isFaculty) {
            Faculty faculty = facultyService.findByEmail(username);
            if (faculty == null || !facultyService.passwordMatches(faculty, oldPassword)) {
                model.addAttribute("error", "Old password is incorrect.");
                return "change-password";
            }
            faculty.setPassword(facultyService.encodePassword(newPassword));
            facultyService.save(faculty);
        } else {
            Student student = studentService.findByEmailOrRoll(username);
            if (student == null || !studentService.passwordMatches(student, oldPassword)) {
                model.addAttribute("error", "Old password is incorrect.");
                return "change-password";
            }
            student.setPassword(studentService.encodePassword(newPassword));
            studentService.save(student);
        }

        model.addAttribute("message", "Password changed successfully!");
        return "redirect:/students/exams";
    }

    /* ---------- Faculty Registration ---------- */

    @GetMapping("/faculty/register-form")
    public String registerFacultyForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "faculty-register";
    }

    @PostMapping("/faculty/register-form")
    public String registerFaculty(@ModelAttribute Faculty faculty, Model model) {
        Faculty saved = facultyService.register(faculty);
        model.addAttribute("faculty", saved);
        return "faculty-register-success";
    }

    /* ---------- Faculty Create Exam ---------- */

    @GetMapping("/faculty/exams/createExam")
    public String showCreateExamPage(@RequestParam(required = false) String examId, Model model) {
        if (examId != null) {
            Exam exam = facultyService.getExams().stream()
                    .filter(e -> e.getId().equals(examId))
                    .findFirst()
                    .orElse(null);

            model.addAttribute("examId", examId);
            model.addAttribute("exam", exam);
            model.addAttribute("questions", questionRepository.findByExamId(examId));
        } else {
            model.addAttribute("exam", new Exam());
            model.addAttribute("questions", List.of());
        }
        model.addAttribute("question", new Question());
        return "createExam";
    }

    @PostMapping("/faculty/exams")
    public String createExam(@ModelAttribute Exam exam) {
        Exam savedExam = facultyService.createExam(exam);
        return "redirect:/faculty/exams/createExam?examId=" + savedExam.getId();
    }

    @PostMapping("/faculty/exams/{id}/questions")
    public String addQuestion(@PathVariable String id,
                              @ModelAttribute Question question) {
        facultyService.addQuestion(id, question);
        return "redirect:/faculty/exams/createExam?examId=" + id;
    }

    @PostMapping("/faculty/exams/{id}/questions/upload")
    public String uploadQuestions(@PathVariable String id,
                                  @RequestParam("file") MultipartFile file) {
        var parsed = importService.parseFile(file);
        facultyService.addQuestions(id, parsed);
        return "redirect:/faculty/exams/createExam?examId=" + id;
    }

    /* ---------- Student: results & exams ---------- */

    @GetMapping("/students/results")
    public String viewResults(Authentication auth, Model model) {
        Student student = studentService.findByEmailOrRoll(auth.getName());
        if (student == null) return "redirect:/login";
        model.addAttribute("student", student);
        model.addAttribute("results", studentService.getResultsForStudent(student.getId()));
        return "student-results";
    }

    @GetMapping("/students/exams")
    public String listExams(Authentication auth, Model model) {
        Student student = studentService.findByEmailOrRoll(auth.getName());
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        List<Exam> exams = facultyService.getExams();
        if (exams == null) exams = List.of(); // prevent NPE
        model.addAttribute("exams", exams);

        return "exam-list";
    }

    @GetMapping("/students/exams/{id}/start")
    public String startExam(@PathVariable String id, Authentication auth, Model model) {
        Student student = studentService.findByEmailOrRoll(auth.getName());
        if (student == null) return "redirect:/login";

        Exam exam = facultyService.getExams().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst()
                .orElse(null);

        if (exam == null) {
            model.addAttribute("message", "Exam not found.");
            return "exam-submitted";
        }

        List<Question> questions = questionRepository.findByExamId(id);
        if (questions.isEmpty()) {
            model.addAttribute("message", "No questions available for this exam yet.");
            return "exam-submitted";
        }

        List<Answer> answers = new ArrayList<>();
        for (Question q : questions) {
            Answer a = new Answer();
            a.setQuestionId(q.getId());
            a.setExamId(id);
            a.setStudentId(student.getId());
            answers.add(a);
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("wrapper", new AnswerListWrapper(answers));
        return "student-exam-page";
    }

    // @PostMapping("/students/exams/{id}/submit")
    // public String submitExam(@PathVariable String id,
    //                          @ModelAttribute("wrapper") AnswerListWrapper wrapper,
    //                          Authentication auth,
    //                          Model model) {
    //     Student student = studentService.findByEmailOrRoll(auth.getName());
    //     if (student == null) return "redirect:/login";

    //     List<Answer> answers = wrapper.getAnswers();
    //     if (answers == null || answers.isEmpty()) {
    //         model.addAttribute("message", "No answers submitted.");
    //         return "exam-submitted";
    //     }

    //     for (Answer a : answers) {
    //         a.setStudentId(student.getId());
    //         a.setExamId(id);
    //     }
    //     studentService.submitAnswers(answers, id, student.getId());

    //     Result result = studentService.getResult(student.getId(), id).orElse(null);
    //     model.addAttribute("message", "Exam submitted successfully!");
    //     model.addAttribute("answers", answers);
    //     model.addAttribute("result", result);
    //     return "exam-submitted";
    // }

 @PostMapping("/students/exams/{id}/submit")
public String submitExam(@PathVariable String id,
                         @ModelAttribute("wrapper") AnswerListWrapper wrapper,
                         Authentication auth,
                         Model model) {
    Student student = studentService.findByEmailOrRoll(auth.getName());
    if (student == null) return "redirect:/login";

    List<Answer> answers = wrapper.getAnswers();
    if (answers == null || answers.isEmpty()) {
        model.addAttribute("message", "No answers submitted.");
        return "exam-submitted";
    }

    for (Answer a : answers) {
        a.setStudentId(student.getId());
        a.setExamId(id);
    }

    studentService.submitAnswers(answers, id, student.getId());
Result result = studentService.getResult(student.getId(), id).orElse(null);
int totalMarks = answers.size(); // or fetch from result.getTotalMarks()

model.addAttribute("message", "Exam submitted successfully!");
model.addAttribute("answers", answers);
model.addAttribute("result", result);
model.addAttribute("totalMarks", totalMarks); // ✅ so view can use it

    return "exam-submitted";
}



    /* ---------- Edit/Delete Questions ---------- */

    @GetMapping("/faculty/questions/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q == null) return "redirect:/faculty/exams/createExam";
        model.addAttribute("question", q);
        return "edit-question";
    }

    @PostMapping("/faculty/questions/{id}/edit")
    public String updateQuestion(@PathVariable String id,
                                 @ModelAttribute Question question) {
        Question existing = questionRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setQuestionText(question.getQuestionText());
            existing.setOptions(question.getOptions());
            existing.setCorrectAnswer(question.getCorrectAnswer());
            questionRepository.save(existing);
            return "redirect:/faculty/exams/createExam?examId=" + existing.getExamId();
        }
        return "redirect:/faculty/exams/createExam";
    }

    @GetMapping("/faculty/questions/{id}/delete")
    public String deleteQuestion(@PathVariable String id) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q != null) {
            String examId = q.getExamId();
            questionRepository.deleteById(id);
            return "redirect:/faculty/exams/createExam?examId=" + examId;
        }
        return "redirect:/faculty/exams/createExam";
    }

    @PostMapping("/faculty/exams/{id}/delete")
    public String deleteExam(@PathVariable String id) {
        facultyService.deleteExam(id);
        return "redirect:/faculty/exams/createExam";
    }

    /* ---------- Logout ---------- */

    @GetMapping("/logout")
    public String logout(org.springframework.web.bind.support.SessionStatus status) {
        status.setComplete();
        return "redirect:/login";
    }
}
