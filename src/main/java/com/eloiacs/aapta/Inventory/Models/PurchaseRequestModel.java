package com.eloiacs.aapta.Inventory.Models;

public class PurchaseRequestModel {

    private int productId = 0;

    private int quantity = 0;

    private double purchasePrice = 0;

    private double mrp = 0;

    private int salesPercentage = 0;

    private double salesPrice = 0;

    private int wholesalePercentage = 0;

    private double wholesalePrice = 0;


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public int getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(int salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getWholesalePercentage() {
        return wholesalePercentage;
    }

    public void setWholesalePercentage(int wholesalePercentage) {
        this.wholesalePercentage = wholesalePercentage;
    }

    public double getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(double wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }
}
