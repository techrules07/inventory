package com.eloiacs.aapta.Inventory.Models;

public class BillOfMaterialsRequestModel {

    private int billOfMaterialsProductId = 0;

    private int billOfMaterialsProductQuantity = 0;

    private int billOfMaterialsProductCost = 0;


    public int getBillOfMaterialsProductId() {
        return billOfMaterialsProductId;
    }

    public void setBillOfMaterialsProductId(int billOfMaterialsProductId) {
        this.billOfMaterialsProductId = billOfMaterialsProductId;
    }

    public int getBillOfMaterialsProductQuantity() {
        return billOfMaterialsProductQuantity;
    }

    public void setBillOfMaterialsProductQuantity(int billOfMaterialsProductQuantity) {
        this.billOfMaterialsProductQuantity = billOfMaterialsProductQuantity;
    }

    public int getBillOfMaterialsProductCost() {
        return billOfMaterialsProductCost;
    }

    public void setBillOfMaterialsProductCost(int billOfMaterialsProductCost) {
        this.billOfMaterialsProductCost = billOfMaterialsProductCost;
    }
}
