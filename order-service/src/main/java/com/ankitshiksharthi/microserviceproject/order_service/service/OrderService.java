package com.ankitshiksharthi.microserviceproject.order_service.service;

import com.ankitshiksharthi.microserviceproject.order_service.client.InventoryClient;
import com.ankitshiksharthi.microserviceproject.order_service.client.ProductClient;
import com.ankitshiksharthi.microserviceproject.order_service.dto.OrderRequest;
import com.ankitshiksharthi.microserviceproject.order_service.dto.OrderResponse;
import com.ankitshiksharthi.microserviceproject.order_service.event.OrderPlacedEvent;
import com.ankitshiksharthi.microserviceproject.order_service.model.Order;
import com.ankitshiksharthi.microserviceproject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        // Check inventory for all items
        for (var item : orderRequest.orderItems()) {
            boolean isProductAvailable = inventoryClient.isInStock(item.skuCode(), item.quantity());
            if (!isProductAvailable) {
                throw new RuntimeException("Product with skuCode " + item.skuCode() + " not in stock");
            }
        }

        // Calculate total amount and prepare line items
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        List<com.ankitshiksharthi.microserviceproject.order_service.model.OrderLineItem> lineItems = new java.util.ArrayList<>();
        
        for (var itemRequest : orderRequest.orderItems()) {
            var lineItem = new com.ankitshiksharthi.microserviceproject.order_service.model.OrderLineItem();
            lineItem.setSkuCode(itemRequest.skuCode());
            lineItem.setPrice(itemRequest.price());
            lineItem.setQuantity(itemRequest.quantity());
            lineItems.add(lineItem);
            
            totalAmount = totalAmount.add(itemRequest.price().multiply(java.math.BigDecimal.valueOf(itemRequest.quantity())));
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setAddress(orderRequest.address());
        order.setTotalAmount(totalAmount);
        order.setEmail(orderRequest.userDetails().email());
        order.setOrderLineItems(lineItems);
        
        orderRepository.save(order);

        // Send the message to Kafka Topic
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
        orderPlacedEvent.setOrderNumber(order.getOrderNumber());
        orderPlacedEvent.setEmail(orderRequest.userDetails().email());
        orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
        orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
        log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
        kafkaTemplate.send("order-placed", orderPlacedEvent);
        log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
    }

    public List<OrderResponse> getOrdersByEmail(String email) {
        log.info("Retrieving order history for email: {}", email);
        return orderRepository.findByEmail(email).stream()
                .map(order -> {
                    List<com.ankitshiksharthi.microserviceproject.order_service.dto.OrderLineItemResponse> itemResponses = new java.util.ArrayList<>();
                    
                    if (order.getOrderLineItems() != null) {
                        for (var item : order.getOrderLineItems()) {
                            String productName = "Unknown Product";
                            try {
                                var product = productClient.getProductBySkuCode(item.getSkuCode());
                                if (product != null && product.name() != null) {
                                    productName = product.name();
                                }
                            } catch (Exception e) {
                                log.warn("Failed to fetch product name for SKU: {} - {}", item.getSkuCode(), e.getMessage());
                            }
                            itemResponses.add(new com.ankitshiksharthi.microserviceproject.order_service.dto.OrderLineItemResponse(
                                    item.getId(),
                                    item.getSkuCode(),
                                    productName,
                                    item.getPrice(),
                                    item.getQuantity()
                            ));
                        }
                    }

                    return new OrderResponse(
                            order.getId(),
                            order.getOrderNumber(),
                            itemResponses,
                            order.getAddress(),
                            order.getTotalAmount(),
                            order.getCreatedAt()
                    );
                })
                .toList();
    }
}
