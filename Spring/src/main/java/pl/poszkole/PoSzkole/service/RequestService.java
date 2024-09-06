package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.mapper.RequestMapper;
import pl.poszkole.PoSzkole.model.Request;
import pl.poszkole.PoSzkole.model.Teacher;
import pl.poszkole.PoSzkole.model.TeacherRequest;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.*;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherRequestRepository teacherRequestRepository;
    private final WebsiteUserRepository websiteUserRepository;
    private final RequestMapper requestMapper;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public List<Request> getRequestsForTeacher(String username) {
        WebsiteUser user = websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Teacher teacher = teacherRepository.findByIdUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return requestRepository.findAllByTeacherId(teacher.getId());
    }

    @Transactional
    public RequestDTO createRequest(RequestDTO requestDTO) {
        Request request = requestMapper.toEntity(requestDTO);

        request.setStudent(studentRepository.findById(requestDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found")));
        request.setSubject(subjectRepository.findById(requestDTO.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    @Transactional
    public void addTeacherRequest(Request request, Teacher teacher) {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setIdRequest(request);
        teacherRequest.setIdTeacher(teacher);
        teacherRequestRepository.save(teacherRequest);
    }

    @Transactional
    public void approveRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        request.setAdmissionDate(LocalDate.now());
        requestRepository.save(request);
    }

    public Request findById(Long id) {
        return requestRepository.findById(id).orElseThrow();
    }
}
