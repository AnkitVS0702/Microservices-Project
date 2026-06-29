package com.ankitshiksharthi.microserviceproject.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank(message = "SKU code is required")
        String skuCode,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "User details are required")
        @Valid
        UserDetails userDetails
) {
    public record UserDetails(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "First name is required")
            String firstName,

            @NotBlank(message = "Last name is required")
            String lastName
    ) {}
}
