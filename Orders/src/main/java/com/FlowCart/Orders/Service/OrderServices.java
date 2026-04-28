package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.FlowCart.Orders.Clients.ProductClient;
import com.FlowCart.Orders.DTO.ProductDTO;
import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.Events.OrderEvents;
import com.FlowCart.Orders.Events.OutBoxEvents;
import com.FlowCart.Orders.Events.StockResultEvent;
import com.FlowCart.Orders.ExceptionHandling.OrderNotFoundException;
import com.FlowCart.Orders.Repository.OrdersRepository;
import com.FlowCart.Orders.Repository.OutBoxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.transaction.Transactional;

@Service
public class OrderServices {
    private OrdersRepository ordersRepository;
    
    @Autowired
    private ProductClient productClient;

    private OutBoxRepository outBoxRepository; // This is used to save the events in the outbox table, the outbox table will be used to store the events that are not sent to the Kafka topic due to some reason and we can use a separate thread to send those events to the Kafka topic

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate; // This is used to send messages to the Kafka topic
    

    
    // private RestTemplate restTemplate; // This is used to make REST calls to the product service

    public OrderServices(OrdersRepository ordersRepository, OutBoxRepository outBoxRepository) {
        this.ordersRepository = ordersRepository;
        this.outBoxRepository = outBoxRepository;
        // this.restTemplate = restTemplate;
    }

    // public ProductDTO getProduct(Integer productId) {
    // String url = productServiceUrl + "/" + productId;
    // return restTemplate.getForObject(url, ProductDTO.class); we can use this method also to call the product service but we are using feign client here beacause it is more convenient and easier to use

    // public void sendOrderEvent(OrderEvents orderEvents) {
    //     System.out.println("Sending order event: " + orderEvents);
    //     kafkaTemplate.send("order-events", orderEvents); // This is used to send messages to the Kafka topic
    // } // not using this method to send the events to the Kafka topic directly, instead we are saving the events in the outbox table and using a separate thread to send those events to the Kafka topic, this is done to ensure that the events are not lost in case of any failure while sending the events to the Kafka topic, if we send the events to the Kafka topic directly and there is any failure while sending the events to the Kafka topic then the events will be lost and we will not be able to recover those events, but if we save the events in the outbox table then we can use a separate thread to send those events to the Kafka topic and if there is any failure while sending the events to the Kafka topic then we can retry sending those events in the next scheduled run
   

// @Transactional wokrs in main thread but when we are using CompletableFuture it runs in a separate thread and @Transactional does not work in a separate thread so we need to use @Transactional in the method that is calling the createOrder method and not in the createOrder method itself
// @Retry(name = "productService", fallbackMethod = "createOrderFallback")
// @CircuitBreaker(name = "productService", fallbackMethod = "createOrderFallback")
// @TimeLimiter(name = "productService", fallbackMethod = "createOrderFallback") now we are using kafka event, kafka provides its own retry mechanism and it is more reliable than the retry mechanism provided by resilience4j, so we don't need to use resilience4j's retry mechanism here, we can just rely on kafka's retry mechanism
@Transactional
public Orders createOrder(Orders order) {
   order.setOrderStatus("PENDING");
   Orders savedOrder = ordersRepository.save(order);
   
   OrderEvents orderEvents = new OrderEvents(
    savedOrder.getOrderId(),
    savedOrder.getProductId(),
    savedOrder.getQuantity()
   );

   String payload = convertToJson(orderEvents); // Convert the order event to a string or JSON format
   OutBoxEvents outBoxEvent = new OutBoxEvents();
   outBoxEvent.setEventType("ORDER_CREATED");
    outBoxEvent.setPayload(payload);
    outBoxEvent.setStatus("PENDING");

    outBoxRepository.save(outBoxEvent); // Save the event in the outbox table with status PENDING

//    sendOrderEvent(orderEvents); // Send the event to the Kafka topic, we can use a separate thread to send the events from the outbox table to the Kafka topic and update the status of the events in the outbox table to SENT after sending them to the Kafka topic
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
    public void handleStockResult(String payload) {
        try {
            StockResultEvent event = new ObjectMapper().readValue(payload, StockResultEvent.class);
            System.out.println("Received stock result event: " + event);
            if (event.isSuccess()) {
                updateOrderStatus(event.getOrderId(), "CONFIRMED");
            } else {
                updateOrderStatus(event.getOrderId(), "FAILED");
            }
        } catch (Exception e) {
            // log properly
            throw new RuntimeException(e);
        }
        } // This method listens to the Kafka topic "stock-result-events" and updates the order status based on the stock result received from the product service

    @Cacheable(value = "orders", key = "#orderId")
    public Optional<Orders> getOrderById(Long orderId) {
    return ordersRepository.findById(orderId);
    }

@Scheduled(fixedRate = 5000)
public void publishEvents() {

    List<OutBoxEvents> events = outBoxRepository.findByStatus("PENDING");

    for (OutBoxEvents event : events) {
        try {
            kafkaTemplate.send("order-events", event.getPayload()); // Send the event to the Kafka topic

            event.setStatus("SENT");
            outBoxRepository.save(event);

        } catch (Exception e) {
            // leave as PENDING → retry later
        }
    }
}// This method is used to publish the events from the outbox table to the Kafka topic, it runs every 5 seconds and checks for the events with status PENDING and sends them to the Kafka topic, if the event is sent successfully then it updates the status of the event to SENT in the outbox table, if there is any exception while sending the event to the Kafka topic then it leaves the status as PENDING and it will retry again in the next scheduled run

private String convertToJson(Object obj) {
    try {
        return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
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
