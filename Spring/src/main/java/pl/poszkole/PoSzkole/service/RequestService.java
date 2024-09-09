package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.mapper.RequestMapper;
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

    @Transactional
    public Page<RequestDTO> getRequestsForTeacher(Subject subject, Pageable pageable) {
        // Get currently logged-in user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a teacher
        Teacher teacher = teacherRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Specification<Request> rSpec = (root, query, builder) -> root.get("subject").in(teacher.getSubjects());

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
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    public Request findById(Long id) {
        return requestRepository.findById(id).orElseThrow();
    }
}
