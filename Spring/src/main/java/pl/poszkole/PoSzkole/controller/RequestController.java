package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.model.Class;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.service.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final StudentClassService studentClassService;

    private WebsiteUserRepository websiteUserRepository;

    @GetMapping("/list")
    public ResponseEntity<Page<RequestDTO>> getRequests(Subject subject, Pageable pageable) {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(subject, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
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
            request.setTeacher(teacher);

            System.out.println(request.getAdmissionDate());

            // Create a new class
            Class newClass = new Class();
            newClass.setIdTeacher(teacher);
            newClass.setIdSubject(request.getSubject());
            newClass.setName(request.getSubject().getName() + " - " + request.getStudent().getLastName());

            // Save the new class
            classService.saveClass(newClass);

            // Create a new student_class record
            StudentClass studentClass = new StudentClass();
            studentClass.setIdStudent(request.getStudent());
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