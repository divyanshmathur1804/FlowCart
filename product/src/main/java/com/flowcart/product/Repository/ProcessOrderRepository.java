package com.flowcart.product.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowcart.product.Entity.ProcessedOrder;

public interface ProcessOrderRepository extends JpaRepository<ProcessedOrder, Long> {
    
}
