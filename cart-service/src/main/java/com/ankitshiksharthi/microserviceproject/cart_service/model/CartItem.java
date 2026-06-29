package com.ankitshiksharthi.microserviceproject.cart_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String skuCode;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}

