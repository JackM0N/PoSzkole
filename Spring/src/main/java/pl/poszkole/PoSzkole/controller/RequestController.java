package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.model.Class;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.service.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final StudentClassService studentClassService;

    private WebsiteUserRepository websiteUserRepository;

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
            WebsiteUser websiteUser = websiteUserRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            Teacher teacher = teacherService.getTeacherByIdUser(websiteUser);

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