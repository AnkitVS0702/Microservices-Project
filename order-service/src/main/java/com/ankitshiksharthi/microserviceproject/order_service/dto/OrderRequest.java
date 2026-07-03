package com.ankitshiksharthi.microserviceproject.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull(message = "Order items cannot be null")
        @NotEmpty(message = "Order must have at least one item")
        java.util.List<@Valid OrderLineItemRequest> orderItems,

        @NotBlank(message = "Address is required")
        String address,

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
