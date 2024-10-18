package pl.poszkole.PoSzkole.service;

import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.RoomRepository;
import pl.poszkole.PoSzkole.repository.ScheduleChangesLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final WebsiteUserService websiteUserService;
    private final ClassScheduleMapper classScheduleMapper;
    private final RoomRepository roomRepository;
    private final ScheduleChangesLogMapper scheduleChangesLogMapper;
    private final ScheduleChangesLogRepository scheduleChangesLogRepository;

    //TODO: (ZS) Possibly add either is_canceled to model or check for certain reasons (like teacher unavailable) that should always mean that the class is canceled
    //This cannot be universal since checking role here would do bad stuff for ppl with 2 roles (T and S)
    public Page<ClassScheduleDTO> getAllClassSchedulesForCurrentStudent(Pageable pageable) {
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is actually a student
        if(currentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can only view student classes here");
        }

        // Build specification to match the current student's classes
        Specification<ClassSchedule> specification = (root, query, builder) -> {
            // Join between ClassSchedule and TutoringClass
            Join<ClassSchedule, TutoringClass> classJoin = root.join("tutoringClass");

            // Predicate to check if the class is in the current user's list of classes
            return builder.isTrue(classJoin.in(currentUser.getClasses()));
        };

        Page<ClassSchedule> classSchedules = classScheduleRepository.findAll(specification, pageable);
        return classSchedules.map(classScheduleMapper::toDto);
    }

    //This is made for "I have an exam and I need to just pass it" type of classes
    public void createSingleClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass, boolean isOnline) {
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));
        createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);

    }

    //This is the standard "I need a whole year of additional math classes" type
    public void createRepeatingClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass,
                                                      boolean isOnline, LocalDate repeatUntil) {
        //TODO: MAYBE add intervals to easily create classes every 2 weeks for example
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));
        if (!firstDate.isAfter(LocalDate.now()) || firstDate.isAfter(repeatUntil)) {
            throw new RuntimeException("Date couldn't be found");
        }
        while (!firstDate.isAfter(repeatUntil)){
            createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);
            firstDate = firstDate.plusWeeks(1);
        }
    }

    private void createSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass, boolean isOnline, LocalDate firstDate) {
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setClassDateFrom(LocalDateTime.of(firstDate, dayAndTimeDTO.getTimeFrom()));
        classSchedule.setClassDateTo(LocalDateTime.of(firstDate, dayAndTimeDTO.getTimeTo()));
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setIsOnline(isOnline);
        classScheduleRepository.save(classSchedule);
    }


    //TODO: Ask if it's possible to add substitute teacher to a schedule
    public ClassScheduleDTO updateClassSchedule(
            Long scheduleId, ClassScheduleDTO classScheduleDTO, ScheduleChangesLogDTO changesLogDTO
    ) {
        //Check if class existsSchedule exists and if reason for change was given
        //Currently im not checking if reason is adequate to the changes made
        ClassSchedule classSchedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Class schedule not found"));
        if(changesLogDTO.getReason() == null) {
            throw new RuntimeException("You must provide a reason for making changes in this class");
        }

        //To the changing
        classScheduleMapper.partialUpdate(classScheduleDTO,classSchedule);

        //Change the room if it was changed since it's too hard of a task for the mapper
        if(classScheduleDTO.getRoom() != null
                && !classScheduleDTO.getRoom().getId().equals(classSchedule.getRoom().getId())){
            classSchedule.setRoom(roomRepository.findById(classScheduleDTO.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found")));
        }

        //Save changes
        classScheduleRepository.save(classSchedule);

        //Create log entry
        ScheduleChangesLog log = scheduleChangesLogMapper.toEntity(changesLogDTO);
        log.setClassSchedule(classSchedule);
        scheduleChangesLogRepository.save(log);

        return classScheduleMapper.toDto(classSchedule);
    }
}
