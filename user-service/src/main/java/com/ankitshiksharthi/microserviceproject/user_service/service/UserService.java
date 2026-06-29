package com.ankitshiksharthi.microserviceproject.user_service.service;

import com.ankitshiksharthi.microserviceproject.user_service.dto.RegisterUserRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UserResponse;
import com.ankitshiksharthi.microserviceproject.user_service.model.User;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserRole;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserStatus;
import com.ankitshiksharthi.microserviceproject.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .keycloakId(request.getKeycloakId())
                .role(UserRole.CUSTOMER)  // Default role
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        log.info("Fetching user profile for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByKeycloakId(String keycloakId) {
        log.info("Fetching user by keycloakId: {}", keycloakId);
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found with keycloakId: " + keycloakId));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

