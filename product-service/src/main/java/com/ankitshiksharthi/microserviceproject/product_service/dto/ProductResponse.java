package com.ankitshiksharthi.microserviceproject.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(String id, String name, String description,
                              String skuCode, BigDecimal price,
                              String status, String category, String imageUrl,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
}
