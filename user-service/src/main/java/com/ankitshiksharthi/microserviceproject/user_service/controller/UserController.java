package com.ankitshiksharthi.microserviceproject.user_service.controller;

import com.ankitshiksharthi.microserviceproject.user_service.dto.RegisterUserRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UserResponse;
import com.ankitshiksharthi.microserviceproject.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        // In a real scenario, extract the user ID from JWT token
        // For now, this is a placeholder
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
}

