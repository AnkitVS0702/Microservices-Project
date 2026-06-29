package com.ankitshiksharthi.microserviceproject.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRequestUpdateRequest {
    @NotBlank(message = "Action is required (approve/reject)")
    private String action; // "approve" or "reject"

    private String adminNotes;
}

