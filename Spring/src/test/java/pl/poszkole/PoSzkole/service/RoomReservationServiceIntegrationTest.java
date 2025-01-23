package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RoomReservationServiceIntegrationTest {

    @Autowired
    private RoomReservationService roomReservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    private Room room;
    private ClassSchedule classSchedule;

    @BeforeEach
    void setUp() {
        // Arrange
        roomReservationRepository.deleteAll();
        roomRepository.deleteAll();

        Subject subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);

        WebsiteUser teacher = new WebsiteUser();
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

        room = new Room();
        room.setId(999L);
        room.setBuilding("Budynek A");
        room.setFloor(3);
        room.setRoomNumber(301);
        room = roomRepository.save(room);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setTeacher(teacher);
        tutoringClass.setSubject(subject);
        tutoringClassRepository.save(tutoringClass);

        classSchedule = new ClassSchedule();
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(2));
        classScheduleRepository.save(classSchedule);
    }

    @Test
    void testGetRoomsWithoutReservationForSchedule_Success() {
        // Arrange
        LocalDateTime timeFrom = LocalDateTime.now().plusHours(1);
        LocalDateTime timeTo = LocalDateTime.now().plusHours(8);

        // Act
        List<RoomDTO> availableRooms = roomReservationService.getRoomsWithoutReservationForSchedule(timeFrom, timeTo);

        // Assert
        assertNotNull(availableRooms);
        assertEquals(1, availableRooms.size());
        assertEquals("Budynek A", availableRooms.get(0).getBuilding());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCreateRoomReservationForSchedule_Success() {
        // Act
        RoomReservationDTO roomReservationDTO =
                roomReservationService.createRoomReservationForSchedule(room.getId(), classSchedule.getId());

        // Assert
        assertNotNull(roomReservationDTO);
        assertNotNull(roomReservationDTO.getRoom());
        assertEquals(room.getId(), roomReservationDTO.getRoom().getId());

        // Verify room reservation in the database
        List<RoomReservation> reservations = roomReservationRepository.findAll();
        assertEquals(1, reservations.size());
        RoomReservation reservation = reservations.get(0);
        assertEquals(room.getId(), reservation.getRoom().getId());
        assertEquals(classSchedule.getClassDateFrom(), reservation.getReservationFrom());
        assertEquals(classSchedule.getClassDateTo(), reservation.getReservationTo());
    }
}
