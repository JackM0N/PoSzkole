package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Request;
import pl.poszkole.PoSzkole.model.Teacher;
import pl.poszkole.PoSzkole.model.TeacherRequest;
import pl.poszkole.PoSzkole.model.Users;
import pl.poszkole.PoSzkole.repository.RequestRepository;
import pl.poszkole.PoSzkole.repository.TeacherRepository;
import pl.poszkole.PoSzkole.repository.TeacherRequestRepository;
import pl.poszkole.PoSzkole.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherRequestRepository teacherRequestRepository;
    private final UserRepository userRepository;

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
    public List<Request> getRequestsForTeacher(String username) {
        Users user = userRepository.findByUsername(username);
        Teacher teacher = teacherRepository.findByIdUser(user);
        return requestRepository.findAllByTeacherId(teacher.getId());
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
