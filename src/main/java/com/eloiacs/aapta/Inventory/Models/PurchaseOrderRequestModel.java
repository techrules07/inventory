package com.eloiacs.aapta.Inventory.Models;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderRequestModel {

    private int supplierId = 0;

    private String purchaseDate = null;

    private String invoiceId = null;

    private String invoiceImage = null;

    private List<PurchaseRequestModel> purchaseItems = new ArrayList<>();


    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceImage() {
        return invoiceImage;
    }

    public void setInvoiceImage(String invoiceImage) {
        this.invoiceImage = invoiceImage;
    }

    public List<PurchaseRequestModel> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseRequestModel> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }
}
