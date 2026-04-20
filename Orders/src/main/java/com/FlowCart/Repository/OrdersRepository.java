package com.FlowCart.Repository;

import org.springframework.stereotype.Repository;

import com.FlowCart.Entity.Orders;

import java.util.List;


@Repository
public interface OrdersRepository extends org.springframework.data.jpa.repository.JpaRepository<com.FlowCart.Entity.Orders, Integer> {
    public Orders findByOrderId(int orderId);
    public List<Orders> findByProductId(int productId);
    public String getOrderStatusByOrderId(int orderId);
    public int getOrderQuantityByOrderId(int orderId);
    public void updateOrderStatusByOrderId(int orderId, String orderStatus);
    public void updateOrderQuantityByOrderId(int orderId, int quantity);
    public void deleteByOrderId(int orderId);
    public List<Orders> findAll();

    
}
