package com.ankitshiksharthi.microserviceproject.product_service.controller;

import com.ankitshiksharthi.microserviceproject.product_service.dto.ProductRequest;
import com.ankitshiksharthi.microserviceproject.product_service.dto.ProductResponse;
import com.ankitshiksharthi.microserviceproject.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
}
