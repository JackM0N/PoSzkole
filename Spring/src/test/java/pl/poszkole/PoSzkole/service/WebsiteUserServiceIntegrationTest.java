package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WebsiteUserServiceIntegrationTest {

    @Autowired
    private WebsiteUserService websiteUserService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    private WebsiteUser testUser;
    private WebsiteUser teacher;

    @BeforeEach
    void setUp() {
        // Create test users in the database
        testUser = new WebsiteUser();
        testUser.setId(999999L);
        testUser.setUsername("testUser");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        testUser.setGender("M");
        websiteUserRepository.save(testUser);

        Role managerRole = roleRepository.findByRoleName("MANAGER").get();

        WebsiteUser manager = new WebsiteUser();
        manager.setId(999998L);
        manager.setUsername("testManager");
        manager.setPassword("password123");
        manager.setFirstName("Jane");
        manager.setLastName("Doe");
        manager.setEmail("jane.doe@example.com");
        manager.setPhone("0987654321");
        manager.setGender("F");
        manager.setRoles(Set.of(managerRole));
        websiteUserRepository.save(manager);

        Role teacherRole = roleRepository.findByRoleName("TEACHER").get();

        teacher = new WebsiteUser();
        teacher.setId(999997L);
        teacher.setUsername("testTeacher");
        teacher.setPassword("password123");
        teacher.setFirstName("Jerry");
        teacher.setLastName("Doe");
        teacher.setEmail("jerry.doe@example.com");
        teacher.setPhone("7894561230");
        teacher.setGender("M");
        teacher.setRoles(Set.of(teacherRole));
        teacher.setSubjects(Collections.emptySet());
        websiteUserRepository.save(teacher);

    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetCurrentUserProfile_Success() {
        // Act
        WebsiteUserDTO result = websiteUserService.getCurrentUserProfile();

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testGetUserProfile_Success() {
        // Act
        WebsiteUserDTO result = websiteUserService.getUserProfile(testUser.getId());

        System.out.println(result);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void testGetUserProfile_USER_NOT_FOUND() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> websiteUserService.getUserProfile(10L));
    }

    @Test
    void testGetAllStudents_Success() {
        // Act
        List<WebsiteUserDTO> result = websiteUserService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllTeachers_Success() {
        // Act
        List<WebsiteUserDTO> result = websiteUserService.getAllTeachers();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testEditUserProfile_Success() {
        // Arrange
        WebsiteUserDTO updatedUserDTO = new WebsiteUserDTO();
        updatedUserDTO.setId(testUser.getId());
        updatedUserDTO.setFirstName("Updated");
        updatedUserDTO.setLastName("Name");

        // Act
        WebsiteUserDTO result = websiteUserService.editUserProfile(updatedUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
    }

    @Test
    @WithMockUser(username = "testTeacher")
    void testEditTeachersSubjects_Success() {
        // Arrange
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setSubjectName("Matematyka");

        // Act
        WebsiteUserDTO result = websiteUserService.editTeachersSubjects(teacher.getId(), Collections.singletonList(subjectDTO));

        // Assert
        assertNotNull(result);
        assertTrue(result.getSubjects().stream().anyMatch(subject -> "Matematyka".equals(subject.getSubjectName())));
    }

    @Test
    void testRestoreUser_Success() {
        // Act
        WebsiteUserDTO result = websiteUserService.restoreUser(testUser.getId());

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsDeleted());
    }

    @Test
    @WithMockUser(username = "testManager")
    void testDeleteUser_Success() {
        // Act
        WebsiteUserDTO result = websiteUserService.deleteUser(testUser.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDeleted());
    }

    @Test
    @WithMockUser(username = "testManager")
    void testDeleteUser_USER_NOT_FOUND() {
        // Act & Assert
        // Try deleting non-existing user
        assertThrows(EntityNotFoundException.class, () -> websiteUserService.deleteUser(10L));
    }

    @Test
    @WithMockUser(username = "testTeacher")
    void testDeleteUser_PERMISSION_DENIED() {
        // Act & Assert
        // Try deleting a user without permission
        assertThrows(RuntimeException.class, () -> websiteUserService.deleteUser(testUser.getId()));
    }
}