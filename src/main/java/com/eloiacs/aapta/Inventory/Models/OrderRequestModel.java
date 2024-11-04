package com.eloiacs.aapta.Inventory.Models;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestModel {

    private String orderId = null;

    private String customerId = null;

    private List<OrderItemsRequestModel> orderItemsList = new ArrayList<>();


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

    public List<OrderItemsRequestModel> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItemsRequestModel> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }
}
