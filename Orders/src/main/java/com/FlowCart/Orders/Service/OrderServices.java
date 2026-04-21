package com.FlowCart.Orders.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.Repository.OrdersRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServices {
    private OrdersRepository ordersRepository;

    public OrderServices(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Transactional
    public Orders createOrder(Orders order) {
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
