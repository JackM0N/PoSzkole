package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.filter.AttendanceFilter;
import pl.poszkole.PoSzkole.mapper.AttendanceMapper;
import pl.poszkole.PoSzkole.model.Attendance;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.repository.AttendanceRepository;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final AttendanceMapper attendanceMapper;

    public Page<AttendanceDTO> findAllForClassSchedule(
            Long classScheduleId, AttendanceFilter attendanceFilter, Pageable pageable
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
        Page<Attendance> attendances = attendanceRepository.findAll(spec, pageable);

        return attendances.map(attendanceMapper::toDto);
    }

    //This method is used to see if attendance exists for given classSchedule
    public Boolean checkIfExists(Long classScheduleId) {
        return attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId);
    }

    public Boolean createForClassSchedule(Long classScheduleId) {
        //Check if classSchedule exists
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(EntityNotFoundException::new);

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
                    .orElseThrow(EntityNotFoundException::new);
            attendance.setIsPresent(attendanceDTO.getIsPresent());
            attendanceRepository.save(attendance);
        });

        return true;
    }
}
