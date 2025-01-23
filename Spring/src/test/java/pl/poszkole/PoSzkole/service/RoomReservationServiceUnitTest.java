package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.RoomDTO;
import pl.poszkole.PoSzkole.dto.RoomReservationDTO;
import pl.poszkole.PoSzkole.mapper.RoomMapper;
import pl.poszkole.PoSzkole.mapper.RoomReservationMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.Room;
import pl.poszkole.PoSzkole.model.RoomReservation;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.RoomRepository;
import pl.poszkole.PoSzkole.repository.RoomReservationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RoomReservationServiceUnitTest {
    @Mock
    private RoomReservationRepository roomReservationRepository;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private RoomReservationMapper roomReservationMapper;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private WebsiteUserService websiteUserService;

    @InjectMocks
    private RoomReservationService roomReservationService;

    @Test
    void testGetRoomsWithoutReservationForSchedule_Success() {
        // Arrange
        LocalDateTime timeFrom = LocalDateTime.now().plusDays(1);
        LocalDateTime timeTo = LocalDateTime.now().plusDays(1).plusHours(1);

        Room room1 = new Room();
        room1.setId(1L);
        Room room2 = new Room();
        room2.setId(2L);

        RoomReservation reservation = new RoomReservation();
        reservation.setRoom(room1);

        when(roomRepository.findAll()).thenReturn(new ArrayList<>(List.of(room1, room2)));
        when(roomReservationRepository.findOverlappingReservations(timeFrom, timeTo)).thenReturn(List.of(reservation));
        when(roomMapper.toDto(room2)).thenReturn(new RoomDTO());

        // Act
        List<RoomDTO> availableRooms = roomReservationService.getRoomsWithoutReservationForSchedule(timeFrom, timeTo);

        // Assert
        assertNotNull(availableRooms);
        assertEquals(1, availableRooms.size());
        verify(roomRepository, times(1)).findAll();
        verify(roomReservationRepository, times(1)).findOverlappingReservations(timeFrom, timeTo);
    }

    @Test
    void testCreateRoomReservationForSchedule_Success() {
        // Arrange
        Long roomId = 1L;
        Long classScheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(2));

        Room room = new Room();
        room.setId(roomId);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomReservationRepository.findOverlappingReservationsForRoom(
                roomId, classSchedule.getClassDateFrom(), classSchedule.getClassDateTo()
        )).thenReturn(Collections.emptyList());
        when(roomReservationMapper.toDto(any(RoomReservation.class))).thenReturn(new RoomReservationDTO());

        // Act
        RoomReservationDTO result = roomReservationService.createRoomReservationForSchedule(roomId, classScheduleId);

        // Assert
        assertNotNull(result);
        verify(roomReservationRepository, times(1)).save(any(RoomReservation.class));
        verify(classScheduleRepository, times(1)).save(classSchedule);
    }

    @Test
    void testCreateRoomReservationForSchedule_ScheduleNotFound() {
        // Arrange
        Long roomId = 1L;
        Long classScheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> roomReservationService.createRoomReservationForSchedule(roomId, classScheduleId));
        assertEquals("Class schedule not found", exception.getMessage());
    }

    @Test
    void testCreateRoomReservationForSchedule_RoomNotFound() {
        // Arrange
        Long roomId = 1L;
        Long classScheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(2));

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> roomReservationService.createRoomReservationForSchedule(roomId, classScheduleId));
        assertEquals("Room not found", exception.getMessage());
    }

    @Test
    void testCreateRoomReservationForSchedule_RoomIsReserved() {
        // Arrange
        Long roomId = 1L;
        Long classScheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(2));

        Room room = new Room();
        room.setId(roomId);

        RoomReservation reservation = new RoomReservation();
        reservation.setRoom(room);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomReservationRepository.findOverlappingReservationsForRoom(
                roomId, classSchedule.getClassDateFrom(), classSchedule.getClassDateTo()
        )).thenReturn(List.of(reservation));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> roomReservationService.createRoomReservationForSchedule(roomId, classScheduleId));
        assertEquals("There is already a reservation for this room", exception.getMessage());
    }
}
