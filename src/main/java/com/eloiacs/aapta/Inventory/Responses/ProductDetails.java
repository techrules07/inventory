package com.eloiacs.aapta.Inventory.Responses;

public class ProductDetails {
    private int productId = 0;
    private int category = 0;
    private int subCategory = 0;
    private int mrp = 0;
    private int salesPrice = 0;
    private int salesPercentage = 0;
    private int wholsalePrice = 0;
    private int wholesalePercentage = 0;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public int getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(int salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(int salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public int getWholsalePrice() {
        return wholsalePrice;
    }

    public void setWholsalePrice(int wholsalePrice) {
        this.wholsalePrice = wholsalePrice;
    }

    public int getWholesalePercentage() {
        return wholesalePercentage;
    }

    public void setWholesalePercentage(int wholesalePercentage) {
        this.wholesalePercentage = wholesalePercentage;
    }
}
