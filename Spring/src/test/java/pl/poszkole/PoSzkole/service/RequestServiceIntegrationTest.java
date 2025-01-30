package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.enums.ClassLocation;
import pl.poszkole.PoSzkole.mapper.SubjectMapper;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RequestServiceIntegrationTest {
    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WebsiteUserMapper websiteUserMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    WebsiteUser student;
    WebsiteUser teacher;
    Subject subject;
    TutoringClass tutoringClass;
    Request request;
    @Autowired
    private UserBusyDayRepository userBusyDayRepository;

    @BeforeEach
    void setup() {
        // Setup necessary data for all tests
        student = new WebsiteUser();
        student.setId(99999L);
        student.setUsername("student");
        student.setPassword("student123");
        student.setFirstName("Student");
        student.setLastName("Test");
        student.setEmail("student@student.com");
        student.setIsDeleted(false);
        student.setGender("M");
        student.setPhone("123456789");
        student.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(student);

        subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);

        teacher = new WebsiteUser();
        teacher.setId(9999L);
        teacher.setUsername("teacher");
        teacher.setPassword("teacher123");
        teacher.setFirstName("Teacher");
        teacher.setLastName("Test");
        teacher.setEmail("teacher@teacher.com");
        teacher.setIsDeleted(false);
        teacher.setGender("M");
        teacher.setPhone("987654321");
        teacher.setRoles(Set.of(roleRepository.findByRoleName("TEACHER").get()));
        teacher.setSubjects(Set.of(subject));
        websiteUserRepository.save(teacher);

        tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setTeacher(teacher);
        tutoringClass.setSubject(subject);
        tutoringClassRepository.save(tutoringClass);

        request = new Request();
        request.setStudent(student);
        request.setSubject(subject);
        request.setIssueDate(LocalDate.now());
        request.setPrefersLocation(ClassLocation.NONE);
        request.setRepeatUntil(LocalDate.now().plusMonths(4));
        requestRepository.save(request);
    }

    @Test
    @WithMockUser(username = "teacher")
    void testGetRequestsForTeacher_Success() throws AccessDeniedException {
        // Act
        Page<RequestDTO> result = requestService.getRequestsForTeacher(false, subject, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @WithMockUser(username = "student")
    void testGetRequestsForTeacher_UserNotTeacher() {
        // Act & Assert
        Exception exception = assertThrows(AccessDeniedException.class, () ->
                requestService.getRequestsForTeacher(false, null, PageRequest.of(0, 10))
        );
        assertEquals("You do not have permission to access this resource", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testGetRequestsForTeacher_NoSubjectAssigned() {
        // Arrange
        teacher.setSubjects(Set.of());
        websiteUserRepository.save(teacher);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                requestService.getRequestsForTeacher(false, null, PageRequest.of(0, 10))
        );
        assertEquals("Teacher has no subjects assigned", exception.getMessage());
    }

    @Test
    void testCreateRequest_Success() throws BadRequestException {
        // Arrange
        WebsiteUserDTO studentDTO = websiteUserMapper.toDto(student);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRepeatUntil(LocalDate.now().plusMonths(4));
        requestDTO.setStudent(studentDTO);
        requestDTO.setSubject(subjectDTO);
        requestDTO.setPrefersIndividual(true);
        requestDTO.setPrefersLocation(ClassLocation.NONE);

        // Act
        RequestDTO result = requestService.createRequest(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getStudent().getId());
        assertEquals(subject.getId(), result.getSubject().getId());
    }

    @Test
    void testCreateRequest_BadRepeatUntil() {
        // Arrange
        WebsiteUserDTO studentDTO = websiteUserMapper.toDto(student);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRepeatUntil(LocalDate.now().minusMonths(1));
        requestDTO.setStudent(studentDTO);
        requestDTO.setSubject(subjectDTO);
        requestDTO.setPrefersIndividual(true);
        requestDTO.setPrefersLocation(ClassLocation.NONE);

        // Act & Assert
        Exception exception = assertThrows(BadRequestException.class, () ->
                requestService.createRequest(requestDTO)
        );
        assertEquals("You cant plan classes into the past", exception.getMessage());
    }

    @Test
    void testCreateRequest_InvalidStudentRole() {
        // Arrange
        WebsiteUserDTO notStudentDTO = websiteUserMapper.toDto(teacher);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setStudent(notStudentDTO);
        requestDTO.setSubject(subjectDTO);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                requestService.createRequest(requestDTO)
        );
        assertEquals("You can't create a class for a user that's not a student", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testAdmitRequestCreateClass_Success() {
        // Arrange
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");

        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(12, 0));

        // Act
        RequestDTO result = requestService.admitRequestCreateClass(request.getId(), tutoringClassDTO, dayAndTimeDTO, true);

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getStudent().getId());
        assertEquals(teacher.getId(), result.getTeacher().getId());
    }

    @Test
    void testAdmitRequestCreateClass_InvalidTimeRange() {
        // Arrange
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");

        DayAndTimeDTO invalidDayAndTime = new DayAndTimeDTO();
        invalidDayAndTime.setDay(DayOfWeek.MONDAY);
        invalidDayAndTime.setTimeFrom(LocalTime.of(12, 0));
        invalidDayAndTime.setTimeTo(LocalTime.of(11, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                requestService.admitRequestCreateClass(request.getId(), tutoringClassDTO, invalidDayAndTime, true)
        );
        assertEquals("Invalid time values", exception.getMessage());
    }

    @Test
    void testAdmitRequestCreateClass_TooShortTimeRange() {
        // Arrange
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");

        DayAndTimeDTO invalidDayAndTime = new DayAndTimeDTO();
        invalidDayAndTime.setDay(DayOfWeek.MONDAY);
        invalidDayAndTime.setTimeFrom(LocalTime.of(10, 30));
        invalidDayAndTime.setTimeTo(LocalTime.of(11, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                requestService.admitRequestCreateClass(request.getId(), tutoringClassDTO, invalidDayAndTime, true)
        );
        assertEquals("Class has to last at least 60 minutes", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testAdmitRequestCreateClass_Overlapping() {
        // Arrange
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");

        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(12, 0));

        UserBusyDay userBusyDay = new UserBusyDay();
        userBusyDay.setUser(student);
        userBusyDay.setDayOfTheWeek("MONDAY");
        userBusyDay.setTimeFrom(LocalTime.of(10, 0));
        userBusyDay.setTimeTo(LocalTime.of(12, 0));
        userBusyDayRepository.save(userBusyDay);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                requestService.admitRequestCreateClass(request.getId(), tutoringClassDTO, dayAndTimeDTO, true)
        );
        assertEquals("You cannot admit class on users busy day", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testAdmitRequestAddToClass_Success() {
        // Act
        RequestDTO result = requestService.admitRequestAddToClass(request.getId(), tutoringClass.getId());

        // Assert
        assertNotNull(result);
        assertEquals(student.getId(), result.getStudent().getId());
        assertNotNull(result.getAcceptanceDate());
    }


    @Test
    void testCreateRequest_RepeatUntilInThePast() {
        // Arrange
        WebsiteUserDTO notStudentDTO = websiteUserMapper.toDto(teacher);
        SubjectDTO subjectDTO = subjectMapper.toDto(subject);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setStudent(notStudentDTO);
        requestDTO.setSubject(subjectDTO);
        requestDTO.setPrefersIndividual(false);
        requestDTO.setRepeatUntil(LocalDate.now().minusDays(1));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                requestService.createRequest(requestDTO)
        );
        assertEquals("You cant plan classes into the past", exception.getMessage());
    }
}
