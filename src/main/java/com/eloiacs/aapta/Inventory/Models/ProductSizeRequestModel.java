package com.eloiacs.aapta.Inventory.Models;

public class ProductSizeRequestModel {

    private int id = 0;
    private String sizeName = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
