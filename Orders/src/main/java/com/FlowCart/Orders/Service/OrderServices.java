package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.FlowCart.Orders.DTO.ProductDTO;
import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.Repository.OrdersRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServices {
    private OrdersRepository ordersRepository;
    
    @Value("${product.service.url}")
    private String productServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public OrderServices(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public ProductDTO getProduct(Integer productId) {
    String url = productServiceUrl + "/" + productId;
    return restTemplate.getForObject(url, ProductDTO.class);
}

    @Transactional
    public Orders createOrder(Orders order) {
        ProductDTO product = getProduct(order.getProductId());

    if (product == null) {
        throw new RuntimeException("Product not found");
    }

    if (product.getStock() < order.getQuantity()) {
        throw new RuntimeException("Out of stock");
    }

    return ordersRepository.save(order);
    }

    public Optional<Orders> getOrderById(Integer orderId) {


    
        return ordersRepository.findById(orderId);
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, String orderStatus) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(orderStatus);
        ordersRepository.save(order);
        
    }

    @Transactional
    public void updateOrderQuantity(Integer orderId, Integer quantity) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        ProductDTO product = getProduct(order.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("Out of stock");
        }
        order.setQuantity(quantity);
        ordersRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        ordersRepository.deleteById(orderId);
    }
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }
    public Optional<Orders> getOrdersByProductId(Integer productId) {
        return ordersRepository.findById(productId);
    }
    public String getOrderStatus(Integer orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getOrderStatus();
    }
    public int getOrderQuantity(Integer orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getQuantity();
    }

}
