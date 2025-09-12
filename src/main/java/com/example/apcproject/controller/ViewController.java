package com.example.apcproject.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.apcproject.model.Answer;
import com.example.apcproject.model.AnswerListWrapper;
import com.example.apcproject.model.Exam;
import com.example.apcproject.model.Faculty;
import com.example.apcproject.model.Question;
import com.example.apcproject.model.Result;
import com.example.apcproject.model.Student;
import com.example.apcproject.repository.QuestionRepository;
import com.example.apcproject.service.FacultyService;
import com.example.apcproject.service.StudentService;
// import com.example.apcproject.repository.QuestionRepository;
@Controller
@SessionAttributes("student")
public class ViewController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final QuestionRepository questionRepository;


    public ViewController(StudentService studentService,
            FacultyService facultyService,
            QuestionRepository questionRepository) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.questionRepository = questionRepository;
    }

    /* ---------- Common ---------- */

    @GetMapping("/")
    public String home() {
        return "login";
    }

    /* ---------- Student Auth ---------- */

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

    @PostMapping("/login")
    public String login(@RequestParam String identifier,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {

        if ("student".equals(role)) {
            Student student = studentService.loginByIdentifier(identifier, password);
            if (student != null) {
                model.addAttribute("student", student);
                model.addAttribute("exams", facultyService.getExams());
                return "redirect:/students/exams"; // goes to src/main/resources/templates/exam-list.html
            } else {
                model.addAttribute("error", "Invalid student credentials");
                return "login";
            }
        }

        if ("faculty".equals(role)) {
            Faculty faculty = facultyService.login(identifier, password);
            if (faculty != null) {
                model.addAttribute("faculty", faculty);
                return "redirect:/faculty/exams/createExam";
            } else {
                model.addAttribute("error", "Invalid faculty credentials");
                return "login";
            }
        }

        model.addAttribute("error", "Invalid role selected");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(org.springframework.web.bind.support.SessionStatus status) {
        status.setComplete(); // clear @SessionAttributes
        return "redirect:/";
    }

    /* ---------- Faculty Auth ---------- */

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
            model.addAttribute("examId", examId);
            model.addAttribute("exam",
                facultyService.getExams().stream()
                    .filter(e -> e.getId().equals(examId)).findFirst().orElse(null));
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
                              @ModelAttribute Question question,
                              Model model) {
    
        // ⚡ Force it to be a NEW record
        question.setId(null);   // <---- THIS is the fix
        question.setExamId(id);
    
        facultyService.addQuestion(id, question);
    
        // Reload all questions for this exam
        List<Question> allQuestions = questionRepository.findByExamId(id);
    
        model.addAttribute("examId", id);
        model.addAttribute("exam", facultyService.getExams()
                .stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null));
        model.addAttribute("question", new Question());
        model.addAttribute("questions", allQuestions);
    
        return "createExam";
    }
    
    
    

    
    

    /* ---------- Student Dashboard / Exams ---------- */

    @GetMapping("/students/results")
    public String viewResults(@SessionAttribute("student") Student student, Model model) {
        model.addAttribute("results", studentService.getResultsForStudent(student.getId()));
        return "student-results";
    }

    @GetMapping("/students/exams/{id}/start")
    public String startExam(@PathVariable String id,
            @SessionAttribute("student") Student student,
            Model model) {

        Exam exam = facultyService.getExams().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst().orElse(null);

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
        AnswerListWrapper wrapper = new AnswerListWrapper(answers);

        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("wrapper", wrapper);

        return "student-exam-page";
    }

    @PostMapping("/students/exams/{id}/submit")
    public String submitExam(@PathVariable String id,
            @ModelAttribute("wrapper") AnswerListWrapper wrapper,
            @SessionAttribute("student") Student student,
            Model model) {

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

        model.addAttribute("message", "Exam submitted successfully!");
        model.addAttribute("answers", answers);
        model.addAttribute("result", result);

        return "exam-submitted";
    }

    @GetMapping("/students/exams")
    public String listExams(@SessionAttribute("student") Student student, Model model) {
        model.addAttribute("student", student);
        model.addAttribute("exams", facultyService.getExams());
        return "exam-list"; // This maps to src/main/resources/templates/exam-list.html
    }
// -------------------- EDIT QUESTION --------------------
@PostMapping("/faculty/questions/{id}/edit")
public String editQuestion(@PathVariable String id,
                           @RequestParam String questionText,
                           @RequestParam String options,
                           @RequestParam String correctAnswer) {

    Question q = questionRepository.findById(id).orElse(null);
    if (q != null) {
        q.setQuestionText(questionText);
        q.setOptions(options);
        q.setCorrectAnswer(correctAnswer);
        questionRepository.save(q);
        return "redirect:/faculty/exams/createExam?examId=" + q.getExamId();
    }
    return "redirect:/faculty/exams/createExam";
}

// -------------------- DELETE QUESTION --------------------
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


}
