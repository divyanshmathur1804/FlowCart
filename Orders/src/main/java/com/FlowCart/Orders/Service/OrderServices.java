package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FlowCart.Orders.Clients.ProductClient;
import com.FlowCart.Orders.DTO.ProductDTO;
import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.ExceptionHandling.OrderNotFoundException;
import com.FlowCart.Orders.Repository.OrdersRepository;

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

    

    @Transactional
    public Orders createOrder(Orders order) {
        ProductDTO product = productClient.getProduct(order.getProductId());

    if (product == null) {
        throw new OrderNotFoundException("Product Not found");
    }

    if (product.getStock() < order.getQuantity()) {
        throw new OrderNotFoundException("Out of stock");
    }

    return ordersRepository.save(order);
    }

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
