package com.ankitshiksharthi.microserviceproject.product_service.repository;

import com.ankitshiksharthi.microserviceproject.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySkuCode(String skuCode);
}
