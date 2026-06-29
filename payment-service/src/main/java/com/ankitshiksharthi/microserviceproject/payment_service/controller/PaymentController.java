package com.ankitshiksharthi.microserviceproject.payment_service.controller;

import com.ankitshiksharthi.microserviceproject.payment_service.dto.PaymentRequest;
import com.ankitshiksharthi.microserviceproject.payment_service.dto.PaymentResponse;
import com.ankitshiksharthi.microserviceproject.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Process a payment", description = "Process a mock payment with configured success rate")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get payment by order ID", description = "Retrieve payment status by order ID")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get payment history", description = "Retrieve payment history for a user")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(
            @PathVariable String userId) {
        List<PaymentResponse> history = paymentService.getPaymentHistory(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{paymentId}/refund")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refund a payment", description = "Initiate a refund for a completed payment")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}

