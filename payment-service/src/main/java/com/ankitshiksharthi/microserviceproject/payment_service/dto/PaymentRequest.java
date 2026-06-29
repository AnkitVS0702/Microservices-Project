package com.ankitshiksharthi.microserviceproject.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.ankitshiksharthi.microserviceproject.payment_service.model.Payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Order number is required")
        String orderNumber,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}

