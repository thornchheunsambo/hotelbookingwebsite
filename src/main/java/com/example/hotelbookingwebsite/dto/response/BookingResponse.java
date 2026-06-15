package com.example.hotelbookingwebsite.dto.response;


import com.example.hotelbookingwebsite.entity.Booking;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long userId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numGuests;
    private BigDecimal totalPrice;
    private String specialRequests;
    private Booking.BookingStatus status;
    private LocalDateTime createdAt;
}
