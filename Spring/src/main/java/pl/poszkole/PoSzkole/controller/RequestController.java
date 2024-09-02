package pl.poszkole.PoSzkole.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.model.Class;
import pl.poszkole.PoSzkole.repository.UserRepository;
import pl.poszkole.PoSzkole.service.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    RequestService requestService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentClassService studentClassService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        List<Request> requests = requestService.getRequestsForTeacher(username);
        List<Student> students = studentService.getAllStudents();
        List<Subject> subjects = subjectService.getAllSubjects();

        for (Request request : requests) {
            Student student = students.stream()
                    .filter(s -> s.getId().equals(request.getIdStudent().getId()))
                    .findFirst()
                    .orElse(null);
            Subject subject = subjects.stream()
                    .filter(sub -> sub.getId().equals(request.getIdSubject().getId()))
                    .findFirst()
                    .orElse(null);
            request.setIdStudent(student);
            request.setIdSubject(subject);
        }

        model.addAttribute("requests", requests);
        return "requests";
    }

    @PostMapping("/approve/{id}")
    @ResponseBody
    public ResponseEntity<String> approveRequest(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Request request = requestService.findById(id);
            Users users = userRepository.findByUsername(userDetails.getUsername());
            Teacher teacher = teacherService.getTeacherByIdUser(users);

            System.out.println(request.getAdmissionDate());

            // Set the admission date and teacher
            request.setAdmissionDate(LocalDate.now());
            request.setIdTeacher(teacher);

            System.out.println(request.getAdmissionDate());

            // Create a new class
            Class newClass = new Class();
            newClass.setIdTeacher(teacher);
            newClass.setIdSubject(request.getIdSubject());
            newClass.setName(request.getIdSubject().getName() + " - " + request.getIdStudent().getLastName());

            // Save the new class
            classService.saveClass(newClass);

            // Create a new student_class record
            StudentClass studentClass = new StudentClass();
            studentClass.setIdStudent(request.getIdStudent());
            studentClass.setIdClass(newClass);

            // Save the student_class record
            studentClassService.save(studentClass);

            // Update the request
            requestService.saveRequest(request);

            return ResponseEntity.ok("Request approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to approve request: " + e.getMessage());
        }
    }
}