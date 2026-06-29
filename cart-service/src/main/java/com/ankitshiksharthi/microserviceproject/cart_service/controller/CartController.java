package com.ankitshiksharthi.microserviceproject.cart_service.controller;

import com.ankitshiksharthi.microserviceproject.cart_service.dto.CartItemRequest;
import com.ankitshiksharthi.microserviceproject.cart_service.dto.CartResponse;
import com.ankitshiksharthi.microserviceproject.cart_service.dto.QuantityRequest;
import com.ankitshiksharthi.microserviceproject.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse addItem(@PathVariable String userId, @Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart(@PathVariable String userId) {
        return cartService.getCart(userId);
    }

    @PutMapping("/{userId}/items/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse updateItemQuantity(@PathVariable String userId,
                                           @PathVariable String skuCode,
                                           @Valid @RequestBody QuantityRequest request) {
        return cartService.updateItemQuantity(userId, skuCode, request);
    }

    @DeleteMapping("/{userId}/items/{skuCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable String userId, @PathVariable String skuCode) {
        cartService.removeItem(userId, skuCode);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
    }
}

