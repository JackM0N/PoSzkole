package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;

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
