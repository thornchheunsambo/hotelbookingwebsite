package com.example.hotelbookingwebsite.service;

import com.example.hotelbookingwebsite.dto.request.CreateRoomRequest;
import com.example.hotelbookingwebsite.dto.request.UpdateRoomRequest;
import com.example.hotelbookingwebsite.dto.response.RoomResponse;
import com.example.hotelbookingwebsite.entity.Room;
import com.example.hotelbookingwebsite.exception.BadRequestException;
import com.example.hotelbookingwebsite.exception.ConflictException;
import com.example.hotelbookingwebsite.exception.ResourceNotFoundException;
import com.example.hotelbookingwebsite.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.AVAILABLE).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomResponse> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        return roomRepository.findAvailableRooms(checkIn, checkOut, guests).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new ConflictException("Room number '" + request.getRoomNumber() + "' already exists");
        }
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .name(request.getName())
                .roomType(request.getRoomType())
                .price(request.getPrice())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .imageUrl(request.getImageUrl())
                .status(Room.RoomStatus.AVAILABLE)
                .build();
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = findById(id);
        if (request.getName() != null) room.setName(request.getName());
        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getPrice() != null) room.setPrice(request.getPrice());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getStatus() != null) room.setStatus(request.getStatus());
        if (request.getImageUrl() != null) room.setImageUrl(request.getImageUrl());
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = findById(id);
        roomRepository.delete(room);
    }

    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .name(room.getName())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .description(room.getDescription())
                .capacity(room.getCapacity())
                .status(room.getStatus())
                .imageUrl(room.getImageUrl())
                .createdAt(room.getCreatedAt())
                .build();
    }
}