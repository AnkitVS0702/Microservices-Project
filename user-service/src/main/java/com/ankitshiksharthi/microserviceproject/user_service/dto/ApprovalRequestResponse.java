package com.ankitshiksharthi.microserviceproject.user_service.dto;

import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalStatus;
import com.ankitshiksharthi.microserviceproject.user_service.model.enums.ApprovalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRequestResponse {
    private Long id;
    private ApprovalType type;
    private Long referenceId;
    private Long vendorId;
    private ApprovalStatus status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}

