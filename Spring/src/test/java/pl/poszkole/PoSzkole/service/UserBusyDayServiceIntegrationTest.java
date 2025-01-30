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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserBusyDayServiceIntegrationTest {
    @Autowired
    private UserBusyDayService userBusyDayService;

    @Autowired
    private UserBusyDayRepository userBusyDayRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    WebsiteUser student;
    UserBusyDay busyDay;

    @BeforeEach
    void setup() {
        // Arrange
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

        busyDay = new UserBusyDay();
        busyDay.setUser(student);
        busyDay.setDayOfTheWeek("MONDAY");
        busyDay.setTimeFrom(LocalTime.of(9, 0));
        busyDay.setTimeTo(LocalTime.of(11, 0));
        userBusyDayRepository.save(busyDay);
    }

    @Test
    void testGetUserBusyDays_Success() {
        // Act
        List<UserBusyDayDTO> result = userBusyDayService.getUserBusyDays(student.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DayOfWeek.MONDAY, result.get(0).getDayOfTheWeek());
    }

    @Test
    @WithMockUser(username = "student")
    void testCreateUserBusyDay_Success() {
        // Arrange
        SimplifiedUserDTO simpleUser = new SimplifiedUserDTO();
        simpleUser.setId(student.getId());
        simpleUser.setFirstName("Jane");
        simpleUser.setLastName("Doe");

        UserBusyDayDTO newBusyDayDTO = new UserBusyDayDTO();
        newBusyDayDTO.setUser(simpleUser);
        newBusyDayDTO.setDayOfTheWeek(DayOfWeek.TUESDAY);
        newBusyDayDTO.setTimeFrom(LocalTime.of(14, 0));
        newBusyDayDTO.setTimeTo(LocalTime.of(16, 0));

        // Act
        UserBusyDayDTO createdBusyDay = userBusyDayService.createUserBusyDay(newBusyDayDTO);

        // Assert
        assertNotNull(createdBusyDay);
        assertEquals(DayOfWeek.TUESDAY, createdBusyDay.getDayOfTheWeek());
        assertEquals(LocalTime.of(14, 0), createdBusyDay.getTimeFrom());
        assertEquals(LocalTime.of(16, 0), createdBusyDay.getTimeTo());
    }

    @Test
    void testCreateUserBusyDay_OverlappingSchedule() {
        // Arrange
        SimplifiedUserDTO simpleUser = new SimplifiedUserDTO();
        simpleUser.setId(student.getId());
        simpleUser.setFirstName("Jane");
        simpleUser.setLastName("Doe");

        UserBusyDayDTO overlappingBusyDayDTO = new UserBusyDayDTO();
        overlappingBusyDayDTO.setUser(simpleUser);
        overlappingBusyDayDTO.setDayOfTheWeek(DayOfWeek.MONDAY);
        overlappingBusyDayDTO.setTimeFrom(LocalTime.of(10, 0));
        overlappingBusyDayDTO.setTimeTo(LocalTime.of(12, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userBusyDayService.createUserBusyDay(overlappingBusyDayDTO));
        assertEquals("Chosen schedule is overlapping with already existing one", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "student")
    void testUpdateUserBusyDay_Success() {
        // Arrange
        UserBusyDayDTO updateDTO = new UserBusyDayDTO();
        updateDTO.setId(busyDay.getId());
        updateDTO.setDayOfTheWeek(DayOfWeek.WEDNESDAY);
        updateDTO.setTimeFrom(LocalTime.of(15, 0));
        updateDTO.setTimeTo(LocalTime.of(17, 0));

        // Act
        UserBusyDayDTO updatedBusyDay = userBusyDayService.updateUserBusyDay(busyDay.getId(), updateDTO);

        // Assert
        assertNotNull(updatedBusyDay);
        assertEquals(DayOfWeek.WEDNESDAY, updatedBusyDay.getDayOfTheWeek());
        assertEquals(LocalTime.of(15, 0), updatedBusyDay.getTimeFrom());
        assertEquals(LocalTime.of(17, 0), updatedBusyDay.getTimeTo());
    }

    @Test
    @WithMockUser(username = "student")
    void testUpdateUserBusyDay_OverlappingSchedule() {
        // Arrange
        UserBusyDayDTO overlappingBDDTO = new UserBusyDayDTO();
        overlappingBDDTO.setId(busyDay.getId() + 1);
        overlappingBDDTO.setDayOfTheWeek(DayOfWeek.MONDAY);
        overlappingBDDTO.setTimeFrom(LocalTime.of(10, 0)); // Overlaps with 9:00-11:00
        overlappingBDDTO.setTimeTo(LocalTime.of(12, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userBusyDayService.updateUserBusyDay(busyDay.getId(), overlappingBDDTO));
        assertEquals("Chosen schedule is overlapping with already existing one", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "student")
    void testDeleteUserBusyDay_Success() {
        // Act
        userBusyDayService.deleteUserBusyDay(busyDay.getId());

        // Assert
        assertFalse(userBusyDayRepository.existsById(busyDay.getId()));
    }

    @Test
    @WithMockUser(username = "student2")
    void testDeleteUserBusyDay_NoPermission() {
        // Arrange
        WebsiteUser newStudent = new WebsiteUser();
        newStudent.setId(99999L);
        newStudent.setUsername("student2");
        newStudent.setPassword("student1232");
        newStudent.setFirstName("John");
        newStudent.setLastName("Doe");
        newStudent.setEmail("john.doe@student.com");
        newStudent.setIsDeleted(false);
        newStudent.setGender("M");
        newStudent.setPhone("123456798");
        newStudent.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(newStudent);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userBusyDayService.deleteUserBusyDay(busyDay.getId()));
        assertEquals("You do not have permission to delete this schedule", exception.getMessage());
    }

    @Test
    void testIsOverlapping_Overlapping() {
        // Act
        boolean result = userBusyDayService.isOverlapping(student, null, DayOfWeek.MONDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsOverlapping_NoOverlap() {
        // Act
        boolean result = userBusyDayService.isOverlapping(student, null, DayOfWeek.TUESDAY,
                LocalTime.of(12, 0), LocalTime.of(14, 0)); // No overlap

        // Assert
        assertFalse(result);
    }
}