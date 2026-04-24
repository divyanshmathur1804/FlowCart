package com.FlowCart.Orders.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.FlowCart.Orders.DTO.ProductDTO;

@FeignClient(name = "product-service")
public interface ProductClient { // This is a Feign client interface to communicate with the product service

    @GetMapping("/products/{id}")
    ProductDTO getProduct(@PathVariable Long id); // This method will call the product service to get the product details by ID
    
}
