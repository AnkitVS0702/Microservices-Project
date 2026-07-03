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
    public CartResponse addItem(
            @PathVariable String userId,
            @Valid @RequestBody CartItemRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        validateOwnership(userId, authEmail, authKeycloakId);
        return cartService.addItem(userId, request);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse getCart(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        validateOwnership(userId, authEmail, authKeycloakId);
        return cartService.getCart(userId);
    }

    @PutMapping("/{userId}/items/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse updateItemQuantity(
            @PathVariable String userId,
            @PathVariable String skuCode,
            @Valid @RequestBody QuantityRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        validateOwnership(userId, authEmail, authKeycloakId);
        return cartService.updateItemQuantity(userId, skuCode, request);
    }

    @DeleteMapping("/{userId}/items/{skuCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @PathVariable String userId,
            @PathVariable String skuCode,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        validateOwnership(userId, authEmail, authKeycloakId);
        cartService.removeItem(userId, skuCode);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Email", required = false) String authEmail,
            @RequestHeader(value = "X-User-Keycloak-Id", required = false) String authKeycloakId) {
        validateOwnership(userId, authEmail, authKeycloakId);
        cartService.clearCart(userId);
    }

    private void validateOwnership(String userId, String authEmail, String authKeycloakId) {
        if (authEmail == null && authKeycloakId == null) {
            return; // bypass validation for local dev/testing
        }
        boolean isEmailOwner = authEmail != null && authEmail.equalsIgnoreCase(userId);
        boolean isKeycloakOwner = authKeycloakId != null && authKeycloakId.equals(userId);
        if (!isEmailOwner && !isKeycloakOwner) {
            throw new RuntimeException("Unauthorized: You cannot access or modify another user's cart");
        }
    }
}

