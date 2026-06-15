package com.example.hotelbookingwebsite.dto.request;

import com.example.hotelbookingwebsite.entity.Room;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {

    private String name;

    private Room.RoomType roomType;

    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    private String description;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Room.RoomStatus status;

    private String imageUrl;
}