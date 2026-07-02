package com.ankitshiksharthi.microserviceproject.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute(){
        return route("product_service")
            .route(RequestPredicates.path("/api/product/**").or(RequestPredicates.path("/api/product")), HandlerFunctions.http())
                .filter(lb("product-service"))
                // .filter(circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(){
        return route("order_service")
            .route(RequestPredicates.path("/api/order/**").or(RequestPredicates.path("/api/order")), HandlerFunctions.http())
                .filter(lb("order-service"))
                // .filter(circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute(){
        return route("inventory_service")
            .route(RequestPredicates.path("/api/inventory/**").or(RequestPredicates.path("/api/inventory")), HandlerFunctions.http())
                .filter(lb("inventory-service"))
                // .filter(circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
            .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"), HandlerFunctions.http())
                .filter(lb("product-service"))
                // .filter(circuitBreaker("productServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
            .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"), HandlerFunctions.http())
                .filter(lb("order-service"))
                // .filter(circuitBreaker("orderServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
            .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"), HandlerFunctions.http())
                .filter(lb("inventory-service"))
                // .filter(circuitBreaker("inventoryServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceRoute() {
        return route("cart_service")
            .route(RequestPredicates.path("/api/cart/**"), HandlerFunctions.http())
            .filter(lb("cart-service"))
            // .filter(circuitBreaker("cartServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> cartServiceSwaggerRoute() {
        return route("cart_service_swagger")
            .route(RequestPredicates.path("/aggregate/cart-service/v3/api-docs"), HandlerFunctions.http())
            .filter(lb("cart-service"))
            // .filter(circuitBreaker("cartServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user_service")
            .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http())
            .filter(lb("user-service"))
            // .filter(circuitBreaker("userServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute() {
        return route("user_service_swagger")
            .route(RequestPredicates.path("/aggregate/user-service/v3/api-docs"), HandlerFunctions.http())
            .filter(lb("user-service"))
            // .filter(circuitBreaker("userServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> adminRouteRoute() {
        return route("admin_route")
            .route(RequestPredicates.path("/api/admin/**"), HandlerFunctions.http())
            .filter(lb("user-service"))
            // .filter(circuitBreaker("adminCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute() {
        return route("payment_service")
            .route(RequestPredicates.path("/api/v1/payments/**"), HandlerFunctions.http())
            .filter(lb("payment-service"))
            // .filter(circuitBreaker("paymentServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceSwaggerRoute() {
        return route("payment_service_swagger")
            .route(RequestPredicates.path("/aggregate/payment-service/v3/api-docs"), HandlerFunctions.http())
            .filter(lb("payment-service"))
            // .filter(circuitBreaker("paymentServiceSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    // Fallback route - kept for when circuit breakers are re-enabled
    // @Bean
    // public RouterFunction<ServerResponse> fallbackRoute() {
    //     return route("fallbackRoute")
    //             .GET("/fallbackRoute",
    //                     request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
    //                     .body("Service Unavailable, please try again later"))
    //             .build();
    // }

}
