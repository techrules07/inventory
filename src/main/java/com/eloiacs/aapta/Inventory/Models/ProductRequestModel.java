package com.eloiacs.aapta.Inventory.Models;

import java.util.ArrayList;
import java.util.List;

public class ProductRequestModel {

    private int productId = 0;

    private String productName = null;

    private int statusTypeId = 0;

    private int categoryId = 0;

    private int subCategoryId = 0;

    private int brandId = 0;

    private int unitId = 0;

    private int quantity = 0;

    private int minPurchaseQuantity = 0;

    private int barcodeType = 0;

    private String barcodeNo = null;

    private String description = null;

    private int purchasePrice = 0;

    private int salesPricePercentage = 0;

    private int mrp = 0;

    private int wholesalePricePercentage = 0;

    private int threshold = 0;

    private Boolean billOfMaterials = false;

    private List<BillOfMaterialsRequestModel> billOfMaterialsList = new ArrayList<>();

    private Boolean freebie = false;

    private int freebieProductId = 0;

    private List<String> images = new ArrayList<>();


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

    public int getStatusTypeId() {
        return statusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        this.statusTypeId = statusTypeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinPurchaseQuantity() {
        return minPurchaseQuantity;
    }

    public void setMinPurchaseQuantity(int minPurchaseQuantity) {
        this.minPurchaseQuantity = minPurchaseQuantity;
    }

    public int getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(int barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getBarcodeNo() {
        return barcodeNo;
    }

    public void setBarcodeNo(String barcodeNo) {
        this.barcodeNo = barcodeNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getSalesPricePercentage() {
        return salesPricePercentage;
    }

    public void setSalesPricePercentage(int salesPricePercentage) {
        this.salesPricePercentage = salesPricePercentage;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public int getWholesalePricePercentage() {
        return wholesalePricePercentage;
    }

    public void setWholesalePricePercentage(int wholesalePricePercentage) {
        this.wholesalePricePercentage = wholesalePricePercentage;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Boolean getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(Boolean billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }

    public List<BillOfMaterialsRequestModel> getBillOfMaterialsList() {
        return billOfMaterialsList;
    }

    public void setBillOfMaterialsList(List<BillOfMaterialsRequestModel> billOfMaterialsList) {
        this.billOfMaterialsList = billOfMaterialsList;
    }

    public Boolean getFreebie() {
        return freebie;
    }

    public void setFreebie(Boolean freebie) {
        this.freebie = freebie;
    }

    public int getFreebieProductId() {
        return freebieProductId;
    }

    public void setFreebieProductId(int freebieProductId) {
        this.freebieProductId = freebieProductId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
