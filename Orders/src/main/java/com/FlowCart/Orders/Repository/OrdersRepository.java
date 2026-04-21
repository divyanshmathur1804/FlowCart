package com.FlowCart.Orders.Repository;

import java.util.List;

import org.springframework.stereotype.Repository;





@Repository
public interface OrdersRepository extends org.springframework.data.jpa.repository.JpaRepository<com.FlowCart.Orders.Entity.Orders, Integer> {
    List<com.FlowCart.Orders.Entity.Orders> findAll();
    }
