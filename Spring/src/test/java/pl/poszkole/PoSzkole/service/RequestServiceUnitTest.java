package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.mapper.RequestMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.RequestRepository;
import pl.poszkole.PoSzkole.repository.SubjectRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RequestServiceUnitTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private TutoringClassMapper tutoringClassMapper;

    @Mock
    private TutoringClassRepository tutoringClassRepository;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private ClassScheduleService classScheduleService;

    @Mock
    private UserBusyDayService userBusyDayService;

    @Mock
    private TutoringClassService tutoringClassService;

    @InjectMocks
    private RequestService requestService;

    Role studentRole;
    Role teacherRole;

    @BeforeEach
    void setUp() {
        // Mock possible user roles
        studentRole = new Role();
        studentRole.setRoleName("STUDENT");

        teacherRole = new Role();
        teacherRole.setRoleName("TEACHER");
    }

    @Test
    void testGetRequestsForTeacher_Success() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "issueDate")
        );
        Subject subject = new Subject();
        WebsiteUser teacher = new WebsiteUser();
        teacher.setSubjects(Set.of(subject));
        teacher.setRoles(Set.of(teacherRole));
        Page<Request> requestPage = new PageImpl<>(List.of(new Request()));

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(requestRepository.findAll(any(Specification.class), eq(sortedPageable))).thenReturn(requestPage);
        when(requestMapper.toDto(any())).thenReturn(new RequestDTO());

        // Act
        Page<RequestDTO> result = requestService.getRequestsForTeacher(false, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(requestRepository).findAll(any(Specification.class), eq(sortedPageable));
    }

    @Test
    void testGetRequestsForTeacher_NotATeacher() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        WebsiteUser user = new WebsiteUser();
        user.setRoles(Set.of(studentRole));
        when(websiteUserService.getCurrentUser()).thenReturn(user);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                requestService.getRequestsForTeacher(false, null, pageable));
    }

    @Test
    void testCreateRequest_Success() throws BadRequestException {
        // Arrange
        WebsiteUserDTO simplifiedUserDTO = new WebsiteUserDTO();
        simplifiedUserDTO.setId(1L);

        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(1L);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setPrefersIndividual(null);
        requestDTO.setRepeatUntil(LocalDate.now().plusDays(1));
        requestDTO.setStudent(simplifiedUserDTO);
        requestDTO.setSubject(subjectDTO);

        WebsiteUser student = new WebsiteUser();
        student.setRoles(Set.of(studentRole));

        Subject subject = new Subject();
        when(websiteUserRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(requestMapper.toEntity(requestDTO)).thenReturn(new Request());
        when(requestMapper.toDto(any())).thenReturn(requestDTO);

        // Act
        RequestDTO result = requestService.createRequest(requestDTO);

        // Assert
        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void testCreateRequest_RepeatUntilPast() {
        // Arrange
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRepeatUntil(LocalDate.now().minusDays(1));
        Request request = new Request();
        request.setRepeatUntil(LocalDate.now().minusDays(2));

        when(requestMapper.toEntity(requestDTO)).thenReturn(request);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> requestService.createRequest(requestDTO));
    }

    @Test
    void testAdmitRequestCreateClass_Success() {
        // Arrange
        Long requestId = 1L;
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 30));

        WebsiteUser currentUser = new WebsiteUser();
        Request request = new Request();
        request.setStudent(new WebsiteUser());
        request.setSubject(new Subject());
        TutoringClass tutoringClass = new TutoringClass();

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(tutoringClassMapper.toEntity(tutoringClassDTO)).thenReturn(tutoringClass);
        when(websiteUserRepository.findById(any())).thenReturn(Optional.of(currentUser));
        when(requestMapper.toDto(request)).thenReturn(new RequestDTO());

        // Act
        RequestDTO result = requestService.admitRequestCreateClass(requestId, tutoringClassDTO, dayAndTimeDTO, true);

        // Assert
        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
        verify(tutoringClassRepository).save(any(TutoringClass.class));
    }

    @Test
    void testAdmitRequestCreateClass_InvalidTimeValues() {
        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(9, 0));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                requestService.admitRequestCreateClass(1L, new TutoringClassDTO(), dayAndTimeDTO, true));
    }

    @Test
    void testAdmitRequestCreateClass_RequestNotFound() {
        // Arrange
        Long requestId = 1L;
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 30));

        WebsiteUser currentUser = new WebsiteUser();

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.admitRequestCreateClass(1L, new TutoringClassDTO(), dayAndTimeDTO, true));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void testAdmitRequestAddToClass_Success() {
        // Arrange
        Long requestId = 1L;
        Long classId = 2L;

        WebsiteUser user = new WebsiteUser();
        user.setId(1L);

        Request request = new Request();
        request.setStudent(user);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(classId);

        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(tutoringClassRepository.findById(classId)).thenReturn(Optional.of(tutoringClass));
        when(requestMapper.toDto(request)).thenReturn(new RequestDTO());

        // Act
        RequestDTO result = requestService.admitRequestAddToClass(requestId, classId);

        // Assert
        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
        verify(tutoringClassService).addToTutoringClass(user.getId(), classId);
    }

    @Test
    void testAdmitRequestAddToClass_RequestNotFound() {
        // Arrange
        Long requestId = 1L;
        Long classId = 2L;

        WebsiteUser user = new WebsiteUser();
        user.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.admitRequestAddToClass(requestId, classId));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void testAdmitRequestAddToClass_ClassNotFound() {
        // Arrange
        Long requestId = 1L;
        Long classId = 2L;

        WebsiteUser user = new WebsiteUser();
        user.setId(1L);

        Request request = new Request();
        request.setStudent(user);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(classId);

        when(websiteUserService.getCurrentUser()).thenReturn(user);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(tutoringClassRepository.findById(classId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.admitRequestAddToClass(requestId, classId));
        assertEquals("Tutoring class not found", exception.getMessage());
    }
}
