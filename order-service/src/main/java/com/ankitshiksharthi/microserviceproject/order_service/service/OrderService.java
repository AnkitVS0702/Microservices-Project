package com.ankitshiksharthi.microserviceproject.order_service.service;

import com.ankitshiksharthi.microserviceproject.order_service.client.InventoryClient;
import com.ankitshiksharthi.microserviceproject.order_service.dto.OrderRequest;
import com.ankitshiksharthi.microserviceproject.order_service.event.OrderPlacedEvent;
import com.ankitshiksharthi.microserviceproject.order_service.model.Order;
import com.ankitshiksharthi.microserviceproject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import static com.ankitshiksharthi.microserviceproject.order_service.client.InventoryClient.log;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        // first check whether it exists it inventory or not
        var isProductAvailable= inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity());
        //then place order
        if(isProductAvailable) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
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
        }else{
            throw new RuntimeException("Product with skuCode "+orderRequest.skuCode() +" not in stock");
        }
    }
}
