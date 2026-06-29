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
public class VendorApplyRequest {
    @NotBlank(message = "Business name is required")
    private String businessName;

    private String businessDescription;

    @NotBlank(message = "GST number is required")
    private String gstNumber;
}

