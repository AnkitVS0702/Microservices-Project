package com.ankitshiksharthi.microserviceproject.payment_service.dto;

import com.ankitshiksharthi.microserviceproject.payment_service.model.Payment;
import com.ankitshiksharthi.microserviceproject.payment_service.model.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String orderId,
        String orderNumber,
        BigDecimal amount,
        String paymentMethod,
        String status,
        String transactionId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getOrderNumber(),
                payment.getAmount(),
                payment.getPaymentMethod().name(),
                payment.getStatus().name(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}

