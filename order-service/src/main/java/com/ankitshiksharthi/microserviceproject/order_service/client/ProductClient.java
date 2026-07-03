package com.ankitshiksharthi.microserviceproject.order_service.client;

import com.ankitshiksharthi.microserviceproject.order_service.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductClient {
    Logger log = LoggerFactory.getLogger(ProductClient.class);

    @GetExchange("/api/product/sku/{skuCode}")
    @CircuitBreaker(name = "product", fallbackMethod = "fallbackMethod")
    @Retry(name = "product")
    ProductResponse getProductBySkuCode(@PathVariable String skuCode);

    default ProductResponse fallbackMethod(String skuCode, Throwable throwable) {
        log.info("Cannot get product for skuCode {}, failure reason: {}", skuCode, throwable.getMessage());
        return new ProductResponse(null, "Unknown Product (" + skuCode + ")", null, skuCode, null, null, null, null);
    }
}
