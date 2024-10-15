package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
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
    private final ClassScheduleService classScheduleService;

    @Transactional
    public Page<RequestDTO> getRequestsForTeacher(Subject subject, Pageable pageable) throws AccessDeniedException {
        // Get currently logged-in user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        // Check if the teacher has subjects assigned
        if (currentUser.getSubjects() == null || currentUser.getSubjects().isEmpty()) {
            throw new IllegalArgumentException("Teacher has no subjects assigned");
        }

        //Find request that this teacher teaches and were not admitted yet
        Specification<Request> rSpec = getRequestSpecification(subject, currentUser);

        Page<Request> requests = requestRepository.findAll(rSpec, pageable);
        return requests.map(requestMapper::toDto);
    }

    private static Specification<Request> getRequestSpecification(Subject subject, WebsiteUser currentUser) {
        Specification<Request> rSpec = (root, query, builder) -> root.get("subject").in(currentUser.getSubjects());
        rSpec = rSpec.and((root, query, builder) -> builder.isNull(root.get("acceptanceDate")));

        //Filter subjects if needed
        if (subject != null && subject.getId() != null) {
            rSpec = rSpec.and((root, query, builder) -> builder.equal(root.get("subject").get("id"), subject.getId()));
        }
        return rSpec;
    }

    @Transactional
    public RequestDTO createRequest(RequestDTO requestDTO) throws BadRequestException {
        //Creating new request
        Request request = requestMapper.toEntity(requestDTO);

        if (!request.getRepeatUntil().isAfter(LocalDate.now())){
            throw new BadRequestException("You cant plan classes into the past");
        }

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
    public RequestDTO admitRequest(Long id, TutoringClassDTO tutoringClassDTO,
                                   DayAndTimeDTO dayAndTimeDTO, Boolean isOnline) {
        //TODO: MAYBE, JUST MAYBE SOMEDAY ADD INSTANT ROOM RESERVATION... that would be a 3rd dto tho...
        //TODO: Ask if classes are usually a group ones. If so should it be possible to add student to existing class
        //TODO: If so maybe add wants_individual to requests maybe
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Admit chosen request
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setAcceptanceDate(LocalDate.now());
        request.setTeacher(currentUser);
        
        //Create new class
        TutoringClass tutoringClass = tutoringClassMapper.toEntity(tutoringClassDTO);
        tutoringClass.setTeacher(currentUser);
        tutoringClass.setSubject(request.getSubject());
        tutoringClassRepository.save(tutoringClass);

        //Save request and created class
        request.setTutoringClass(tutoringClass);
        requestRepository.save(request);

        //Add student to created class
        WebsiteUser student = websiteUserRepository.findById(request.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.addClass(tutoringClass);
        websiteUserRepository.save(student);

        //Create class schedule
        if(request.getRepeatUntil() == null) {
            classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, isOnline);
        }else{
            classScheduleService
                    .createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, request.getRepeatUntil());
        }
        return requestMapper.toDto(request);
    }
}
