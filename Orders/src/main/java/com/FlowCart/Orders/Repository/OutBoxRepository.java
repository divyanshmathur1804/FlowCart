package com.FlowCart.Orders.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FlowCart.Orders.Events.OutBoxEvents;

public interface OutBoxRepository extends JpaRepository<OutBoxEvents, Long> {
    List<OutBoxEvents> findByStatus(String status);
}
