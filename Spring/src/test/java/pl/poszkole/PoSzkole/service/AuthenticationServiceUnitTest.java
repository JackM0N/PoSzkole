package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.security.JWTService;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthenticationServiceUnitTest {
    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private WebsiteUserMapper websiteUserMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private WebsiteUserDTO testUserDTO;
    private WebsiteUser testUser;
    private Role studentRole;
    private Role teacherRole;
    private Role managerRole;

    @BeforeEach
    void setUp() {
        // Mock user DTO
        testUserDTO = new WebsiteUserDTO();
        testUserDTO.setUsername("testUser");
        testUserDTO.setPassword("password123");

        // Mock possible user roles
        studentRole = new Role();
        studentRole.setRoleName("STUDENT");

        teacherRole = new Role();
        teacherRole.setRoleName("TEACHER");

        managerRole = new Role();
        managerRole.setRoleName("MANAGER");

        // Mock test user
        testUser = new WebsiteUser();
        testUser.setId(10001L);
        testUser.setUsername("testUser");
        testUser.setRoles(Set.of(studentRole, teacherRole));
    }

    @Test
    void testRegisterStudent_Success() {
        // Arrange
        when(websiteUserMapper.toEntity(testUserDTO)).thenReturn(testUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(websiteUserRepository.findHighestIdInRange(10000L, 99999L)).thenReturn(10000L);
        when(roleRepository.findByRoleName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.registerStudent(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        verify(websiteUserRepository, times(1)).save(testUser);
    }

    @Test
    void testRegisterTeacher_Success() {
        // Arrange
        when(websiteUserMapper.toEntity(testUserDTO)).thenReturn(testUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(websiteUserRepository.findHighestIdInRange(1000L, 9999L)).thenReturn(1000L);
        when(roleRepository.findByRoleName("TEACHER")).thenReturn(Optional.of(teacherRole));
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.registerTeacher(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        verify(websiteUserRepository, times(1)).save(testUser);
    }

    @Test
    void testRegisterManager_Success() {
        // Arrange
        when(websiteUserMapper.toEntity(testUserDTO)).thenReturn(testUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("MANAGER")).thenReturn(Optional.of(managerRole));
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.registerManager(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        verify(websiteUserRepository, times(1)).save(testUser);
    }

    @Test
    void testLogin_Success() throws AuthenticationException {
        // Arrange
        when(websiteUserRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        testUser.setIsDeleted(false);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        // Act
        AuthenticationResponse response = authenticationService.login(testUserDTO);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserDeleted() {
        // Arrange
        testUser.setIsDeleted(true);
        when(websiteUserRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // Act and assert
        assertThrows(AuthenticationException.class, () -> authenticationService.login(testUserDTO));
    }

    @Test
    void testChangeUserRoles_Success() {
        // Arrange
        when(websiteUserRepository.findById(10001L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName("MANAGER")).thenReturn(Optional.of(managerRole));

        // Act
        authenticationService.changeUserRoles(10001L, Set.of(managerRole));

        // Assert
        verify(websiteUserRepository, times(1)).save(testUser);
        assertTrue(testUser.getRoles().contains(managerRole));
    }

    @Test
    void testChangeUserRoles_RoleNotFound() {
        // Arrange
        Role nonExistentRole = new Role();
        nonExistentRole.setRoleName("NON_EXISTENT");

        // Act
        when(websiteUserRepository.findById(10001L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName("NON_EXISTENT")).thenReturn(Optional.empty());

        // Assert
        assertThrows(EntityNotFoundException.class, () ->
                authenticationService.changeUserRoles(10001L, Set.of(nonExistentRole)));
    }
}
