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
                        .uri("http://product-service:8081"))
                .route("order-service", r -> r
                        .path("/orders", "/orders/**")
                        .uri("http://order-service:8080"))
                .build();
    }
}
