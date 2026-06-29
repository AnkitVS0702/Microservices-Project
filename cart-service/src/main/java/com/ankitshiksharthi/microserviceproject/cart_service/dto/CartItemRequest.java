package com.ankitshiksharthi.microserviceproject.cart_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CartItemRequest(
        @NotBlank(message = "SKU code is required") String skuCode,
        @NotBlank(message = "Product name is required") String productName,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero") BigDecimal price,
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1") Integer quantity
) {
}

