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

    private int sizeId = 0;

    private int barcodeType = 0;

    private String barcodeNo = null;

    private String description = null;

    private Boolean billOfMaterials = false;

    private List<BillOfMaterialsRequestModel> billOfMaterialsList = new ArrayList<>();

    private Boolean freebie = false;

    private int freebieProductId = 0;

    private List<String> images = new ArrayList<>();


    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
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
