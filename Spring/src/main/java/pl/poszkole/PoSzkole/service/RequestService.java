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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherRequestRepository teacherRequestRepository;
    private final RequestMapper requestMapper;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final WebsiteUserService websiteUserService;

    @Transactional
    public Page<RequestDTO> getRequestsForTeacher(Subject subject, Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        Teacher teacher = teacherRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<TeacherRequest> teacherRequests = teacherRequestRepository.findAll((root, query, builder) ->
                builder.equal(root.get("teacher"), teacher)
        );

        List<Long> requestIds = teacherRequests.stream()
                .map(tr -> tr.getRequest().getId())
                .collect(Collectors.toList());
        Specification<Request> rSpec = (root, query, builder) -> root.get("id").in(requestIds);

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

        //Create lists of teachers and requests
        List<Teacher> teachers = teacherRepository.findBySubjectsId(request.getSubject().getId());
        List<TeacherRequest> teacherRequests = new ArrayList<>();

        //Create requests for all teachers that teach given subject
        for (Teacher teacher : teachers) {
            TeacherRequest teacherRequest = new TeacherRequest();
            teacherRequest.setTeacher(teacher);
            teacherRequest.setRequest(request);
            teacherRequests.add(teacherRequest);
        }
        teacherRequestRepository.saveAll(teacherRequests);

        return requestMapper.toDto(request);
    }

    @Transactional
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    @Transactional
    public void addTeacherRequest(Request request, Teacher teacher) {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setRequest(request);
        teacherRequest.setTeacher(teacher);
        teacherRequestRepository.save(teacherRequest);
    }


    public Request findById(Long id) {
        return requestRepository.findById(id).orElseThrow();
    }
}
