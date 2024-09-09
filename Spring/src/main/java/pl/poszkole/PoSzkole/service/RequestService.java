package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.mapper.RequestMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final TeacherRepository teacherRepository;
    private final RequestMapper requestMapper;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final WebsiteUserService websiteUserService;
    private final TutoringClassMapper tutoringClassMapper;
    private final TutoringClassRepository tutoringClassRepository;

    @Transactional
    public Page<RequestDTO> getRequestsForTeacher(Subject subject, Pageable pageable) {
        // Get currently logged-in user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a teacher
        Teacher teacher = teacherRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        //Find request that this teacher teaches and were not admitted yet
        Specification<Request> rSpec = (root, query, builder) -> root.get("subject").in(teacher.getSubjects());
        rSpec = rSpec.and((root, query, builder) -> builder.equal(root.get("admission_date"), null));

        //Filter subjects if needed
        if (subject.getId() != null) {
            rSpec = rSpec.and((root, query, builder) -> builder.equal(root.get("subject").get("id"), subject.getId()));
        }

        Page<Request> requests = requestRepository.findAll(rSpec, pageable);
        return requests.map(requestMapper::toDto);
    }

    @Transactional
    public RequestDTO createRequest(RequestDTO requestDTO) {
        //Manually creating new request because toEntity in a simple mapper won't be useful here
        Request request = new Request();

        //Set manually needed data
        request.setStudent(studentRepository.findById(requestDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found")));
        request.setSubject(subjectRepository.findById(requestDTO.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));
        request.setIssueDate(LocalDate.now());
        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    @Transactional
    public RequestDTO admitRequest(Long id, TutoringClassDTO tutoringClassDTO) {
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Admit chosen request
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setAdmissionDate(LocalDate.now());
        request.setTeacher(teacherRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found")));
        requestRepository.save(request);

        //Create new class
        TutoringClass tutoringClass = tutoringClassMapper.toEntity(tutoringClassDTO);
        tutoringClass.setTeacher(teacherRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found")));
        tutoringClass.setSubject(request.getSubject());
        tutoringClassRepository.save(tutoringClass);

        //Add student to created class
        Student student = studentRepository.findById(request.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.addClass(tutoringClass);

        return requestMapper.toDto(request);
    }
}
