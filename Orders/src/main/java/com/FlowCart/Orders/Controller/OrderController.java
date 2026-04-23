package com.FlowCart.Orders.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.FlowCart.Orders.Entity.Orders;
import com.FlowCart.Orders.Service.OrderServices;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;






@RestController
public class OrderController {
    private OrderServices orderServices;


    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    
    @PostMapping("/orders")
    public Orders createOrder(@RequestBody Orders order) {
        return orderServices.createOrder(order);
    }

    @GetMapping("/orders")
    public List<Orders> getAllOrders() {
        return orderServices.getAllOrders();
    }
    

    @GetMapping("/orders/{id}")
    public Orders getOrderById(@PathVariable Integer id) {
        return orderServices.getOrderById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @PutMapping("/orders/update/{id}")
    public String updateOrdersById(@PathVariable String id, @RequestBody String entity) {
        orderServices.updateOrderStatus(Integer.parseInt(id), entity);
        
        return entity;
    }

    @DeleteMapping("/orders/delete/{id}")
    public String deleteOrderById(@PathVariable int id) {
        orderServices.deleteOrder(id);
        return "Deleted order with id: " + id;
    }

    @GetMapping("order/status/{id}")
    public String getOrderStatus(@PathVariable int id) {
        return orderServices.getOrderStatus(id);
    }
    

    
}
