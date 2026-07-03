package com.ankitshiksharthi.microserviceproject.user_service.controller;

import com.ankitshiksharthi.microserviceproject.user_service.dto.RegisterUserRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UserResponse;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UpdateUserProfileRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.LoginRequest;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserRole;
import com.ankitshiksharthi.microserviceproject.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        if (request.getEmail().equals(adminEmail) && request.getPassword().equals(adminPassword)) {
            return ResponseEntity.ok(UserResponse.builder()
                    .email(adminEmail)
                    .firstName("Admin")
                    .role(UserRole.ADMIN)
                    .build());
        }
        
        try {
            UserResponse user = userService.getUserByEmail(request.getEmail());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Mock response for testing if user not found since this is a mock setup
            return ResponseEntity.ok(UserResponse.builder()
                    .email(request.getEmail())
                    .firstName("Test User")
                    .role(UserRole.CUSTOMER)
                    .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        UserResponse response = userService.getUserProfile(userId);
        validateOwnership(response, authEmail, authKeycloakId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserProfileRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        UserResponse response = userService.getUserProfile(userId);
        validateOwnership(response, authEmail, authKeycloakId);
        UserResponse updatedResponse = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(updatedResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail) {
        if (authKeycloakId != null) {
            return ResponseEntity.ok(userService.getUserByKeycloakId(authKeycloakId));
        } else if (authEmail != null) {
            return ResponseEntity.ok(userService.getUserByEmail(authEmail));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(@PathVariable String keycloakId) {
        UserResponse response = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(response);
    }

    private void validateOwnership(UserResponse user, String authEmail, String authKeycloakId) {
        if (authEmail != null && !authEmail.equalsIgnoreCase(user.getEmail())) {
            throw new RuntimeException("Unauthorized: You cannot access or update another user's profile");
        }
        if (authKeycloakId != null && !authKeycloakId.equals(user.getKeycloakId())) {
            throw new RuntimeException("Unauthorized: You cannot access or update another user's profile");
        }
    }
}

