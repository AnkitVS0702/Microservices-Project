package com.ankitshiksharthi.microserviceproject.inventory_service.service;

import com.ankitshiksharthi.microserviceproject.inventory_service.controller.InventoryController;
import com.ankitshiksharthi.microserviceproject.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String skuCode, Integer quantity) {

        boolean isInStock = inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
        return isInStock;
    }
}
