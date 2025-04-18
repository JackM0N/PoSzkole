package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomReservationService {
    private final RoomReservationRepository roomReservationRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final RoomReservationMapper roomReservationMapper;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final WebsiteUserService websiteUserService;

    public List<RoomDTO> getRoomsWithoutReservationForSchedule(LocalDateTime timeFrom, LocalDateTime timeTo){
        //Get all rooms
        List<Room> rooms = roomRepository.findAll();

        //Find reservations that overlap with schedule
        List<RoomReservation> roomReservations = roomReservationRepository.findOverlappingReservations(timeFrom, timeTo);

        //Get all reserved rooms for chosen schedules' timeframe
        List<Room> reservedRooms = new ArrayList<>();
        roomReservations.forEach(roomReservation -> reservedRooms.add(roomReservation.getRoom()));

        //Remove all rooms that are reserved
        rooms.removeAll(reservedRooms);

        return rooms.stream().map(roomMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RoomReservationDTO createRoomReservationForSchedule(Long roomId, Long classScheduleId){
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new RuntimeException("Class schedule not found"));
        if (classSchedule.getRoom() != null){
            RoomReservation existingReservation = roomReservationRepository.findByRoomIdAndTeacherIdAndReservationFromAndReservationTo(
                    classSchedule.getRoom().getId(),
                    currentUser.getId(),
                    classSchedule.getClassDateFrom(),
                    classSchedule.getClassDateTo()
            ).orElseThrow(() -> new RuntimeException("Class reservation not found"));
            roomReservationRepository.delete(existingReservation);
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        //Make sure there are no reservations for this room
        List<RoomReservation> roomReservations = roomReservationRepository.findOverlappingReservationsForRoom(
                room.getId(), classSchedule.getClassDateFrom(), classSchedule.getClassDateTo()
        );

        if (!roomReservations.isEmpty()){
            throw new RuntimeException("There is already a reservation for this room");
        }

        //Create new reservation
        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setRoom(room);
        roomReservation.setTeacher(currentUser);
        roomReservation.setReservationFrom(classSchedule.getClassDateFrom());
        roomReservation.setReservationTo(classSchedule.getClassDateTo());
        roomReservationRepository.save(roomReservation);

        classSchedule.setRoom(room);
        classScheduleRepository.save(classSchedule);

        return roomReservationMapper.toDto(roomReservation);
    }
}
