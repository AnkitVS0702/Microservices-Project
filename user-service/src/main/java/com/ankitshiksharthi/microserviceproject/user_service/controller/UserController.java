package com.ankitshiksharthi.microserviceproject.user_service.controller;

import com.ankitshiksharthi.microserviceproject.user_service.dto.RegisterUserRequest;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UserResponse;
import com.ankitshiksharthi.microserviceproject.user_service.dto.UpdateUserProfileRequest;
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

