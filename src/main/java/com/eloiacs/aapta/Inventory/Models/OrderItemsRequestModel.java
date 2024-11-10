package com.eloiacs.aapta.Inventory.Models;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class OrderItemsRequestModel {

    private String orderId = null;

    private int productId = 0;

    private int discount = 0;

    private int quantity = 0;

    private Boolean manuallyEntered = false;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Boolean isManuallyEntered() {
        return manuallyEntered;
    }

    public void setManuallyEntered(boolean manuallyEntered) {
        this.manuallyEntered = manuallyEntered;
    }
}
