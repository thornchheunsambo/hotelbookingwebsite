package com.example.hotelbookingwebsite.config;

import com.example.hotelbookingwebsite.entity.Room;
import com.example.hotelbookingwebsite.entity.User;
import com.example.hotelbookingwebsite.repository.RoomRepository;
import com.example.hotelbookingwebsite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedRooms();
    }

    private void seedAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@hotel.com")
                    .fullName("Hotel Administrator")
                    .phone("+855 23 000 000")
                    .role(User.Role.ROLE_ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin user created -> username: admin | password: admin123");
        }
    }

    private void seedRooms() {
        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder()
                    .roomNumber("101").name("Standard Room A")
                    .roomType(Room.RoomType.STANDARD).price(new BigDecimal("45.00"))
                    .description("Comfortable standard room with city view, 1 queen bed")
                    .capacity(2).status(Room.RoomStatus.AVAILABLE).build());

            roomRepository.save(Room.builder()
                    .roomNumber("102").name("Standard Room B")
                    .roomType(Room.RoomType.STANDARD).price(new BigDecimal("45.00"))
                    .description("Comfortable standard room with garden view, 2 single beds")
                    .capacity(2).status(Room.RoomStatus.AVAILABLE).build());

            roomRepository.save(Room.builder()
                    .roomNumber("201").name("Deluxe Room")
                    .roomType(Room.RoomType.DELUXE).price(new BigDecimal("85.00"))
                    .description("Spacious deluxe room with king bed and river view")
                    .capacity(2).status(Room.RoomStatus.AVAILABLE).build());

            roomRepository.save(Room.builder()
                    .roomNumber("202").name("Deluxe Family Room")
                    .roomType(Room.RoomType.DELUXE).price(new BigDecimal("110.00"))
                    .description("Large deluxe room perfect for families with 2 queen beds")
                    .capacity(4).status(Room.RoomStatus.AVAILABLE).build());

            roomRepository.save(Room.builder()
                    .roomNumber("301").name("Junior Suite")
                    .roomType(Room.RoomType.SUITE).price(new BigDecimal("150.00"))
                    .description("Elegant junior suite with separate living area and balcony")
                    .capacity(2).status(Room.RoomStatus.AVAILABLE).build());

            roomRepository.save(Room.builder()
                    .roomNumber("401").name("Penthouse Suite")
                    .roomType(Room.RoomType.PENTHOUSE).price(new BigDecimal("350.00"))
                    .description("Luxurious penthouse with panoramic views and private jacuzzi")
                    .capacity(4).status(Room.RoomStatus.AVAILABLE).build());

            log.info("✅ 6 sample rooms seeded successfully");
        }
    }
}