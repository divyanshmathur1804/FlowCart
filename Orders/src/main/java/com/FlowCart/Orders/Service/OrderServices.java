package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.FlowCart.Orders.Clients.ProductClient;
import com.FlowCart.Orders.DTO.ProductDTO;
import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.ExceptionHandling.OrderNotFoundException;
import com.FlowCart.Orders.Repository.OrdersRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;

@Service
public class OrderServices {
    private OrdersRepository ordersRepository;
    
    @Autowired
    private ProductClient productClient;

    
    // private RestTemplate restTemplate; // This is used to make REST calls to the product service

    public OrderServices(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
        // this.restTemplate = restTemplate;
    }

    // public ProductDTO getProduct(Integer productId) {
    // String url = productServiceUrl + "/" + productId;
    // return restTemplate.getForObject(url, ProductDTO.class); we can use this method also to call the product service but we are using feign client here beacause it is more convenient and easier to use

    

// @Transactional wokrs in main thread but when we are using CompletableFuture it runs in a separate thread and @Transactional does not work in a separate thread so we need to use @Transactional in the method that is calling the createOrder method and not in the createOrder method itself
@Retry(name = "productService", fallbackMethod = "createOrderFallback")
@CircuitBreaker(name = "productService", fallbackMethod = "createOrderFallback")
@TimeLimiter(name = "productService", fallbackMethod = "createOrderFallback")
public CompletableFuture<Orders> createOrder(Orders order) {

    return CompletableFuture.supplyAsync(() -> {

        ProductDTO product = productClient.getProduct(order.getProductId());

        if (product == null) {
            throw new RuntimeException("Product not found"); // not using OrderNotFoundException here because it is not an order related exception, it is a product related exception
        }

        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Out of stock");
        }

        return ordersRepository.save(order);
    });
}

    public CompletableFuture<Orders> createOrderFallback(Orders order, Throwable throwable) { // This is the fallback method that will be called when the product service is down or not responding
        Orders fallbackOrder = new Orders();
        fallbackOrder.setOrderId(order.getOrderId());
        fallbackOrder.setProductId(order.getProductId());
        fallbackOrder.setQuantity(order.getQuantity());
        fallbackOrder.setOrderStatus("FAILED");
        return CompletableFuture.completedFuture(fallbackOrder);
    }

    @Cacheable(value = "orders", key = "#orderId")
    public Optional<Orders> getOrderById(Long orderId) {
    return ordersRepository.findById(orderId);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String orderStatus) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not found"));
        order.setOrderStatus(orderStatus);
        ordersRepository.save(order);
        
    }

    @Transactional
    public void updateOrderQuantity(Long orderId, Long quantity) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not found"));
        ProductDTO product = productClient.getProduct(order.getProductId());
        if (product == null) {
            throw new OrderNotFoundException("Product Not found");
        }
        if (product.getStock() < quantity) {
            throw new OrderNotFoundException("Out of stock");
        }
        order.setQuantity(quantity);
        ordersRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        ordersRepository.deleteById(orderId);
    }
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }
    public Optional<Orders> getOrdersByProductId(Long productId) {
        return ordersRepository.findById(productId);
    }
    public String getOrderStatus(Long orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not found"));
        return order.getOrderStatus();
    }
    public Long getOrderQuantity(Long orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not found"));
        return order.getQuantity();
    }

}
