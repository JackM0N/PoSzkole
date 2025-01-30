package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.enums.Reason;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.model.Course;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CourseServiceIntegrationTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Course course;
    private WebsiteUser student;
    private WebsiteUser teacher;
    private Subject subject;


    @BeforeEach
    void setUp() {
        // Arrange
        course = new Course();
        course.setCourseName("Kurs z przedmiotu");
        course.setPrice(BigDecimal.valueOf(100));
        course.setMaxParticipants(10);
        course.setStartDate(LocalDate.now().plusDays(10));
        course.setDescription("Opis kursu");
        course.setIsDone(false);
        course.setIsOpenForRegistration(false);
        courseRepository.save(course);

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
        websiteUserRepository.save(teacher);

        subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);
    }

    @Test
    void testGetAllNotStartedCourses_Success() {
        // Arrange
        CourseFilter filter = new CourseFilter();

        // Act
        Page<CourseDTO> result = courseService.getAllNotStartedCourses(filter, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllAvailableCourses_Success() {
        // Arrange
        course.setIsOpenForRegistration(true);
        courseRepository.save(course);
        CourseFilter filter = new CourseFilter();

        // Act
        Page<CourseDTO> result = courseService.getAllAvailableCourses(filter, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @WithMockUser(username = "student")
    void testGetBoughtCourses_Success() {
        // Arrange
        student.addCourse(course);
        websiteUserRepository.save(student);

        // Act
        Page<CourseDTO> result = courseService.getBoughtCourses(null, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetActiveCourses_Success() {
        // Arrange
        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setClassName("Zajęcia z przedmiotu");
        tutoringClass.setSubject(subject);
        tutoringClass.setTeacher(teacher);
        tutoringClassRepository.save(tutoringClass);

        course.setTutoringClass(tutoringClass);
        courseRepository.save(course);

        CourseFilter filter = new CourseFilter();

        // Act
        Page<CourseDTO> result = courseService.getActiveCourses(filter, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetCourseDescription_Success() {
        // Act
        String description = courseService.getCourseDescription(course.getId());

        // Assert
        assertNotNull(description);
        assertEquals("Opis kursu", description);
    }

    @Test
    void testGetCourseDescription_CourseNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.getCourseDescription(9999999L), "Course not found");
    }

    @Test
    void testGetCourseAttendants_Success() {
        // Arrange
        student.addCourse(course);
        websiteUserRepository.save(student);

        // Act
        List<SimplifiedUserDTO> result = courseService.getCourseAttendants(course.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCourseAttendants_CourseNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.getCourseAttendants(9999999L), "Course not found");
    }

    @Test
    void testStartCourse_Success() {
        // Arrange
        SimplifiedUserDTO simpleTeacher = new SimplifiedUserDTO();
        simpleTeacher.setFirstName("Jane");
        simpleTeacher.setLastName("Doe");

        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(subject.getId());
        subjectDTO.setSubjectName("Przedmiot");

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName(course.getCourseName());
        tutoringClassDTO.setSubject(subjectDTO);
        tutoringClassDTO.setTeacher(simpleTeacher);

        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 0));

        StartCourseDTO startCourseDTO = new StartCourseDTO();
        startCourseDTO.setCourseId(course.getId());
        startCourseDTO.setTeacherId(teacher.getId());
        startCourseDTO.setTutoringClassDTO(tutoringClassDTO);
        startCourseDTO.setDayAndTimeDTO(dayAndTimeDTO);
        startCourseDTO.setIsOnline(true);
        startCourseDTO.setRepeatUntil(LocalDate.now().plusMonths(1));

        // Act
        CourseDTO result = courseService.startCourse(startCourseDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTutoringClassId());
    }

    @Test
    void testStartCourse_CourseNotFound() {
        StartCourseDTO dto = new StartCourseDTO();
        dto.setCourseId(9999999L); // Non-existent course ID

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.startCourse(dto), "Course not found");
    }

    @Test
    void testStartCourse_TeacherNotFound() {
        // Arrange
        StartCourseDTO dto = new StartCourseDTO();
        dto.setCourseId(course.getId());
        dto.setTeacherId(99L); // Non-existent teacher ID

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.startCourse(dto), "Teacher not found");
    }

    @Test
    void testStartCourse_UserNotTeacher() {
        // Arrange
        StartCourseDTO dto = new StartCourseDTO();
        dto.setCourseId(course.getId());
        dto.setTeacherId(student.getId());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.startCourse(dto), "Chosen user is not a teacher");
    }


    @Test
    void testCreateCourse_Success() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseName("New Course");
        courseDTO.setPrice(BigDecimal.valueOf(200));
        courseDTO.setMaxParticipants(15);
        courseDTO.setStartDate(LocalDate.now().plusWeeks(2));
        courseDTO.setDescription("New Description");

        // Act
        CourseDTO result = courseService.createCourse(courseDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New Course", result.getCourseName());
    }

    @Test
    void testAddStudentToCourse_Success() {
        // Act
        CourseDTO result = courseService.addStudentToCourse(course.getId(), student.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getStudents().size());
    }

    @Test
    void testAddStudentToCourse_CourseNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.addStudentToCourse(9999999L, student.getId()), "Course not found");
    }

    @Test
    void testAddStudentToCourse_StudentNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.addStudentToCourse(course.getId(), 99L), "Student not found"
        );
    }

    @Test
    void testAddStudentToCourse_UserNotStudent() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> courseService.addStudentToCourse(course.getId(), teacher.getId()),
                "User you are trying to add is not a student"
        );
    }

    @Test
    void testRemoveStudentFromCourse_Success() {
        // Arrange
        student.addCourse(course);
        websiteUserRepository.save(student);

        // Act
        CourseDTO result = courseService.removeStudentFromCourse(course.getId(), student.getId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getStudents().size());
    }

    @Test
    void testEditCourse_Success() {
        // Arrange
        CourseDTO updatedCourseDTO = new CourseDTO();
        updatedCourseDTO.setCourseName("Updated Course Name");
        updatedCourseDTO.setPrice(BigDecimal.valueOf(150));
        updatedCourseDTO.setMaxParticipants(20);

        // Act
        CourseDTO result = courseService.editCourse(course.getId(), updatedCourseDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Course Name", result.getCourseName());
        assertEquals(BigDecimal.valueOf(150), result.getPrice());
        assertEquals(20, result.getMaxParticipants());
    }

    @Test
    void testOpenCourseForRegistration_Success() {
        // Act
        CourseDTO result = courseService.openCourseForRegistration(course.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsOpenForRegistration());
    }

    @Test
    void testFinishCourse_Success() {
        // Act
        CourseDTO result = courseService.finishCourse(course.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDone());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCancelCourse_Success() {
        // Arrange
        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setTeacher(teacher);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setSubject(subject);
        tutoringClassRepository.save(tutoringClass);

        course.setTutoringClass(tutoringClass);
        courseRepository.save(course);

        ScheduleChangesLogDTO logDTO = new ScheduleChangesLogDTO();
        logDTO.setReason(Reason.OTHER);

        // Act
        courseService.cancelCourse(course.getId(), logDTO);

        // Assert
        Course cancelledCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertTrue(cancelledCourse.getIsDone());
    }

    @Test
    void testDeleteCourse_Success() {
        // Act
        courseService.deleteCourse(course.getId());

        // Assert
        boolean courseExists = courseRepository.findById(course.getId()).isPresent();
        assertFalse(courseExists);
    }

    @Test
    void testDeleteCourse_CourseNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> courseService.deleteCourse(999L), "Course not found");
    }

    @Test
    void testDeleteCourse_OpenForRegistration() {
        course.setIsOpenForRegistration(true);
        courseRepository.save(course);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(course.getId()),
                "You cant delete a course that is already open for registration");
    }

    @Test
    void testDeleteCourse_HasRegisteredStudents() {
        student.addCourse(course);
        websiteUserRepository.save(student);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(course.getId()),
                "You cant delete a course that people are already registered to"
        );
    }
}
