package com.ankitshiksharthi.microserviceproject.payment_service.repository;

import com.ankitshiksharthi.microserviceproject.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByTransactionId(String transactionId);
}

