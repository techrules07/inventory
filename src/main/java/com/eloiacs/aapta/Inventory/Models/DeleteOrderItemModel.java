package com.eloiacs.aapta.Inventory.Models;

public class DeleteOrderItemModel {

    private String orderId = null;

    private int productId = 0;


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
}
