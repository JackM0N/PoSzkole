package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.SimplifiedUserDTO;
import pl.poszkole.PoSzkole.dto.UserBusyDayDTO;
import pl.poszkole.PoSzkole.mapper.UserBusyDayMapper;
import pl.poszkole.PoSzkole.model.UserBusyDay;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.UserBusyDayRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserBusyDayServiceUnitTest {

    @Mock
    private UserBusyDayRepository userBusyDayRepository;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private UserBusyDayMapper userBusyDayMapper;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private UserBusyDayService userBusyDayService;

    @Test
    void testGetUserBusyDays_Success() {
        // Arrange
        Long userId = 1L;

        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(userId);

        List<UserBusyDay> mockBusyDays = List.of(new UserBusyDay());
        List<UserBusyDayDTO> expectedDTOs = List.of(new UserBusyDayDTO());

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userBusyDayRepository.findByUserIdOrderByTimeTo(userId)).thenReturn(mockBusyDays);
        when(userBusyDayMapper.toDto(any(UserBusyDay.class))).thenReturn(expectedDTOs.get(0));

        // Act
        List<UserBusyDayDTO> result = userBusyDayService.getUserBusyDays(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTOs.size(), result.size());
        verify(websiteUserRepository).findById(userId);
        verify(userBusyDayRepository).findByUserIdOrderByTimeTo(userId);
    }

    @Test
    void testGetUserBusyDays_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(websiteUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userBusyDayService.getUserBusyDays(userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateUserBusyDay_Success() {
        // Arrange
        Long userId = 1L;

        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(userId);

        SimplifiedUserDTO mockUserDTO = new SimplifiedUserDTO();
        mockUserDTO.setId(userId);

        UserBusyDayDTO userBusyDayDTO = new UserBusyDayDTO();
        userBusyDayDTO.setUser(mockUserDTO);

        UserBusyDay userBusyDay = new UserBusyDay();
        UserBusyDayDTO expectedDTO = new UserBusyDayDTO();

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(authorizationService.cantModifyEntity(any())).thenReturn(false);
        when(userBusyDayMapper.toEntity(any())).thenReturn(userBusyDay);
        when(userBusyDayRepository.save(any())).thenReturn(userBusyDay);
        when(userBusyDayMapper.toDto(any())).thenReturn(expectedDTO);

        // Act
        UserBusyDayDTO result = userBusyDayService.createUserBusyDay(userBusyDayDTO);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository).findById(userId);
        verify(userBusyDayRepository).save(userBusyDay);
    }

    @Test
    void testCreateUserBusyDay_NoPermission() {
        // Arrange
        Long userId = 1L;

        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(userId);

        SimplifiedUserDTO mockUserDTO = new SimplifiedUserDTO();
        mockUserDTO.setId(userId);

        UserBusyDayDTO userBusyDayDTO = new UserBusyDayDTO();
        userBusyDayDTO.setUser(mockUserDTO); // Set the user from the simplified DTO

        UserBusyDay userBusyDay = new UserBusyDay();
        userBusyDay.setUser(mockUser); // Ensure the user is set here

        // Mock the repository and authorization service
        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userBusyDayMapper.toEntity(any())).thenReturn(userBusyDay);
        when(authorizationService.cantModifyEntity(any(UserBusyDay.class))).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userBusyDayService.createUserBusyDay(userBusyDayDTO));
        assertEquals("You do not have permission to create this schedule", exception.getMessage());
    }


    @Test
    void testUpdateUserBusyDay_Success() {
        // Arrange
        Long bdId = 1L;
        Long userId = 2L;

        // Mock input UserBusyDayDTO
        UserBusyDayDTO userBusyDayDTO = new UserBusyDayDTO();
        userBusyDayDTO.setId(bdId);
        userBusyDayDTO.setDayOfTheWeek(DayOfWeek.MONDAY);
        userBusyDayDTO.setTimeFrom(LocalTime.of(9, 0));
        userBusyDayDTO.setTimeTo(LocalTime.of(10, 0));

        // Mock WebsiteUser
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(userId);

        // Mock existing UserBusyDay in the repository
        UserBusyDay userBusyDay = new UserBusyDay();
        userBusyDay.setId(bdId);
        userBusyDay.setUser(mockUser); // Properly set the user
        userBusyDay.setDayOfTheWeek("MONDAY");
        userBusyDay.setTimeFrom(LocalTime.of(8, 0));
        userBusyDay.setTimeTo(LocalTime.of(9, 0));

        when(userBusyDayRepository.findById(bdId)).thenReturn(Optional.of(userBusyDay));
        when(authorizationService.cantModifyEntity(userBusyDay)).thenReturn(false);
        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userBusyDayRepository.findByUserIdOrderByTimeTo(userId)).thenReturn(List.of(userBusyDay));

        // Mock mapper behavior
        UserBusyDayDTO expectedDTO = new UserBusyDayDTO();
        when(userBusyDayMapper.toDto(any())).thenReturn(expectedDTO);

        // Act
        UserBusyDayDTO result = userBusyDayService.updateUserBusyDay(bdId, userBusyDayDTO);

        // Assert
        assertNotNull(result); // Verify that the result is not null
        verify(userBusyDayRepository).findById(bdId); // Verify that the repository was called
        verify(userBusyDayRepository).save(userBusyDay); // Verify that the save method was called
    }

    @Test
    void testUpdateUserBusyDay_ScheduleNotFound() {
        // Arrange
        Long bdId = 1L;
        UserBusyDayDTO userBusyDayDTO = new UserBusyDayDTO();

        when(userBusyDayRepository.findById(bdId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userBusyDayService.updateUserBusyDay(bdId, userBusyDayDTO));
        assertEquals("User schedule not found", exception.getMessage());
    }

    @Test
    void testDeleteUserBusyDay_Success() {
        // Arrange
        Long bdId = 1L;
        UserBusyDay mockEntity = new UserBusyDay();

        when(userBusyDayRepository.findById(bdId)).thenReturn(Optional.of(mockEntity));
        when(authorizationService.cantModifyEntity(mockEntity)).thenReturn(false);

        // Act
        userBusyDayService.deleteUserBusyDay(bdId);

        // Assert
        verify(userBusyDayRepository).deleteById(bdId);
    }

    @Test
    void testDeleteUserBusyDay_NoPermission() {
        // Arrange
        Long bdId = 1L;
        UserBusyDay mockEntity = new UserBusyDay();

        when(userBusyDayRepository.findById(bdId)).thenReturn(Optional.of(mockEntity));
        when(authorizationService.cantModifyEntity(mockEntity)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userBusyDayService.deleteUserBusyDay(bdId));
        assertEquals("You do not have permission to delete this schedule", exception.getMessage());
    }

    @Test
    void testIsOverlapping_NoOverlap() {
        // Arrange
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(1L);

        UserBusyDay busyDay = new UserBusyDay();
        busyDay.setDayOfTheWeek(String.valueOf(DayOfWeek.MONDAY));
        busyDay.setTimeFrom(LocalTime.of(9, 0));
        busyDay.setTimeTo(LocalTime.of(11, 0));

        List<UserBusyDay> mockBusyDays = List.of(busyDay);
        LocalTime timeFrom = LocalTime.of(13, 0);
        LocalTime timeTo = LocalTime.of(14, 0);

        when(userBusyDayRepository.findByUserIdOrderByTimeTo(mockUser.getId())).thenReturn(mockBusyDays);

        // Act
        boolean result = userBusyDayService.isOverlapping(mockUser, null, DayOfWeek.MONDAY, timeFrom, timeTo);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsOverlapping_OverlapAtStart() {
        // Arrange
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(1L);

        UserBusyDay busyDay = new UserBusyDay();
        busyDay.setDayOfTheWeek(String.valueOf(DayOfWeek.MONDAY));
        busyDay.setTimeFrom(LocalTime.of(9, 0));
        busyDay.setTimeTo(LocalTime.of(11, 0));

        List<UserBusyDay> mockBusyDays = List.of(busyDay);

        LocalTime timeFrom = LocalTime.of(10, 0);
        LocalTime timeTo = LocalTime.of(12, 0);

        when(userBusyDayRepository.findByUserIdOrderByTimeTo(mockUser.getId())).thenReturn(mockBusyDays);

        // Act
        boolean result = userBusyDayService.isOverlapping(mockUser, null, DayOfWeek.MONDAY, timeFrom, timeTo);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsOverlapping_OverlapAtEnd() {
        // Arrange
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(1L);

        UserBusyDay busyDay = new UserBusyDay();
        busyDay.setDayOfTheWeek(String.valueOf(DayOfWeek.MONDAY));
        busyDay.setTimeFrom(LocalTime.of(9, 0));
        busyDay.setTimeTo(LocalTime.of(11, 0));

        List<UserBusyDay> mockBusyDays = List.of(busyDay);

        LocalTime timeFrom = LocalTime.of(8, 0);
        LocalTime timeTo = LocalTime.of(10, 0);

        when(userBusyDayRepository.findByUserIdOrderByTimeTo(mockUser.getId())).thenReturn(mockBusyDays);

        // Act
        boolean result = userBusyDayService.isOverlapping(mockUser, null, DayOfWeek.MONDAY, timeFrom, timeTo);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsOverlapping_OverlapAtBothEnds() {
        // Arrange
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(1L);

        UserBusyDay busyDay = new UserBusyDay();
        busyDay.setDayOfTheWeek(String.valueOf(DayOfWeek.MONDAY));
        busyDay.setTimeFrom(LocalTime.of(9, 0));
        busyDay.setTimeTo(LocalTime.of(11, 0));

        List<UserBusyDay> mockBusyDays = List.of(busyDay);

        LocalTime timeFrom = LocalTime.of(8, 0);
        LocalTime timeTo = LocalTime.of(12, 0);

        when(userBusyDayRepository.findByUserIdOrderByTimeTo(mockUser.getId())).thenReturn(mockBusyDays);

        // Act
        boolean result = userBusyDayService.isOverlapping(mockUser, null, DayOfWeek.MONDAY, timeFrom, timeTo);

        // Assert
        assertTrue(result);
    }

}