package com.ankitshiksharthi.microserviceproject.payment_service.service;

import com.ankitshiksharthi.microserviceproject.payment_service.dto.PaymentRequest;
import com.ankitshiksharthi.microserviceproject.payment_service.dto.PaymentResponse;
import com.ankitshiksharthi.microserviceproject.payment_service.model.Payment;
import com.ankitshiksharthi.microserviceproject.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payment.mock.success-rate:80}")
    private int successRate;

    @Value("${payment.mock.processing-delay-ms:1000}")
    private long processingDelayMs;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.orderNumber());

        // Simulate processing delay
        try {
            long delay = (long) (Math.random() * processingDelayMs) + 500;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate unique transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Mock payment processing with configurable success rate
        boolean isSuccessful = (Math.random() * 100) < successRate;

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .orderNumber(request.orderNumber())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .transactionId(transactionId)
                .status(isSuccessful ? Payment.PaymentStatus.COMPLETED : Payment.PaymentStatus.FAILED)
                .failureReason(isSuccessful ? null : generateFailureReason())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} for order {}: {}", transactionId, request.orderNumber(),
                isSuccessful ? "COMPLETED" : "FAILED");

        // Publish Kafka event
        publishPaymentEvent(savedPayment, isSuccessful);

        return PaymentResponse.fromEntity(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        log.info("Fetching payment for order: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        log.info("Processing refund for payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Payment {} refunded successfully", paymentId);

        return PaymentResponse.fromEntity(refundedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentHistory(String userId) {
        log.info("Fetching payment history for user: {}", userId);
        // In a real application, payments would be linked to users
        // For now, return all payments
        return paymentRepository.findAll().stream()
                .map(PaymentResponse::fromEntity)
                .toList();
    }

    private void publishPaymentEvent(Payment payment, boolean isSuccessful) {
        try {
            String topic = isSuccessful ? "payment-completed" : "payment-failed";
            kafkaTemplate.send(topic, payment.getOrderNumber(), payment);
            log.info("Published {} event to topic: {}", isSuccessful ? "PaymentCompletedEvent" : "PaymentFailedEvent", topic);
        } catch (Exception e) {
            log.error("Failed to publish payment event for order: {}", payment.getOrderNumber(), e);
        }
    }

    private String generateFailureReason() {
        String[] reasons = {
                "Insufficient funds",
                "Card declined",
                "Invalid card details",
                "Network error",
                "Service temporarily unavailable",
                "Transaction timeout"
        };
        return reasons[(int) (Math.random() * reasons.length)];
    }
}

