package com.FlowCart.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.FlowCart.Entity.Orders;
import com.FlowCart.Repository.OrdersRepository;

@Service
public class OrderServices {
    private OrdersRepository ordersRepository;

    public OrderServices(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Orders createOrder(Orders order) {
        return ordersRepository.save(order);
    }
    public Orders getOrderById(int orderId) {
        return ordersRepository.findByOrderId(orderId);
    }
    public void updateOrderStatus(int orderId, String orderStatus) {
        ordersRepository.updateOrderStatusByOrderId(orderId, orderStatus);
    }
    public void updateOrderQuantity(int orderId, int quantity) {
        ordersRepository.updateOrderQuantityByOrderId(orderId, quantity);
    }
    public void deleteOrder(int orderId) {
        ordersRepository.deleteByOrderId(orderId);
    }
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }
    public List<Orders> getOrdersByProductId(int productId) {
        return ordersRepository.findByProductId(productId);
    }
    public String getOrderStatus(int orderId) {
        return ordersRepository.getOrderStatusByOrderId(orderId);
    }
    public int getOrderQuantity(int orderId) {
        return ordersRepository.getOrderQuantityByOrderId(orderId);
    }

}
