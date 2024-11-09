package com.eloiacs.aapta.Inventory.Responses;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

    private int productId = 0;

    private String productName = null;

    private String HSNCode = null;

    private int statusTypeId = 0;

    private String statusType = null;

    private int categoryId = 0;

    private String category = null;

    private int subCategoryId = 0;

    private String subCategory = null;

    private int brandId = 0;

    private String brand = null;

    private int unitId = 0;

    private String unit = null;

    private int sizeId = 0;

    private String size = null;

    private int minPurchaseQuantity = 0;

    private int barcodeType = 0;

    private String barcodeNo = null;

    private String description = null;

    private Boolean billOfMaterials = false;

    private List<BillOfMaterialsResponse> billOfMaterialsList = new ArrayList<>();

    private Boolean freebie = false;

    private int freebieProductId = 0;

    private String freebieProductName = null;

    private List<String> images = new ArrayList<>();

    private Boolean isActive = false;

    private String createdAt = null;

    private int createdById = 0;

    private String createdBy = null;


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

    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }

    public int getStatusTypeId() {
        return statusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        this.statusTypeId = statusTypeId;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public Boolean getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(Boolean billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }

    public List<BillOfMaterialsResponse> getBillOfMaterialsList() {
        return billOfMaterialsList;
    }

    public void setBillOfMaterialsList(List<BillOfMaterialsResponse> billOfMaterialsList) {
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

    public String getFreebieProductName() {
        return freebieProductName;
    }

    public void setFreebieProductName(String freebieProductName) {
        this.freebieProductName = freebieProductName;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
}
