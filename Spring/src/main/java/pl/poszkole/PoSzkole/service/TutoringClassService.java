package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.dto.SimplifiedUserDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.ScheduleChangesLogRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TutoringClassService {
    private final TutoringClassMapper tutoringClassMapper;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserRepository websiteUserRepository;
    private final TutoringClassRepository tutoringClassRepository;
    private final ClassScheduleService classScheduleService;
    private final SimplifiedUserMapper simplifiedUserMapper;
    private final ClassScheduleRepository classScheduleRepository;
    private final ClassScheduleMapper classScheduleMapper;
    private final UserBusyDayService userBusyDayService;
    private final ScheduleChangesLogMapper scheduleChangesLogMapper;
    private final ScheduleChangesLogRepository scheduleChangesLogRepository;

    public List<TutoringClassDTO> getActiveTutoringClassesForCurrentTeacher(Long subjectId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))){
            throw new RuntimeException("You are not allowed to access this function");
        }

        //Get all teachers active classes
        List<TutoringClass> tutoringClasses = tutoringClassRepository.findByTeacherIdAndIsCompletedAndSubjectId(
                currentUser.getId(),false, subjectId
        );

        return tutoringClasses.stream().map(tutoringClass -> {
            //Additional mapping added for better tutoringClass description
            TutoringClassDTO tutoringClassDTO = tutoringClassMapper.toDto(tutoringClass);
            tutoringClassDTO.setNumberOfStudents(tutoringClass.getStudents().size());
            tutoringClassDTO.setNextClassSchedule(
                    classScheduleMapper.toDto(classScheduleRepository.findFirstByTutoringClassIdAndClassDateFromAfter(
                            tutoringClass.getId(), LocalDateTime.now()
                    ).orElse(null))
            );
            return tutoringClassDTO;
        }).collect(Collectors.toList());
    }

    public List<SimplifiedUserDTO> getStudentsForTutoringClass(Long tutoringClassId) {
        TutoringClass tutoringClass = tutoringClassRepository.findById(tutoringClassId)
                .orElseThrow(() -> new EntityNotFoundException("Tutoring class not found"));
        List<WebsiteUser> tutoredStudents = tutoringClass.getStudents();
        return tutoredStudents.stream().map(simplifiedUserMapper::toSimplifiedUserDTO).collect(Collectors.toList());
    }

    public TutoringClassDTO addToTutoringClass(Long userId, Long classId) {
        //Find the student
        WebsiteUser studentUser = websiteUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("This student does not exist"));

        //Check if it's actually a student
        if (studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can't add user to a class that's not a student");
        }

        //Find the class
        TutoringClass tutoringClass = tutoringClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("This class does not exist"));

        if(studentUser.getClasses().contains(tutoringClass)){
            throw new RuntimeException("You can't add this student to a class that he is already attending");
        }

        //Find closest classSchedule
        ClassSchedule classSchedule = classScheduleRepository.findFirstByTutoringClassIdAndClassDateFromAfter(
                tutoringClass.getId(), LocalDateTime.now())
                .orElseThrow(() -> new EntityNotFoundException("This class does not have any more schedules"));
        DayOfWeek scheduleDayOfWeek = DayOfWeek.from(classSchedule.getClassDateFrom());
        LocalTime timeFrom = LocalTime.from(classSchedule.getClassDateFrom());
        LocalTime timeTo = LocalTime.from(classSchedule.getClassDateTo());

        if (userBusyDayService.isOverlapping(studentUser, null, scheduleDayOfWeek, timeFrom, timeTo)){
            throw new RuntimeException("You cannot add student to a class that's on students busy day");
        }

        if (!classScheduleRepository.findOverlappingSchedulesForStudent(
                studentUser.getId(), classSchedule.getClassDateFrom(), classSchedule.getClassDateTo()).isEmpty()){
            throw new RuntimeException("Class schedule overlaps with existing class of this student");
        }

        //Add student to class
        studentUser.addClass(tutoringClass);
        websiteUserRepository.save(studentUser);

        return tutoringClassMapper.toDto(tutoringClass);
    }

    public TutoringClassDTO createTutoringClass(Long studentId, TutoringClassDTO tutoringClassDTO,
                                                DayAndTimeDTO dayAndTimeDTO, Boolean isOnline, LocalDate repeatUntil) {
        //Get needed users
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        WebsiteUser studentUser = websiteUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("This user does not exist"));

        //Check if it's actually a student
        if (studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can't add user to a class that's not a student");
        }

        //Create class
        TutoringClass tutoringClass = tutoringClassMapper.toEntity(tutoringClassDTO);
        tutoringClass.setTeacher(currentUser);
        tutoringClassRepository.save(tutoringClass);

        //Add student to created class
        studentUser.addClass(tutoringClass);
        websiteUserRepository.save(studentUser);

        //Create class schedule
        if (repeatUntil == null) {
            classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, studentUser.getId());
        }else {
            List<WebsiteUser> students = new ArrayList<>();
            students.add(studentUser);
            classScheduleService.createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, repeatUntil, students);
        }

        return tutoringClassMapper.toDto(tutoringClass);
    }

    public TutoringClassDTO cancelTheRestOfTutoringClass(Long tutoringClassId, ScheduleChangesLogDTO scheduleChangesLogDTO) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        TutoringClass tutoringClass = tutoringClassRepository.findById(tutoringClassId)
                .orElseThrow(() -> new EntityNotFoundException("This class does not exist"));

        if (!tutoringClass.getTeacher().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot cancel tutoring class that you are not the teacher of");
        }

        List<ClassSchedule> classSchedules = classScheduleRepository.findAllByTutoringClassIdAndClassDateFromAfter(
                tutoringClass.getId(), LocalDateTime.now());

        classSchedules.forEach(classSchedule -> {
            //Cancel schedule
            classSchedule.setIsCanceled(true);
            classScheduleRepository.save(classSchedule);

            //TODO: Ask if it should create one for chosen schedule or all of em` (as in where did the cancellation started)
            //Create changelogs
            ScheduleChangesLog scheduleChangesLog = scheduleChangesLogMapper.toEntity(scheduleChangesLogDTO);
            scheduleChangesLog.setUser(currentUser);
            scheduleChangesLog.setClassSchedule(classSchedule);
            scheduleChangesLogRepository.save(scheduleChangesLog);
        });

        return tutoringClassMapper.toDto(tutoringClass);
    }

    public TutoringClassDTO removeStudentFromTutoringClass(Long tutoringClassId, Long studentId) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        TutoringClass tutoringClass = tutoringClassRepository.findById(tutoringClassId)
                .orElseThrow(() -> new EntityNotFoundException("This class does not exist"));

        if (!tutoringClass.getTeacher().getId().equals(currentUser.getId())) {
            throw new  AccessDeniedException("You cannot edit tutoring class that you are not the teacher of");
        }

        WebsiteUser student = websiteUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("This student does not exist"));

        student.removeClass(tutoringClass);
        websiteUserRepository.save(student);

        //TODO: Kinda same vibe here. Should this create a changelog? (I think not but maybe?)

        return tutoringClassMapper.toDto(tutoringClass);
    }
}
