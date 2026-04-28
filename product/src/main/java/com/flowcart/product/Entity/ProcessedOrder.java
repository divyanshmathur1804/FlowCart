package com.flowcart.product.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProcessedOrder {

    @Id
    private Long orderId;

    public ProcessedOrder() {
    }

    public ProcessedOrder(Long orderId) {
        this.orderId = orderId;
    }


    
}
