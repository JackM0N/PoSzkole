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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final WebsiteUserService websiteUserService;
    private final ClassScheduleMapper classScheduleMapper;
    private final RoomRepository roomRepository;
    private final ScheduleChangesLogMapper scheduleChangesLogMapper;
    private final ScheduleChangesLogRepository scheduleChangesLogRepository;
    private final UserBusyDayService userBusyDayService;

    //TODO: (ZS) Possibly add either is_canceled to model or check for certain reasons (like teacher unavailable) that should always mean that the class is canceled
    //This cannot be universal since checking role here would do bad stuff for ppl with 2 roles (T and S)
    public List<ClassScheduleDTO> getAllClassSchedulesForCurrentStudent() {
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

        List<ClassSchedule> classSchedules = classScheduleRepository.findAll(specification);
        return classSchedules.stream().map(classScheduleMapper::toDto).collect(Collectors.toList());
    }

    //This is made for "I have an exam and I need to just pass it" type of classes
    public void createSingleClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass,
                                          boolean isOnline, Long studentId) {
        //Get first calendar date chosen day of class
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));

        //Create schedule
        ClassSchedule classSchedule = createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);

        //Check for overlap with other class schedules
        if (!classScheduleRepository.findOverlappingSchedulesForStudent(
                studentId, classSchedule.getClassDateFrom(), classSchedule.getClassDateTo()).isEmpty()){
            throw new RuntimeException("Class schedule overlaps with existing class");
        }

        classScheduleRepository.save(classSchedule);
    }

    //This is the standard "I need a whole year of additional math classes" type
    public void createRepeatingClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass,
                                                      boolean isOnline, LocalDate repeatUntil, Long studentId) {
        //TODO: MAYBE add intervals to easily create classes every 2 weeks for example
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));

        if (!firstDate.isAfter(LocalDate.now()) || firstDate.isAfter(repeatUntil)) {
            throw new RuntimeException("Date couldn't be found");
        }

        List<ClassSchedule> classSchedules = new ArrayList<>();
        while (!firstDate.isAfter(repeatUntil)){
            //Create new ClassSchedule
            ClassSchedule newClassSchedule = createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);

            //Check if this class schedule overlaps any existing one
            if (!classScheduleRepository.findOverlappingSchedulesForStudent(
                    studentId, newClassSchedule.getClassDateFrom(), newClassSchedule.getClassDateTo()).isEmpty()){
                throw new RuntimeException("Class schedule overlaps with existing class");
            }
            //If it doesn't add it to list of class schedules
            classSchedules.add(newClassSchedule);
            firstDate = firstDate.plusWeeks(1);
        }
        classScheduleRepository.saveAll(classSchedules);
    }

    private ClassSchedule createSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass, boolean isOnline, LocalDate firstDate) {
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setClassDateFrom(LocalDateTime.of(firstDate, dayAndTimeDTO.getTimeFrom()));
        classSchedule.setClassDateTo(LocalDateTime.of(firstDate, dayAndTimeDTO.getTimeTo()));
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setIsOnline(isOnline);
        return classSchedule;
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
