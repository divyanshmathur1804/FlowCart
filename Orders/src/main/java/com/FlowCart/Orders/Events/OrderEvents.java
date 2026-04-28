package com.FlowCart.Orders.Events;

public class OrderEvents {
    private Long productId;
    private Long quantity;

    public OrderEvents() {
    }

    public OrderEvents(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
