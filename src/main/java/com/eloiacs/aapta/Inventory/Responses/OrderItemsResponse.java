package com.eloiacs.aapta.Inventory.Responses;

public class OrderItemsResponse {

    private int orderItemId = 0;

    private String orderItemOrderId = null;

    private int productId = 0;

    private String productName = null;

    private double unitPrice = 0;

    private double mrp = 0;

    private double salesPrice = 0;

    private int salesPercentage = 0;

    private double wholesalePrice = 0;

    private int wholesalePercentage = 0;

    private int quantity = 0;

    private double totalAmount = 0;

    private double discount = 0;

    private double discountAmount = 0;

    private int orderItemCreatedById = 0;

    private String orderItemCreatedBy = null;

    private String orderItemCreatedAt = null;

    private String category = null;

    private String subCategory = null;

    private String size = null;

    private String unit = null;


    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(int salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public double getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(double wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public int getWholesalePercentage() {
        return wholesalePercentage;
    }

    public void setWholesalePercentage(int wholesalePercentage) {
        this.wholesalePercentage = wholesalePercentage;
    }

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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
