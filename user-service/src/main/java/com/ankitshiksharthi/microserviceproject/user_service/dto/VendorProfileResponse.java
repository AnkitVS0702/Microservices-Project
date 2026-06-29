package com.ankitshiksharthi.microserviceproject.user_service.dto;

import com.ankitshiksharthi.microserviceproject.user_service.model.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorProfileResponse {
    private Long id;
    private Long userId;
    private String businessName;
    private String businessDescription;
    private String gstNumber;
    private VendorStatus status;
    private LocalDateTime createdAt;
}

