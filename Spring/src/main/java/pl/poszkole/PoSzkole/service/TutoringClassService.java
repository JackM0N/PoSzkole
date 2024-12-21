package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.SimplifiedUserDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.LocalDate;
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

    //TODO: Add possibility to cancel the rest of the classes

    public List<TutoringClassDTO> getActiveTutoringClassesForCurrentTeacher() {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if user is a teacher
        if(currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))){
            throw new RuntimeException("You are not allowed to access this function");
        }


        return null;
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

        //Add student to class
        studentUser.addClass(tutoringClass);
        websiteUserRepository.save(studentUser);

        return tutoringClassMapper.toDto(tutoringClass);
    }

    //TODO: Ask if teacher should be able to make an account for a student for his class
    //TODO: Same payments problem. Schedule or monthly based. What about February or December for example?
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

    //TODO: Add removal of student from a class and ask what should that entail. Just canceling rest of the payments? Canceling rest of payments starting next month? Something else?
}
