package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.ArrayList;
import java.util.List;

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
    private final UserBusyDayService userBusyDayService;
    private final TutoringClassService tutoringClassService;

    public Page<RequestDTO> getRequestsForTeacher(Boolean gotAdmitted,
                                                  Subject subject,
                                                  Pageable pageable) throws AccessDeniedException {
        // Get currently logged-in user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a teacher
        if (currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        // Check if the teacher has subjects assigned
        if (currentUser.getSubjects() == null || currentUser.getSubjects().isEmpty()) {
            throw new RuntimeException("Teacher has no subjects assigned");
        }

        //Find request that this teacher teaches and were not admitted yet
        Specification<Request> rSpec = getRequestSpecification(subject, currentUser, gotAdmitted);

        Page<Request> requests = requestRepository.findAll(rSpec, pageable);
        return requests.map(requestMapper::toDto);
    }

    private static Specification<Request> getRequestSpecification(Subject subject,
                                                                  WebsiteUser currentUser,
                                                                  boolean gotAdmitted) {
        Specification<Request> rSpec = (root, query, builder) -> root.get("subject").in(currentUser.getSubjects());

        if (gotAdmitted) {
            rSpec = rSpec.and((root, query, builder) -> builder.isNotNull(root.get("acceptanceDate")));
            rSpec = rSpec.and((root, query, builder) -> builder.equal(
                    root.get("teacher").get("id"), currentUser.getId())
            );
        } else {
            rSpec = rSpec.and((root, query, builder) -> builder.isNull(root.get("acceptanceDate")));
        }
        //Filter subjects if needed
        if (subject != null && subject.getId() != null) {
            rSpec = rSpec.and((root, query, builder) -> builder.equal(root.get("subject").get("id"), subject.getId()));
        }
        return rSpec;
    }

    @Transactional
    public RequestDTO createRequest(RequestDTO requestDTO) throws BadRequestException {
        //Sometimes material form instead of false gives null so this was made in order to fix that issue
        if (requestDTO.getPrefersIndividual() == null){
            requestDTO.setPrefersIndividual(false);
        }
        //Creating new request
        Request request = requestMapper.toEntity(requestDTO);

        if (request.getRepeatUntil() != null && !request.getRepeatUntil().isAfter(LocalDate.now())) {
            throw new BadRequestException("You cant plan classes into the past");
        }

        //Set manually needed data
        WebsiteUser studentUser = websiteUserRepository.findById(requestDTO.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can't create a class for a user that's not a student");
        }

        request.setStudent(studentUser);
        request.setSubject(subjectRepository.findById(requestDTO.getSubject().getId())
                .orElseThrow(() -> new RuntimeException("Subject not found")));
        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    @Transactional
    public RequestDTO admitRequestCreateClass(Long requestId, TutoringClassDTO tutoringClassDTO,
                                   DayAndTimeDTO dayAndTimeDTO, Boolean isOnline) {
        //Validation
        if (dayAndTimeDTO.getTimeTo().isBefore(dayAndTimeDTO.getTimeFrom())){
            throw new RuntimeException("Invalid time values");
        }
        if (dayAndTimeDTO.getTimeTo().minusMinutes(60).isBefore(dayAndTimeDTO.getTimeFrom())){
            throw new RuntimeException("Class has to last at least 60 minutes");
        }
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Admit chosen request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
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

        if (userBusyDayService.isOverlapping(student, null, dayAndTimeDTO.getDay(), dayAndTimeDTO.getTimeFrom(), dayAndTimeDTO.getTimeTo())) {
            throw new RuntimeException("You cannot admit class on users busy day");
        }

        //Create class schedule
        if (request.getRepeatUntil() == null) {
            classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, student.getId());
        } else {
            List<WebsiteUser> students = new ArrayList<>();
            students.add(student);
            classScheduleService
                    .createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, request.getRepeatUntil(), students);
        }
        return requestMapper.toDto(request);
    }

    @Transactional
    public RequestDTO admitRequestAddToClass(Long requestId, Long classId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        TutoringClass tutoringClass = tutoringClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Tutoring class not found"));

        //Add student to a chosen tutoringClass
        tutoringClassService.addToTutoringClass(request.getStudent().getId(), tutoringClass.getId());

        //Admit request
        request.setAcceptanceDate(LocalDate.now());
        request.setTeacher(currentUser);

        requestRepository.save(request);
        return requestMapper.toDto(request);
    }
}
