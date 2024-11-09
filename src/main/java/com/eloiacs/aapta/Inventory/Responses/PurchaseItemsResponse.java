package com.eloiacs.aapta.Inventory.Responses;

public class PurchaseItemsResponse {

    private int purchaseItemId = 0;

    private String purchaseItemOrderId = null;

    private int productId = 0;

    private int quantity = 0;

    private double purchasePrice = 0;

    private String purchaseItemCreatedAt = null;


    public String getPurchaseItemCreatedAt() {
        return purchaseItemCreatedAt;
    }

    public void setPurchaseItemCreatedAt(String purchaseItemCreatedAt) {
        this.purchaseItemCreatedAt = purchaseItemCreatedAt;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getPurchaseItemOrderId() {
        return purchaseItemOrderId;
    }

    public void setPurchaseItemOrderId(String purchaseItemOrderId) {
        this.purchaseItemOrderId = purchaseItemOrderId;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }
}
