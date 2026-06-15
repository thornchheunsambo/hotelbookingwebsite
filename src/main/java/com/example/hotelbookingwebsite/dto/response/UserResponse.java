package com.example.hotelbookingwebsite.dto.response;

import com.example.hotelbookingwebsite.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private User.Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}