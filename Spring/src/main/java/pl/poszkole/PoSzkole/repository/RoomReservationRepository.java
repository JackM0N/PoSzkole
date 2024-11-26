package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.RoomReservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {
    @Query("SELECT rr FROM RoomReservation rr " +
            "WHERE ((:startTime BETWEEN rr.reservationFrom AND rr.reservationTo) " +
            "OR (:endTime BETWEEN rr.reservationFrom AND rr.reservationTo) " +
            "OR (rr.reservationFrom BETWEEN :startTime AND :endTime) " +
            "OR (rr.reservationTo BETWEEN :startTime AND :endTime))")
    List<RoomReservation> findOverlappingReservations(
            @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT rr FROM RoomReservation rr " +
            "WHERE rr.room.id = :roomId " +
            "AND ((:startTime BETWEEN rr.reservationFrom AND rr.reservationTo) " +
            "OR (:endTime BETWEEN rr.reservationFrom AND rr.reservationTo) " +
            "OR (rr.reservationFrom BETWEEN :startTime AND :endTime) " +
            "OR (rr.reservationTo BETWEEN :startTime AND :endTime))")
    List<RoomReservation> findOverlappingReservationsForRoom(
            @Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
