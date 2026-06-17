package com.ankitshiksharthi.microserviceproject.order_service.repository;

import com.ankitshiksharthi.microserviceproject.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
