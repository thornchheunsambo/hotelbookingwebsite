package com.example.hotelbookingwebsite.dto.response;

import com.example.hotelbookingwebsite.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String name;
    private Room.RoomType roomType;
    private BigDecimal price;
    private String description;
    private Integer capacity;
    private Room.RoomStatus status;
    private String imageUrl;
    private LocalDateTime createdAt;
}