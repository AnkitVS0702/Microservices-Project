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
                .vendorId(productRequest.vendorId())
                .category(productRequest.category())
                .imageUrl(productRequest.imageUrl())
                .status(productRequest.vendorId() != null ? "PENDING_APPROVAL" : "ACTIVE")
                .build();
        productRepository.save(product);
        log.info("Product created successfully with status: {}", product.getStatus());
        return mapToProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(this::mapToProductResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByStatus(String status) {
        return productRepository.findAll()
                .stream()
                .filter(p -> status.equals(p.getStatus()))
                .map(this::mapToProductResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByVendor(Long vendorId) {
        return productRepository.findAll()
                .stream()
                .filter(p -> vendorId.equals(p.getVendorId()))
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
                product.getVendorId(),
                product.getStatus(),
                product.getCategory(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
