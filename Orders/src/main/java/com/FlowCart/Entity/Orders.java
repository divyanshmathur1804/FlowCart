package com.FlowCart.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int orderId;
    int productId;
    int quantity;
    String orderStatus;

    
    public Orders() {
    }


    
    public Orders(int orderId, int productId, int quantity, String orderStatus) {
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



    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    
}
