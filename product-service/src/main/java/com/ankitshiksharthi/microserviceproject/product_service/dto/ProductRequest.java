package com.ankitshiksharthi.microserviceproject.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        String id,
        @NotBlank(message = "Product name is required")
        String name,
        String description,
        @NotBlank(message = "SKU code is required")
        String skuCode,
        @NotNull(message = "Product price is required")
        @Positive(message = "Product price must be positive")
        BigDecimal price
) {
}
