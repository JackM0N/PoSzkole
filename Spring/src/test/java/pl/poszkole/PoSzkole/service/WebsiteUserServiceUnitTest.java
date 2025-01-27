package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import pl.poszkole.PoSzkole.dto.SimplifiedUserDTO;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.filter.UserFilter;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationFacade;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WebsiteUserServiceUnitTest {

    @InjectMocks
    private WebsiteUserService websiteUserService;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private WebsiteUserMapper websiteUserMapper;

    @Mock
    private SimplifiedUserMapper simplifiedUserMapper;

    @Mock
    private Authentication authentication;

    private WebsiteUser mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new WebsiteUser();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setEmail("test@example.com");

        // Mock the authentication object and its behavior
        when(authentication.getName()).thenReturn("testUser");

        // Mock the authentication facade to return the mocked authentication
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testGetCurrentUserProfile_Success() {
        // Arrange
        when(authenticationFacade.getAuthentication().getName()).thenReturn(mockUser.getUsername());
        when(websiteUserRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));
        when(websiteUserMapper.toDto(mockUser)).thenReturn(new WebsiteUserDTO());

        // Act
        WebsiteUserDTO result = websiteUserService.getCurrentUserProfile();

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository, times(1)).findByUsername(mockUser.getUsername());
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        when(websiteUserRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(websiteUserMapper.toDtoWithoutSensitiveData(mockUser)).thenReturn(new WebsiteUserDTO());

        // Act
        WebsiteUserDTO result = websiteUserService.getUserProfile(1L);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllStudents_Success() {
        // Arrange
        when(websiteUserRepository.findByRoleNameAndNotDeleted("STUDENT"))
                .thenReturn(List.of(mockUser));
        when(websiteUserMapper.toDtoWithoutSensitiveData(any(WebsiteUser.class)))
                .thenReturn(new WebsiteUserDTO());

        // Act
        List<WebsiteUserDTO> result = websiteUserService.getAllStudents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(websiteUserRepository, times(1))
                .findByRoleNameAndNotDeleted("STUDENT");
    }

    @Test
    void testGetAllTeachers_Success() {
        // Arrange
        when(websiteUserRepository.findByRoleNameAndNotDeleted("TEACHER"))
                .thenReturn(List.of(mockUser));
        when(websiteUserMapper.toDtoWithoutSensitiveData(any(WebsiteUser.class)))
                .thenReturn(new WebsiteUserDTO());

        // Act
        List<WebsiteUserDTO> result = websiteUserService.getAllTeachers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(websiteUserRepository, times(1))
                .findByRoleNameAndNotDeleted("TEACHER");
    }

    @Test
    void testGetAllStudentsPageable_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        UserFilter filter = new UserFilter();
        Page<WebsiteUser> mockPage = new PageImpl<>(List.of(mockUser));

        when(websiteUserRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);
        when(simplifiedUserMapper.toSimplifiedUserDTO(any(WebsiteUser.class)))
                .thenReturn(new SimplifiedUserDTO());

        // Act
        Page<SimplifiedUserDTO> result = websiteUserService.getAllStudentsPageable(filter, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testEditOwnUserProfile_Success() {
        // Arrange
        WebsiteUserDTO updatedUserDTO = new WebsiteUserDTO();
        updatedUserDTO.setFirstName("Updated");

        when(authenticationFacade.getAuthentication().getName()).thenReturn(mockUser.getUsername());
        when(websiteUserRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));
        when(websiteUserMapper.partialProfileUpdate(updatedUserDTO, mockUser)).thenReturn(mockUser);
        when(websiteUserRepository.save(mockUser)).thenReturn(mockUser);
        when(websiteUserMapper.toDto(mockUser)).thenReturn(updatedUserDTO);

        // Act
        WebsiteUserDTO result = websiteUserService.editOwnUserProfile(updatedUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
    }

    @Test
    void editChosenUserProfile_Success() {
        // Arrange
        Long userId = mockUser.getId();

        WebsiteUserDTO updatedDTO = new WebsiteUserDTO();
        updatedDTO.setEmail("updated@example.com");

        WebsiteUser updatedUser = new WebsiteUser();
        updatedUser.setId(userId);
        updatedUser.setEmail("updated@example.com");

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(websiteUserMapper.partialFullProfileUpdate(updatedDTO, mockUser)).thenReturn(updatedUser);
        when(websiteUserRepository.save(mockUser)).thenReturn(mockUser);
        when(websiteUserMapper.toDto(mockUser)).thenReturn(updatedDTO);

        // Act
        WebsiteUserDTO result = websiteUserService.editChosenUserProfile(userId, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void editChosenUserProfile_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        WebsiteUserDTO updatedDTO = new WebsiteUserDTO();

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> websiteUserService.editChosenUserProfile(userId, updatedDTO));
        assertEquals("User not found", exception.getMessage());
        verifyNoInteractions(websiteUserMapper);
    }

    @Test
    void testRestoreUser_Success() {
        // Arrange
        when(websiteUserRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(websiteUserMapper.toDtoWithoutSensitiveData(mockUser)).thenReturn(new WebsiteUserDTO());

        // Act
        WebsiteUserDTO result = websiteUserService.restoreUser(1L);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        Role managerRole = new Role();
        managerRole.setId(1L);
        managerRole.setRoleName("MANAGER");

        Role studentRole = new Role();
        studentRole.setId(2L);
        studentRole.setRoleName("STUDENT");

        WebsiteUser mockCurrentUser = new WebsiteUser();
        mockCurrentUser.setId(1L);
        mockCurrentUser.setUsername("testUser");
        mockCurrentUser.setRoles(Set.of(managerRole));

        WebsiteUser userToDelete = new WebsiteUser();
        userToDelete.setId(2L);
        userToDelete.setRoles(Set.of(studentRole));
        userToDelete.setIsDeleted(false);

        WebsiteUserDTO userToDeleteDTO = new WebsiteUserDTO();
        userToDeleteDTO.setId(userToDelete.getId());

        when(websiteUserRepository.findByUsername("testUser")).thenReturn(Optional.of(mockCurrentUser));
        when(websiteUserRepository.findById(2L)).thenReturn(Optional.of(userToDelete));
        when(websiteUserMapper.toDtoWithoutSensitiveData(userToDelete)).thenReturn(userToDeleteDTO);


        // Act
        WebsiteUserDTO result = websiteUserService.deleteUser(2L);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository, times(1)).save(any(WebsiteUser.class));
    }
}