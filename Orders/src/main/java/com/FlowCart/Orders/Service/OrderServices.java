package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.FlowCart.Orders.Clients.ProductClient;
import com.FlowCart.Orders.DTO.ProductDTO;
import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.Events.OrderEvents;
import com.FlowCart.Orders.Events.StockResultEvent;
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

    @Autowired
    KafkaTemplate<String, OrderEvents> kafkaTemplate; // This is used to send messages to the Kafka topic

    
    // private RestTemplate restTemplate; // This is used to make REST calls to the product service

    public OrderServices(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
        // this.restTemplate = restTemplate;
    }

    // public ProductDTO getProduct(Integer productId) {
    // String url = productServiceUrl + "/" + productId;
    // return restTemplate.getForObject(url, ProductDTO.class); we can use this method also to call the product service but we are using feign client here beacause it is more convenient and easier to use

    public void sendOrderEvent(OrderEvents orderEvents) {
        System.out.println("Sending order event: " + orderEvents);
        kafkaTemplate.send("order-events", orderEvents); // This is used to send messages to the Kafka topic
    }
   

// @Transactional wokrs in main thread but when we are using CompletableFuture it runs in a separate thread and @Transactional does not work in a separate thread so we need to use @Transactional in the method that is calling the createOrder method and not in the createOrder method itself
// @Retry(name = "productService", fallbackMethod = "createOrderFallback")
// @CircuitBreaker(name = "productService", fallbackMethod = "createOrderFallback")
// @TimeLimiter(name = "productService", fallbackMethod = "createOrderFallback") now we are using kafka event, kafka provides its own retry mechanism and it is more reliable than the retry mechanism provided by resilience4j, so we don't need to use resilience4j's retry mechanism here, we can just rely on kafka's retry mechanism
public Orders createOrder(Orders order) {
   order.setOrderStatus("PENDING");
   Orders savedOrder = ordersRepository.save(order);
   
   OrderEvents orderEvents = new OrderEvents(
    savedOrder.getOrderId(),
    savedOrder.getProductId(),
    savedOrder.getQuantity()
   );
   sendOrderEvent(orderEvents);
   return savedOrder;
}// This method is used to create an order and send an event to the Kafka topic, the event will be consumed by the inventory service to update the stock of the product, if the product service is down or not responding then the fallback method will be called and it will return a failed order with the same order details but with the order status as "FAILED"

    // public CompletableFuture<Orders> createOrderFallback(Orders order, Throwable throwable) { // This is the fallback method that will be called when the product service is down or not responding
    //     Orders fallbackOrder = new Orders();
    //     fallbackOrder.setOrderId(order.getOrderId());
    //     fallbackOrder.setProductId(order.getProductId());
    //     fallbackOrder.setQuantity(order.getQuantity());
    //     fallbackOrder.setOrderStatus("FAILED");
    //     return CompletableFuture.completedFuture(fallbackOrder);
    // } 

    @KafkaListener(topics = "stock-result-events", groupId = "order-group")
    public void handleStockResult(StockResultEvent event) {
        System.out.println("Received stock result event: " + event);
        if (event.isSuccess()) {
            updateOrderStatus(event.getOrderId(), "CONFIRMED");
        } else {
            updateOrderStatus(event.getOrderId(), "FAILED");
        }
    } // This method listens to the Kafka topic "stock-result-events" and updates the order status based on the stock result received from the product service

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
