package com.example.hotelbookingwebsite.repository;

import com.example.hotelbookingwebsite.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(Room.RoomStatus status);

    List<Room> findByRoomType(Room.RoomType roomType);

    List<Room> findByStatusAndRoomType(Room.RoomStatus status, Room.RoomType roomType);

    @Query("""
        SELECT r FROM Room r
        WHERE r.capacity >= :guests
        AND r.status = 'AVAILABLE'
        AND r.id NOT IN (
            SELECT b.room.id FROM Booking b
            WHERE b.status IN ('PENDING','CONFIRMED')
            AND b.checkInDate < :checkOut
            AND b.checkOutDate > :checkIn
        )
    """)
    List<Room> findAvailableRooms(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guests") int guests
    );
}