package com.ankitshiksharthi.microserviceproject.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderNumber,
        String skuCode,
        String productName,
        BigDecimal price,
        Integer quantity,
        LocalDateTime orderDate
) {}
