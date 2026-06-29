package com.ankitshiksharthi.microserviceproject.cart_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        String userId,
        List<CartItem> items,
        BigDecimal totalAmount
) {
    public record CartItem(
            String skuCode,
            String productName,
            BigDecimal price,
            Integer quantity,
            BigDecimal lineTotal
    ) {
    }
}

