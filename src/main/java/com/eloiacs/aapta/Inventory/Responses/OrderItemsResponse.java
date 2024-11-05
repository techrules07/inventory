package com.eloiacs.aapta.Inventory.Responses;

public class OrderItemsResponse {

    private int orderItemId = 0;

    private String orderItemOrderId = null;

    private int productId = 0;

    private String productName = null;

    private double unitPrice = 0;

    private int quantity = 0;

    private double totalAmount = 0;

    private int discount = 0;

    private int orderItemCreatedById = 0;

    private String orderItemCreatedBy = null;

    private String orderItemCreatedAt = null;


    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderItemOrderId() {
        return orderItemOrderId;
    }

    public void setOrderItemOrderId(String orderItemOrderId) {
        this.orderItemOrderId = orderItemOrderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getOrderItemCreatedById() {
        return orderItemCreatedById;
    }

    public void setOrderItemCreatedById(int orderItemCreatedById) {
        this.orderItemCreatedById = orderItemCreatedById;
    }

    public String getOrderItemCreatedBy() {
        return orderItemCreatedBy;
    }

    public void setOrderItemCreatedBy(String orderItemCreatedBy) {
        this.orderItemCreatedBy = orderItemCreatedBy;
    }

    public String getOrderItemCreatedAt() {
        return orderItemCreatedAt;
    }

    public void setOrderItemCreatedAt(String orderItemCreatedAt) {
        this.orderItemCreatedAt = orderItemCreatedAt;
    }
}
