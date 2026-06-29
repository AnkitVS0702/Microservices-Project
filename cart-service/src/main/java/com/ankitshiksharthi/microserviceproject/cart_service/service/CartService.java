package com.ankitshiksharthi.microserviceproject.cart_service.service;

import com.ankitshiksharthi.microserviceproject.cart_service.dto.CartItemRequest;
import com.ankitshiksharthi.microserviceproject.cart_service.dto.CartResponse;
import com.ankitshiksharthi.microserviceproject.cart_service.dto.QuantityRequest;
import com.ankitshiksharthi.microserviceproject.cart_service.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7);

    private final RedisTemplate<String, Object> redisTemplate;

    public CartResponse addItem(String userId, CartItemRequest request) {
        Map<String, CartItem> cart = getCartItems(userId);
        cart.put(request.skuCode(), new CartItem(request.skuCode(), request.productName(), request.price(), request.quantity()));
        saveCart(userId, cart);
        return mapToResponse(userId, cart);
    }

    public CartResponse getCart(String userId) {
        Map<String, CartItem> cart = getCartItems(userId);
        return mapToResponse(userId, cart);
    }

    public CartResponse updateItemQuantity(String userId, String skuCode, QuantityRequest request) {
        Map<String, CartItem> cart = getCartItems(userId);
        CartItem item = cart.get(skuCode);
        if (item == null) {
            throw new IllegalArgumentException("Cart item not found for skuCode: " + skuCode);
        }
        item.setQuantity(request.quantity());
        cart.put(skuCode, item);
        saveCart(userId, cart);
        return mapToResponse(userId, cart);
    }

    public void removeItem(String userId, String skuCode) {
        Map<String, CartItem> cart = getCartItems(userId);
        cart.remove(skuCode);
        saveCart(userId, cart);
    }

    public void clearCart(String userId) {
        redisTemplate.delete(cartKey(userId));
    }

    @SuppressWarnings("unchecked")
    private Map<String, CartItem> getCartItems(String userId) {
        Object value = redisTemplate.opsForValue().get(cartKey(userId));
        if (value == null) {
            return new HashMap<>();
        }
        if (value instanceof Map<?, ?> existing) {
            Map<String, CartItem> result = new HashMap<>();
            existing.forEach((key, val) -> result.put(String.valueOf(key), convertToCartItem(val)));
            return result;
        }
        return new HashMap<>();
    }

    private CartItem convertToCartItem(Object value) {
        if (value instanceof CartItem cartItem) {
            return cartItem;
        }
        if (value instanceof Map<?, ?> map) {
            return CartItem.builder()
                    .skuCode(String.valueOf(map.get("skuCode")))
                    .productName(String.valueOf(map.get("productName")))
                    .price(new BigDecimal(String.valueOf(map.get("price"))))
                    .quantity(Integer.valueOf(String.valueOf(map.get("quantity"))))
                    .build();
        }
        throw new IllegalStateException("Unsupported cart item format: " + value.getClass());
    }

    private void saveCart(String userId, Map<String, CartItem> cart) {
        redisTemplate.opsForValue().set(cartKey(userId), cart, CART_TTL);
    }

    private String cartKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }

    private CartResponse mapToResponse(String userId, Map<String, CartItem> cart) {
        List<CartResponse.CartItem> items = cart.values().stream()
                .map(item -> new CartResponse.CartItem(
                        item.getSkuCode(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartResponse.CartItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(userId, items, total);
    }
}

