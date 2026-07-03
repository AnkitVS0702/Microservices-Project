package com.ankitshiksharthi.microserviceproject.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderNumber,
        java.util.List<OrderLineItemResponse> orderItems,
        String address,
        BigDecimal totalAmount,
        LocalDateTime orderDate
) {}
