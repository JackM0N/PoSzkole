package pl.poszkole.PoSzkole.service;

import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final WebsiteUserService websiteUserService;
    private final ClassScheduleMapper classScheduleMapper;

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
            // Join between ClassSchedule and Class (assuming the entity is named 'class' in ClassSchedule)
            Join<ClassSchedule, TutoringClass> classJoin = root.join("tutoringClass");

            // Predicate to check if the class is in the current user's list of classes
            return builder.isTrue(classJoin.in(currentUser.getClasses()));
        };

        Page<ClassSchedule> classSchedules = classScheduleRepository.findAll(specification, pageable);
        return classSchedules.map(classScheduleMapper::toDto);
    }

    public void createSingleClassSchedule(DayAndTimeDTO dayAndTimeDTO, TutoringClass tutoringClass, boolean isOnline) {
        LocalDate firstDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayAndTimeDTO.getDay()));
        createSchedule(dayAndTimeDTO, tutoringClass, isOnline, firstDate);

    }

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
}
