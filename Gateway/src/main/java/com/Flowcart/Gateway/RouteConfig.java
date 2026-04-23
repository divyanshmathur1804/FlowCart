package com.Flowcart.Gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/products", "/products/**")
                        .uri("lb://product-service"))
                .route("order-service", r -> r
                        .path("/orders", "/orders/**")
                        .uri("lb://order-service"))
                .build();
    }
}
