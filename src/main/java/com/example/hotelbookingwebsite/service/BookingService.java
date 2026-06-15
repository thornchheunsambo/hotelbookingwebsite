package com.example.hotelbookingwebsite.service;

import com.example.hotelbookingwebsite.dto.request.CreateBookingRequest;
import com.example.hotelbookingwebsite.dto.response.BookingResponse;
import com.example.hotelbookingwebsite.entity.Booking;
import com.example.hotelbookingwebsite.entity.Room;
import com.example.hotelbookingwebsite.entity.User;
import com.example.hotelbookingwebsite.exception.BadRequestException;
import com.example.hotelbookingwebsite.exception.ResourceNotFoundException;
import com.example.hotelbookingwebsite.repository.BookingRepository;
import com.example.hotelbookingwebsite.repository.RoomRepository;
import com.example.hotelbookingwebsite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(String username, CreateBookingRequest request) {
        User user = findUserByUsername(username);
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));

        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());

        if (room.getStatus() == Room.RoomStatus.MAINTENANCE) {
            throw new BadRequestException("Room is under maintenance and cannot be booked");
        }

        List<Room> available = roomRepository.findAvailableRooms(
                request.getCheckInDate(), request.getCheckOutDate(), request.getNumGuests());

        boolean isAvailable = available.stream().anyMatch(r -> r.getId().equals(room.getId()));
        if (!isAvailable) {
            throw new BadRequestException("Room is not available for the selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .bookingCode("BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numGuests(request.getNumGuests())
                .totalPrice(totalPrice)
                .specialRequests(request.getSpecialRequests())
                .status(Booking.BookingStatus.PENDING)
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getMyBookings(String username) {
        User user = findUserByUsername(username);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BookingResponse getBookingById(String username, Long bookingId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!isAdmin && !booking.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only view your own bookings");
        }
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(String username, Long bookingId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!isAdmin && !booking.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only cancel your own bookings");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    // ─── Admin ───────────────────────────────────────────────

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, Booking.BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        booking.setStatus(newStatus);
        return toResponse(bookingRepository.save(booking));
    }

    // ─── Helpers ────────────────────────────────────────────

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .bookingCode(b.getBookingCode())
                .userId(b.getUser().getId())
                .guestName(b.getUser().getFullName())
                .roomId(b.getRoom().getId())
                .roomNumber(b.getRoom().getRoomNumber())
                .roomName(b.getRoom().getName())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .numGuests(b.getNumGuests())
                .totalPrice(b.getTotalPrice())
                .specialRequests(b.getSpecialRequests())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .build();
    }
}