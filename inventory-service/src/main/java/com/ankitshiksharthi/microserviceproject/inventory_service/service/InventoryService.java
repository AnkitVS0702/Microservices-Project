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

    public void addInventory(com.ankitshiksharthi.microserviceproject.inventory_service.dto.InventoryRequest request) {
        inventoryRepository.findBySkuCode(request.getSkuCode()).ifPresentOrElse(
            inventory -> {
                inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
                inventoryRepository.save(inventory);
            },
            () -> {
                com.ankitshiksharthi.microserviceproject.inventory_service.model.Inventory inventory = new com.ankitshiksharthi.microserviceproject.inventory_service.model.Inventory();
                inventory.setSkuCode(request.getSkuCode());
                inventory.setQuantity(request.getQuantity());
                inventory.setReservedQuantity(0);
                inventoryRepository.save(inventory);
            }
        );
    }
}
