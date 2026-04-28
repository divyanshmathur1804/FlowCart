package com.FlowCart.Orders.Entity;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders implements Serializable { // using Serializable to convert the object into a byte stream and send it to the kafka topic

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;
    Long productId;
    Long quantity;
    String orderStatus;

    
    public Orders() {
    }


    
    public Orders(Long orderId, Long productId, Long quantity, String orderStatus) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
    }



    @Override
    public String toString() {
        return "Orders [orderId=" + orderId + ", productId=" + productId + ", quantity=" + quantity + ", orderStatus="
                + orderStatus + "]";
    }



    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    
}
