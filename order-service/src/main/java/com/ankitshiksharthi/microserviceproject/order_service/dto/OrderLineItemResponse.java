package com.ankitshiksharthi.microserviceproject.order_service.dto;

import java.math.BigDecimal;

public record OrderLineItemResponse(
        Long id,
        String skuCode,
        String productName,
        BigDecimal price,
        Integer quantity
) {}
