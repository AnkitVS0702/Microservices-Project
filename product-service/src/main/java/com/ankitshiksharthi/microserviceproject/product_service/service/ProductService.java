package com.ankitshiksharthi.microserviceproject.product_service.service;

import com.ankitshiksharthi.microserviceproject.product_service.dto.ProductRequest;
import com.ankitshiksharthi.microserviceproject.product_service.dto.ProductResponse;
import com.ankitshiksharthi.microserviceproject.product_service.model.Product;
import com.ankitshiksharthi.microserviceproject.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .skuCode(productRequest.skuCode())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created successfully");
        return mapToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSkuCode(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
