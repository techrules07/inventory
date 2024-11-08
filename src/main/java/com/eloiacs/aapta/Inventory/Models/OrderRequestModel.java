package com.eloiacs.aapta.Inventory.Models;

public class OrderRequestModel {

    private String orderId = null;

    private String customerId = null;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
