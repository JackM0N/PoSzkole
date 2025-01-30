package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.mapper.CourseMapper;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.CourseRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CourseServiceUnitTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private TutoringClassMapper tutoringClassMapper;

    @Mock
    private TutoringClassRepository tutoringClassRepository;

    @Mock
    private ClassScheduleService classScheduleService;

    @Mock
    private SimplifiedUserMapper simplifiedUserMapper;

    @Mock
    private TutoringClassService tutoringClassService;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void testGetAllNotStartedCourses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        CourseFilter filter = new CourseFilter();
        Page<Course> coursePage = new PageImpl<>(List.of(new Course()));

        when(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(coursePage);
        when(courseMapper.toDto(any(Course.class)))
                .thenReturn(new CourseDTO());

        // Act
        Page<CourseDTO> result = courseService.getAllNotStartedCourses(filter, pageable);

        // Assert
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
        verify(courseMapper, times(1)).toDto(any(Course.class));
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAllAvailableCourses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        CourseFilter filter = new CourseFilter();
        Page<Course> coursePage = new PageImpl<>(List.of(new Course()));

        when(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(coursePage);
        when(courseMapper.toDto(any(Course.class)))
                .thenReturn(new CourseDTO());

        // Act
        Page<CourseDTO> result = courseService.getAllAvailableCourses(filter, pageable);

        // Assert
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
        verify(courseMapper, times(1)).toDto(any(Course.class));
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetBoughtCourses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        CourseFilter filter = new CourseFilter();
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        Page<Course> coursePage = new PageImpl<>(List.of(new Course()));

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(coursePage);
        when(courseMapper.toDto(any(Course.class)))
                .thenReturn(new CourseDTO());

        // Act
        Page<CourseDTO> result = courseService.getBoughtCourses(filter, pageable);

        // Assert
        verify(websiteUserService).getCurrentUser();
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetActiveCourses_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        CourseFilter filter = new CourseFilter();
        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        Course course = new Course();
        course.setTutoringClass(tutoringClass);
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setClassDateFrom(LocalDateTime.now());
        classSchedule.setTutoringClass(tutoringClass);
        Page<Course> coursePage = new PageImpl<>(List.of(course));

        when(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(coursePage);
        when(classScheduleRepository.findLastScheduleByClassId(1L))
                .thenReturn(Optional.of(classSchedule));
        when(courseMapper.toDto(any(Course.class)))
                .thenReturn(new CourseDTO());
        when(simplifiedUserMapper.toSimplifiedUserDTO(any()))
                .thenReturn(new SimplifiedUserDTO());

        // Act
        Page<CourseDTO> result = courseService.getActiveCourses(filter, pageable);

        // Assert
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
        verify(classScheduleRepository).findLastScheduleByClassId(1L);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetCourseDescription_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setDescription("Sample description");

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.of(course));

        // Act
        String description = courseService.getCourseDescription(courseId);

        // Assert
        verify(courseRepository).findById(courseId);
        assertNotNull(description);
        assertEquals("Sample description", description);
    }

    @Test
    void testGetCourseDescription_CourseNotFound() {
        // Arrange
        Long courseId = 1L;

        when(courseRepository.findById(courseId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> courseService.getCourseDescription(courseId));
        verify(courseRepository).findById(courseId);
    }

    @Test
    public void testGetCourseAttendants_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        WebsiteUser user1 = new WebsiteUser();
        user1.setId(1L);
        WebsiteUser user2 = new WebsiteUser();
        user2.setId(2L);

        course.setStudents(Arrays.asList(user1, user2));

        SimplifiedUserDTO dto1 = new SimplifiedUserDTO();
        dto1.setId(1L);
        SimplifiedUserDTO dto2 = new SimplifiedUserDTO();
        dto2.setId(2L);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(simplifiedUserMapper.toSimplifiedUserDTO(user1)).thenReturn(dto1);
        when(simplifiedUserMapper.toSimplifiedUserDTO(user2)).thenReturn(dto2);

        // Act
        List<SimplifiedUserDTO> result = courseService.getCourseAttendants(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    public void testStartCourse_Success() {
        // Arrange
        StartCourseDTO startCourseDTO = new StartCourseDTO();
        startCourseDTO.setCourseId(1L);
        startCourseDTO.setTeacherId(2L);
        startCourseDTO.setTutoringClassDTO(new TutoringClassDTO());
        startCourseDTO.setDayAndTimeDTO(new DayAndTimeDTO());
        startCourseDTO.setIsOnline(true);
        startCourseDTO.setRepeatUntil(LocalDate.now().plusDays(30));

        Course course = new Course();
        course.setId(1L);
        Role role = new Role();
        role.setRoleName("TEACHER");
        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(2L);
        teacher.setRoles(Set.of(role));
        TutoringClass tutoringClass = new TutoringClass();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.of(teacher));
        when(tutoringClassMapper.toEntity(startCourseDTO.getTutoringClassDTO())).thenReturn(tutoringClass);
        when(courseMapper.toDto(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.startCourse(startCourseDTO);

        // Assert
        assertNotNull(result);
        verify(tutoringClassRepository, times(1)).save(tutoringClass);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testStartCourse_TeacherNotFound() {
        // Arrange
        StartCourseDTO startCourseDTO = new StartCourseDTO();
        startCourseDTO.setCourseId(1L);
        startCourseDTO.setTeacherId(2L);
        startCourseDTO.setTutoringClassDTO(new TutoringClassDTO());
        startCourseDTO.setDayAndTimeDTO(new DayAndTimeDTO());
        startCourseDTO.setIsOnline(true);
        startCourseDTO.setRepeatUntil(LocalDate.now().plusDays(30));

        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> courseService.startCourse(startCourseDTO));
        assertEquals("Teacher not found", exception.getMessage());
    }

    @Test
    public void testStartCourse_UserNotTeacher() {
        // Arrange
        StartCourseDTO startCourseDTO = new StartCourseDTO();
        startCourseDTO.setCourseId(1L);
        startCourseDTO.setTeacherId(2L);
        startCourseDTO.setTutoringClassDTO(new TutoringClassDTO());
        startCourseDTO.setDayAndTimeDTO(new DayAndTimeDTO());
        startCourseDTO.setIsOnline(true);
        startCourseDTO.setRepeatUntil(LocalDate.now().plusDays(30));

        Course course = new Course();
        course.setId(1L);
        Role role = new Role();
        role.setRoleName("STUDENT");
        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(2L);
        teacher.setRoles(Set.of(role));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.of(teacher));

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> courseService.startCourse(startCourseDTO));
        assertEquals("Chosen user is not a teacher", exception.getMessage());
    }

    @Test
    public void testCreateCourse_Success() {
        // Arrange
        CourseDTO courseDTO = new CourseDTO();
        Course course = new Course();

        when(courseMapper.toEntity(courseDTO)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toDto(course)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.createCourse(courseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(courseDTO, result);
    }

    @Test
    public void testAddStudentToCourse_Success() {
        // Arrange
        Long courseId = 1L;
        Long studentId = 2L;
        Course course = new Course();
        course.setId(courseId);
        course.setMaxParticipants(2);

        Role role = new Role();
        role.setRoleName("STUDENT");

        WebsiteUser studentUser = new WebsiteUser();
        studentUser.setId(studentId);
        studentUser.setRoles(Set.of(role));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(courseMapper.toDto(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.addStudentToCourse(courseId, studentId);

        // Assert
        assertNotNull(result);
        assertFalse(course.getIsOpenForRegistration());
    }

    @Test
    public void testAddStudentToCourse_UserNotStudent() {
        // Arrange
        Long courseId = 1L;
        Long studentId = 2L;

        Role role = new Role();
        role.setRoleName("TEACHER");

        Course course = new Course();
        WebsiteUser user = new WebsiteUser();
        user.setRoles(Set.of(role));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> courseService.addStudentToCourse(courseId, studentId));
        assertEquals("User you are trying to add is not a student", exception.getMessage());
    }

    @Test
    public void testRemoveStudentFromCourse_Success() {
        // Arrange
        Long courseId = 1L;
        Long studentId = 2L;

        Course course = new Course();
        course.setId(courseId);
        WebsiteUser studentUser = new WebsiteUser();
        studentUser.setId(studentId);
        studentUser.addCourse(course);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(courseMapper.toDto(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.removeStudentFromCourse(courseId, studentId);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository, times(1)).save(studentUser);
    }

    @Test
    public void testRemoveStudentFromCourse_CourseNotFound() {
        // Arrange
        Long courseId = 1L;
        Long studentId = 2L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> courseService.removeStudentFromCourse(courseId, studentId));
        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    public void testRemoveStudentFromCourse_StudentNotFound() {
        // Arrange
        Long courseId = 1L;
        Long studentId = 2L;

        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> courseService.removeStudentFromCourse(courseId, studentId));
        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    public void testEditCourse_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        CourseDTO courseDTO = new CourseDTO();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.editCourse(courseId, courseDTO);

        // Assert
        assertNotNull(result);
        verify(courseMapper, times(1)).partialUpdate(courseDTO, course);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testEditCourse_CourseNotFound() {
        // Arrange
        Long courseId = 1L;
        CourseDTO courseDTO = new CourseDTO();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> courseService.editCourse(courseId, courseDTO));
        assertEquals("Course not found", exception.getMessage());
    }

    // 7. Test for openCourseForRegistration
    @Test
    public void testOpenCourseForRegistration_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.openCourseForRegistration(courseId);

        // Assert
        assertNotNull(result);
        assertTrue(course.getIsOpenForRegistration());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testFinishCourse_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.finishCourse(courseId);

        // Assert
        assertNotNull(result);
        assertTrue(course.getIsDone());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testCancelCourse_Success() {
        // Arrange
        Long courseId = 1L;
        ScheduleChangesLogDTO scheduleChangesLogDTO = new ScheduleChangesLogDTO();
        Course course = new Course();
        course.setId(courseId);
        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(10L);
        course.setTutoringClass(tutoringClass);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        courseService.cancelCourse(courseId, scheduleChangesLogDTO);

        // Assert
        assertTrue(course.getIsDone());
        verify(tutoringClassService, times(1)).cancelTheRestOfTutoringClass(10L, scheduleChangesLogDTO);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testDeleteCourse_Success() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setIsOpenForRegistration(false);
        course.setStudents(Collections.emptyList());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        courseService.deleteCourse(courseId);

        // Assert
        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    public void testDeleteCourse_OpenForRegistration() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setIsOpenForRegistration(true);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.deleteCourse(courseId));
        assertEquals("You cant delete a course that is already open for registration", exception.getMessage());
    }

    @Test
    public void testDeleteCourse_StudentsRegistered() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setIsOpenForRegistration(false);
        course.setStudents(Collections.singletonList(new WebsiteUser()));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.deleteCourse(courseId));
        assertEquals("You cant delete a course that people are already registered to", exception.getMessage());
    }
}
