package com.eloiacs.aapta.Inventory.Responses;

public class BillOfMaterialsResponse {

    private int billOfMaterialsProductId = 0;

    private String billOfMaterialsProductName = null;

    private int billOfMaterialsProductQuantity = 0;

    private int billOfMaterialsProductCost = 0;


    public int getBillOfMaterialsProductId() {
        return billOfMaterialsProductId;
    }

    public void setBillOfMaterialsProductId(int billOfMaterialsProductId) {
        this.billOfMaterialsProductId = billOfMaterialsProductId;
    }

    public String getBillOfMaterialsProductName() {
        return billOfMaterialsProductName;
    }

    public void setBillOfMaterialsProductName(String billOfMaterialsProductName) {
        this.billOfMaterialsProductName = billOfMaterialsProductName;
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
