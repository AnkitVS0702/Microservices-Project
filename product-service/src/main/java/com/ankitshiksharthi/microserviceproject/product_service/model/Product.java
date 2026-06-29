package com.ankitshiksharthi.microserviceproject.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(value = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String id;
    private String name;
    private String description;
    private String skuCode;
    private BigDecimal price;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
