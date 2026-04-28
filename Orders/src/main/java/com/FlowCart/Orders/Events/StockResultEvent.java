package com.FlowCart.Orders.Events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResultEvent {
    private Long orderId;
    private boolean success;

    public Long getOrderId() {
        return orderId;
    }

    public boolean isSuccess() {
        return success;
    }
}
