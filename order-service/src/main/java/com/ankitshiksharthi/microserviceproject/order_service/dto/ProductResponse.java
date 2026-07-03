package com.ankitshiksharthi.microserviceproject.order_service.dto;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        String skuCode,
        BigDecimal price,
        String status,
        String category,
        String imageUrl
) {}
