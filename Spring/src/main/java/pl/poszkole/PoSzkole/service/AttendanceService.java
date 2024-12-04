package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.filter.AttendanceFilter;
import pl.poszkole.PoSzkole.mapper.AttendanceMapper;
import pl.poszkole.PoSzkole.model.Attendance;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.AttendanceRepository;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final AttendanceMapper attendanceMapper;
    private final WebsiteUserService websiteUserService;

    public List<AttendanceDTO> findAllForClassSchedule(
            Long classScheduleId, AttendanceFilter attendanceFilter
    ) {
        //Check if classSchedule exists
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(EntityNotFoundException::new);

        //Get attendance for that schedule
        Specification<Attendance> spec = ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("classSchedule"), classSchedule));

        //Filter is_present
        if (attendanceFilter.getIsPresent() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("isPresent"), attendanceFilter.getIsPresent()));
        }

        //Filter students first or last name
        if (attendanceFilter.getTextSearch() != null){
            String likePattern = "%" + attendanceFilter.getTextSearch() + "%";
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("student").get("fistName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("student").get("lastName")), likePattern)
            )));
        }
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("student.firstName")));
        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);

        return attendances.stream().map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    public Page<AttendanceDTO> findAllAttendanceForStudent(String searchText, Pageable pageable, boolean isPresent){
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        //Get this students attendance
        Specification<Attendance> spec = ((root, query, builder) -> builder.equal(root.get("student").get("id"), currentUser.getId()));
        //Get only present positions
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isPresent"), isPresent));
        //Filter subjects
        if (searchText != null) {
            String likePattern = "%" + searchText.toLowerCase() + "%";
            spec = spec.and((root, query, builder) ->
                    builder.like(
                            builder.lower(root.get("classSchedule").get("tutoringClass").get("subject").get("subjectName")),
                            likePattern
                    )
            );
        }

        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);
        return attendances.map(attendanceMapper::toDto);
    }

    //This method is used to see if attendance exists for given classSchedule
    public Boolean checkIfExists(Long classScheduleId) {
        return attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId);
    }

    public Boolean createForClassSchedule(Long classScheduleId) {
        if (checkIfExists(classScheduleId)) {
            throw new EntityExistsException("Class attendance already exists");
        }

        //Check if classSchedule exists
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("This class schedule does not exist"));

        //For each student create and save attendance
        classSchedule.getTutoringClass().getStudents().forEach(student -> {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setClassSchedule(classSchedule);
            attendanceRepository.save(attendance);
        });

        return true;
    }

    public Boolean checkAttendanceForClassSchedule(Long classScheduleId, List<AttendanceDTO> attendanceDTOs) {
        //Check if classSchedule exists
        classScheduleRepository.findById(classScheduleId).orElseThrow(EntityNotFoundException::new);

        //Check attendance
        attendanceDTOs.forEach(attendanceDTO -> {
            Attendance attendance = attendanceRepository.findById(attendanceDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("This attendance does not exist"));
            attendance.setIsPresent(attendanceDTO.getIsPresent());
            attendanceRepository.save(attendance);
        });

        return true;
    }
}
