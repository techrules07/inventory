package com.eloiacs.aapta.Inventory.Responses;

import java.util.ArrayList;
import java.util.List;

public class OrderResponse {

    private int id = 0;

    private String orderId = null;

    private String customerId = null;

    private int statusId = 0;

    private String status = null;

    private int createdById = 0;

    private String createdBy = null;

    private String createdAt = null;

    private double totalUnitPrice = 0;

    private double totalPrice = 0;

    private double totalAmount = 0;

    private double totalDiscount = 0;

    private List<OrderItemsResponse> orderItems = new ArrayList<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalUnitPrice() {
        return totalUnitPrice;
    }

    public void setTotalUnitPrice(double totalUnitPrice) {
        this.totalUnitPrice = totalUnitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<OrderItemsResponse> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemsResponse> orderItems) {
        this.orderItems = orderItems;
    }

}
