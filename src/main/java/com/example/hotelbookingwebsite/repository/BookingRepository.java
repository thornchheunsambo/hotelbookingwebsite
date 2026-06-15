package com.example.hotelbookingwebsite.repository;

import com.example.hotelbookingwebsite.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByRoomId(Long roomId);

    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByStatus(Booking.BookingStatus status);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}