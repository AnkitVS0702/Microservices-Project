package com.ankitshiksharthi.microserviceproject.order_service.controller;

import com.ankitshiksharthi.microserviceproject.order_service.dto.OrderRequest;
import com.ankitshiksharthi.microserviceproject.order_service.dto.OrderResponse;
import com.ankitshiksharthi.microserviceproject.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }

    @GetMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrderHistory(
            @RequestParam String email,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail) {
        if (authEmail != null && !authEmail.equalsIgnoreCase(email)) {
            throw new RuntimeException("Unauthorized: You cannot view order history for other users");
        }
        return orderService.getOrdersByEmail(email);
    }
}
