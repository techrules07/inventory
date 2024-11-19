package com.eloiacs.aapta.Inventory.Models;


public class OrderItemsRequestModel {

    private String orderId = null;

    private int productId = 0;

    private int discount = 0;

    private int quantity = 0;

    private Boolean manuallyEntered = false;

    private Integer typeOfDiscount = 0;

    private Integer discountAmount = 0;


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

    public Boolean getManuallyEntered() {
        return manuallyEntered;
    }

    public void setManuallyEntered(Boolean manuallyEntered) {
        this.manuallyEntered = manuallyEntered;
    }

    public Integer getTypeOfDiscount() {
        return typeOfDiscount;
    }

    public void setTypeOfDiscount(Integer typeOfDiscount) {
        this.typeOfDiscount = typeOfDiscount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }
}
