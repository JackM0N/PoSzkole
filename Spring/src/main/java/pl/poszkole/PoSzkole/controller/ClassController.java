package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.model.Class;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.service.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClassController {
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final RequestService requestService;
    private final ClassService classService;

    WebsiteUserRepository websiteUserRepository;

    @GetMapping("/classes/{username}")
    public String classes(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String username, Model model) throws BadRequestException {
        WebsiteUser user = websiteUserRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("Username " + username + " not found"));
        Student student = studentService.getStudentByIdUser(user);
        List<Class> classes = classService.getClassesForStudent(student.getId());
        model.addAttribute("classes", classes);
        model.addAttribute("username", userDetails.getUsername());
        return "classes";
    }

    @GetMapping("/classes/create")
    public String showCreateClassForm(Model model) {
        List<Student> students = studentService.getAllStudents();
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("students", students);
        model.addAttribute("subjects", subjects);
        return "createClass";
    }

    @PostMapping("/classes/create")
    public String createClass(@RequestParam("student") Long studentId, @RequestParam("subject") Long subjectId) {
        // Znajdź ucznia i przedmiot na podstawie ich ID
        Student student = studentService.getStudentById(studentId);
        Subject subject = subjectService.getSubjectById(subjectId);

        // Stwórz nowy request
        Request newRequest = new Request();
        newRequest.setStudent(student);
        newRequest.setSubject(subject);
        newRequest.setIssueDate(LocalDate.now());

        // Zapisz request do bazy danych
        requestService.saveRequest(newRequest);

        // Dodaj nauczycieli uczących przedmiotu do tabeli pośredniczącej
//        List<Teacher> teachers = teacherService.getTeachersBySubject(subject);
//        for (Teacher teacher : teachers) {
//            requestService.addTeacherRequest(newRequest, teacher);
//        }

        return "redirect:/home";
    }
}
