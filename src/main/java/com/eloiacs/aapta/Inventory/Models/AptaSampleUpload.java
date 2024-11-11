package com.eloiacs.aapta.Inventory.Models;

public class AptaSampleUpload {

    private String ItemName = null;

    private double SalesPrice = 0;

    private double WholesalesPrice = 0;


    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public double getSalesPrice() {
        return SalesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        SalesPrice = salesPrice;
    }

    public double getWholesalesPrice() {
        return WholesalesPrice;
    }

    public void setWholesalesPrice(double wholesalesPrice) {
        this.WholesalesPrice = wholesalesPrice;
    }
}
