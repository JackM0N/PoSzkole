package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthenticationServiceIntegrationTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    WebsiteUserDTO newUserDTO;
    WebsiteUser manager;

    @BeforeEach
    void setup() {
        manager = new WebsiteUser();
        manager.setId(1100L);
        manager.setUsername("jane.doe");
        manager.setPassword(passwordEncoder.encode("securepassword1"));
        manager.setFirstName("Jane");
        manager.setLastName("Doe");
        manager.setGender("F");
        manager.setEmail("jane.doe@example.com");
        manager.setPhone("0987654321");
        manager.setIsDeleted(false);
        manager.setRoles(Set.of(roleRepository.findByRoleName("MANAGER").get()));
        websiteUserRepository.save(manager);

        newUserDTO = new WebsiteUserDTO();
        newUserDTO.setUsername("john.doe");
        newUserDTO.setPassword("securepassword2");
        newUserDTO.setFirstName("John");
        newUserDTO.setLastName("Doe");
        newUserDTO.setGender("M");
        newUserDTO.setEmail("john.doe@example.com");
        newUserDTO.setPhone("123456789");
        newUserDTO.setIsDeleted(false);
    }

    @Test
    @WithMockUser(username = "jane.doe")
    void testRegisterStudent_Success() {
        // Arrange
        newUserDTO.setUsername("student1");

        // Act
        AuthenticationResponse response = authenticationService.registerStudent(newUserDTO);

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());

        WebsiteUser savedUser = websiteUserRepository.findByUsername("student1")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals("student1", savedUser.getUsername());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("STUDENT")));
    }

    @Test
    @WithMockUser(username = "jane.doe")
    void testRegisterTeacher_Success() {
        // Arrange
        newUserDTO.setUsername("teacher1");

        // Act
        AuthenticationResponse response = authenticationService.registerTeacher(newUserDTO);

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());

        WebsiteUser savedUser = websiteUserRepository.findByUsername("teacher1")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals("teacher1", savedUser.getUsername());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("TEACHER")));
    }

    @Test
    @WithMockUser(username = "jane.doe")
    void testRegisterManager_Success() {
        // Arrange
        newUserDTO.setUsername("manager1");

        // Act
        AuthenticationResponse response = authenticationService.registerManager(newUserDTO);

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());

        WebsiteUser savedUser = websiteUserRepository.findByUsername("manager1")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals("manager1", savedUser.getUsername());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("MANAGER")));
    }

    @Test
    void testLogin_Success() throws AuthenticationException {
        // Arrange
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setUsername("jane.doe");
        request.setPassword("securepassword1");

        // Act
        AuthenticationResponse response = authenticationService.login(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());
    }

    @Test
    void testLogin_IncorrectPassword() {
        // Arrange
        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setUsername("jane.doe");
        request.setPassword("wrongPassword");

        // Act and assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
    }

    @Test
    void testLogin_UserDeleted() {
        // Arrange
        manager.setIsDeleted(true);
        websiteUserRepository.save(manager);

        WebsiteUserDTO request = new WebsiteUserDTO();
        request.setUsername("jane.doe");
        request.setPassword(passwordEncoder.encode("securepassword1"));

        // Act and assert
        assertThrows(AuthenticationException.class, () -> authenticationService.login(request));
    }

    @Test
    @WithMockUser(username = "jane.doe")
    void testChangeUserRoles_Success() {
        // Arrange
        Set<Role> newRoles = new HashSet<>();
        newRoles.add(roleRepository.findByRoleName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Role not found")));
        newRoles.add(roleRepository.findByRoleName("TEACHER")
                .orElseThrow(() -> new RuntimeException("Role not found")));

        // Act
        authenticationService.changeUserRoles(1100L, newRoles);

        // Assert
        WebsiteUser updatedUser = websiteUserRepository.findById(1100L)
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals(2, updatedUser.getRoles().size());
        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("MANAGER")));
        assertTrue(updatedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("TEACHER")));
    }
}

