package com.example.hotelbookingwebsite.service;

import com.example.hotelbookingwebsite.dto.request.ChangePasswordRequest;
import com.example.hotelbookingwebsite.dto.request.UpdateUserRequest;
import com.example.hotelbookingwebsite.dto.response.UserResponse;
import com.example.hotelbookingwebsite.entity.User;
import com.example.hotelbookingwebsite.exception.BadRequestException;
import com.example.hotelbookingwebsite.exception.ResourceNotFoundException;
import com.example.hotelbookingwebsite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(String username) {
        User user = findByUsername(username);
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateUserRequest request) {
        User user = findByUsername(username);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail())
                    && !user.getEmail().equals(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = findByUsername(username);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ─── Admin ───────────────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id)));
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setIsActive(!user.getIsActive());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse promoteToAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setRole(User.Role.ROLE_ADMIN);
        return toResponse(userRepository.save(user));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .role(u.getRole())
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}