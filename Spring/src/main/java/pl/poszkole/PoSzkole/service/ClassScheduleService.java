package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.DateAndTimeDTO;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final TutoringClassRepository tutoringClassRepository;
    private final WebsiteUserRepository websiteUserRepository;
    private final UserBusyDayService userBusyDayService;

    //This cannot be universal since checking role here would do bad stuff for ppl with 2 roles (T and S)
    public List<ClassScheduleDTO> getAllClassSchedulesForCurrentStudent(Long userId) {
        WebsiteUser currentUser;

        //Get current user if no user is sent or get schedule for chosen user
        if (userId == null) {
           currentUser = websiteUserService.getCurrentUser();
        }else {
           currentUser = websiteUserRepository.findById(userId)
                   .orElseThrow(() -> new RuntimeException("User not found"));
        }

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

        // Add sorting by date
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("classDateFrom")));

        Page<ClassSchedule> classSchedules = classScheduleRepository.findAll(specification, pageable);
        return classSchedules.stream().map(classScheduleMapper::toDto).collect(Collectors.toList());
    }

    public List<ClassScheduleDTO> getAllClassSchedulesForCurrentTeacher(Long userId) {
        WebsiteUser currentUser;

        //Get current user if no user is sent or get schedule for chosen user
        if (userId == null) {
            currentUser = websiteUserService.getCurrentUser();
        }else {
            currentUser = websiteUserRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        //Check if user is actually a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))) {
            throw new RuntimeException("You can only view teacher classes here");
        }

        // Build specification to match the current teachers classes
        Specification<ClassSchedule> specification = (root, query, builder) -> {
            // Join between ClassSchedule and TutoringClass
            Join<ClassSchedule, TutoringClass> classJoin = root.join("tutoringClass");

            // Predicate to check if the class has current teacher in it
            return builder.equal(classJoin.get("teacher").get("id"), currentUser.getId());
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
                studentId, classSchedule.getClassDateFrom(), classSchedule.getClassDateTo(), null).isEmpty()){
            throw new RuntimeException("Class schedule overlaps with existing student's class");
        }

        if (!classScheduleRepository.findOverlappingSchedulesForTeacher(
                tutoringClass.getTeacher().getId(),
                classSchedule.getClassDateFrom(),
                classSchedule.getClassDateTo(),
                null
        ).isEmpty()){
            throw new RuntimeException("Class schedule overlaps with existing teacher's class");
        }

        classScheduleRepository.save(classSchedule);
    }

    //This is the standard "I need a whole year of additional math classes" type
    @Transactional
    public void createRepeatingClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass,
                                                      boolean isOnline, LocalDate repeatUntil, List<WebsiteUser> students) {
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));

        if (firstDate.equals(LocalDate.now())) {
            firstDate = firstDate.plusWeeks(1);
        }

        if (!firstDate.isAfter(LocalDate.now()) || firstDate.isAfter(repeatUntil)) {
            throw new RuntimeException("Date couldn't be found");
        }

        List<ClassSchedule> classSchedules = new ArrayList<>();
        while (!firstDate.isAfter(repeatUntil)){
            //Create new ClassSchedule
            ClassSchedule newClassSchedule = createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);

            //Check if this class schedule overlaps any existing one for the teacher
            if (!classScheduleRepository.findOverlappingSchedulesForTeacher(
                    tutoringClass.getTeacher().getId(),
                    newClassSchedule.getClassDateFrom(),
                    newClassSchedule.getClassDateTo(),
                    null
            ).isEmpty()){
                throw new RuntimeException("Class schedule overlaps with existing teacher's class");
            }

            //We need to check all students (this method was changed from single student use for course impl)
            students.forEach(student ->{
                //Check if this class schedule overlaps any existing one for every student
                if (!classScheduleRepository.findOverlappingSchedulesForStudent(
                        student.getId(),
                        newClassSchedule.getClassDateFrom(),
                        newClassSchedule.getClassDateTo(),
                        null
                ).isEmpty()){
                    throw new RuntimeException("Class schedule overlaps with existing student's class");
                }
            });
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
        classSchedule.setIsCanceled(false);
        return classSchedule;
    }


    public ClassScheduleDTO updateClassSchedule(
            Long scheduleId, ClassScheduleDTO classScheduleDTO, DateAndTimeDTO dateAndTimeDTO, ScheduleChangesLogDTO changesLogDTO
    ) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        //Check if class existsSchedule exists, currently logged teacher can edit it and if reason for change was given
        //Currently im not checking if reason is adequate to the changes made
        ClassSchedule classSchedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Class schedule not found"));
        if (!Objects.equals(classSchedule.getTutoringClass().getTeacher().getId(), currentUser.getId())) {
            throw new RuntimeException("You can only edit your own classes");
        }
        if(changesLogDTO.getReason() == null) {
            throw new RuntimeException("You must provide a reason for making changes in this class");
        }

        //Do the changing
        classScheduleMapper.partialUpdate(classScheduleDTO,classSchedule);

        //Change the name of the tutoringClass if needed
        if (!Objects.equals(classScheduleDTO.getTutoringClass().getClassName(), classSchedule.getTutoringClass().getClassName())){
            TutoringClass tutoringClass = classSchedule.getTutoringClass();
            tutoringClass.setClassName(classScheduleDTO.getTutoringClass().getClassName());
            tutoringClassRepository.save(tutoringClass);
        }

        //Update class dates
        if (dateAndTimeDTO.getTimeFrom() != null){
            LocalDateTime timeFrom = classSchedule.getClassDateFrom();
            LocalDateTime timeTo = classSchedule.getClassDateTo();

            //Check if times were given properly
            if (dateAndTimeDTO.getTimeFrom().isAfter(dateAndTimeDTO.getTimeTo())) {
                throw new RuntimeException("Time from is after time to");
            }

            if (dateAndTimeDTO.getDate() == null){
                classSchedule.setClassDateFrom(timeFrom.withHour(dateAndTimeDTO.getTimeFrom().getHour()).withMinute(dateAndTimeDTO.getTimeFrom().getMinute()));
                classSchedule.setClassDateTo(timeTo.withHour(dateAndTimeDTO.getTimeTo().getHour()).withMinute(dateAndTimeDTO.getTimeTo().getMinute()));
            }else {
                LocalDate firstDate = dateAndTimeDTO.getDate();
                classSchedule.setClassDateFrom(LocalDateTime.of(firstDate, dateAndTimeDTO.getTimeFrom()));
                classSchedule.setClassDateTo(LocalDateTime.of(firstDate, dateAndTimeDTO.getTimeTo()));
            }
        }

        //Change the room if it was changed since it's too hard of a task for the mapper
        if(classScheduleDTO.getRoom() != null
                && !classScheduleDTO.getRoom().getId().equals(classSchedule.getRoom().getId())){
            classSchedule.setRoom(roomRepository.findById(classScheduleDTO.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found")));
        }

        //Check for schedule collisions
        //Check if this class schedule overlaps any existing one for the teacher
        if (!classScheduleRepository.findOverlappingSchedulesForTeacher(
                classSchedule.getTutoringClass().getTeacher().getId(),
                classSchedule.getClassDateFrom(),
                classSchedule.getClassDateTo(),
                classSchedule.getId()
        ).isEmpty()){
            throw new RuntimeException("Class schedule overlaps with your schedule");
        }

        //Check if this class schedule overlaps any existing one for any of the students or their busy days
        classSchedule.getTutoringClass().getStudents().forEach(student -> {
            //Schedule
            if (!classScheduleRepository.findOverlappingSchedulesForStudent(
                    student.getId(),
                    classSchedule.getClassDateFrom(),
                    classSchedule.getClassDateTo(),
                    classSchedule.getId()
            ).isEmpty()){
                throw new RuntimeException("Class schedule overlaps with one of the student's class");
            }
            //Busy days
            if (userBusyDayService.isOverlapping(
                    student,
                    null,
                    DayOfWeek.from(classSchedule.getClassDateFrom()),
                    classSchedule.getClassDateFrom().toLocalTime(),
                    classSchedule.getClassDateTo().toLocalTime())){
                throw new RuntimeException("Class schedule overlaps with busy day of one of the students");
            }
        });

        //Save changes
        classScheduleRepository.save(classSchedule);

        //Create log entry
        ScheduleChangesLog log = scheduleChangesLogMapper.toEntity(changesLogDTO);
        log.setClassSchedule(classSchedule);
        log.setUser(currentUser);
        scheduleChangesLogRepository.save(log);

        return classScheduleMapper.toDto(classSchedule);
    }

    public ClassScheduleDTO completeClassSchedule(Long scheduleId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if current user is a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))){
            throw new RuntimeException("You are not allowed to perform this action");
        }

        ClassSchedule classSchedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Class schedule not found"));

        //Check if he can actually complete it
        if (!classSchedule.getTutoringClass().getTeacher().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not allowed to perform this action on someone else's class");
        }
        if(LocalDateTime.now().isBefore(classSchedule.getClassDateTo())){
            throw new RuntimeException("You cant complete a class that has not started yet");
        }
        if(classSchedule.getIsCompleted()){
            throw new RuntimeException("Class schedule is already completed");
        }

        //Complete and save
        classSchedule.setIsCompleted(true);
        classScheduleRepository.save(classSchedule);

        //Check if all schedules are completed
        TutoringClass tutoringClass = classSchedule.getTutoringClass();
        boolean allSchedulesCompleted = classScheduleRepository
                .findAllByTutoringClassId(tutoringClass.getId())
                .stream()
                .allMatch(schedule -> schedule.getIsCompleted() || schedule.getIsCanceled());

        //If all schedules are completed, set tutoringClass to complete
        if (allSchedulesCompleted) {
            tutoringClass.setIsCompleted(true);
            tutoringClassRepository.save(tutoringClass);
        }

        return classScheduleMapper.toDto(classSchedule);
    }

    @Transactional
    public ClassScheduleDTO cancelClassSchedule(Long scheduleId, ScheduleChangesLogDTO changesLogDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        //Check if classSchedule exists
        ClassSchedule classSchedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Class schedule not found"));
        //Assert that student is able to cancel it
        if (classSchedule.getTutoringClass().getStudents().size() > 1){
            throw new RuntimeException("You can only cancel individual classes");
        }
        if (classSchedule.getClassDateFrom().isBefore(LocalDateTime.now().plusDays(1))){
            throw new RuntimeException("You cannot cancel a class that starts in less than 24 hours");
        }

        //Assert that he has given a reason for canceling
        if (changesLogDTO.getReason() == null) {
            throw new RuntimeException("You must provide a reason for making changes in this class");
        }
        //Cancel chosen schedule
        classSchedule.setIsCanceled(true);
        classScheduleRepository.save(classSchedule);

        //Create log entry
        ScheduleChangesLog log = scheduleChangesLogMapper.toEntity(changesLogDTO);
        log.setClassSchedule(classSchedule);
        log.setUser(currentUser);
        scheduleChangesLogRepository.save(log);

        return classScheduleMapper.toDto(classSchedule);
    }
}
