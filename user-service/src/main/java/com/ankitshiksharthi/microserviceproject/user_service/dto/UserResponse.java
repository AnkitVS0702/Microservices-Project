package com.ankitshiksharthi.microserviceproject.user_service.dto;

import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserRole;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String keycloakId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
}

