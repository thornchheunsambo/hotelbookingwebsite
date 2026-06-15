package com.example.hotelbookingwebsite.service;

import com.example.hotelbookingwebsite.dto.request.LoginRequest;
import com.example.hotelbookingwebsite.dto.request.RegisterRequest;
import com.example.hotelbookingwebsite.dto.response.AuthResponse;
import com.example.hotelbookingwebsite.entity.User;
import com.example.hotelbookingwebsite.exception.BadRequestException;
import com.example.hotelbookingwebsite.exception.ConflictException;
import com.example.hotelbookingwebsite.repository.UserRepository;
import com.example.hotelbookingwebsite.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(User.Role.ROLE_USER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        String token = jwtUtils.generateTokenFromUsername(saved.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            String token = jwtUtils.generateToken(auth);
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }
}