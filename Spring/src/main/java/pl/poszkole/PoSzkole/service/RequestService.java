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

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final SubjectRepository subjectRepository;
    private final WebsiteUserService websiteUserService;
    private final TutoringClassMapper tutoringClassMapper;
    private final TutoringClassRepository tutoringClassRepository;
    private final WebsiteUserRepository websiteUserRepository;

    @Transactional
    public Page<RequestDTO> getRequestsForTeacher(Subject subject, Pageable pageable) throws AccessDeniedException {
        // Get currently logged-in user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        //Find request that this teacher teaches and were not admitted yet
        Specification<Request> rSpec = (root, query, builder) -> root.get("subject").in(currentUser.getSubjects());
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
        //Creating new request
        Request request = requestMapper.toEntity(requestDTO);

        //Set manually needed data
        WebsiteUser studentUser = websiteUserRepository.findById(requestDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can't create a class for a user that's not a student");
        }

        request.setStudent(studentUser);
        request.setSubject(subjectRepository.findById(requestDTO.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));
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
        request.setAcceptanceDate(LocalDate.now());
        request.setTeacher(currentUser);
        requestRepository.save(request);

        //Create new class
        TutoringClass tutoringClass = tutoringClassMapper.toEntity(tutoringClassDTO);
        tutoringClass.setTeacher(currentUser);
        tutoringClass.setSubject(request.getSubject());
        tutoringClassRepository.save(tutoringClass);

        //Add student to created class
        WebsiteUser student = websiteUserRepository.findById(request.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.addClass(tutoringClass);

        return requestMapper.toDto(request);
    }
}
