package com.eloiacs.aapta.Inventory.Models;

public class ExcelItem {

    String ItemName = null;
    int CategoryID = 0;
    String Description = null;
    int Unit = 0;
    double MRP = 0;
    double SRate = 0;
    double PRate = 0;
    Boolean RateInclusive = false;
    int Manufacturer = 0;
    int Discount = 0;
    Boolean ActiveStatus = false;
    int HSNID = 0;
    String mfgBarcode = null;


    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getUnit() {
        return Unit;
    }

    public void setUnit(int unit) {
        Unit = unit;
    }

    public double getMRP() {
        return MRP;
    }

    public void setMRP(double MRP) {
        this.MRP = MRP;
    }

    public double getSRate() {
        return SRate;
    }

    public void setSRate(double SRate) {
        this.SRate = SRate;
    }

    public double getPRate() {
        return PRate;
    }

    public void setPRate(double PRate) {
        this.PRate = PRate;
    }

    public Boolean getRateInclusive() {
        return RateInclusive;
    }

    public void setRateInclusive(Boolean rateInclusive) {
        RateInclusive = rateInclusive;
    }

    public int getManufacturer() {
        return Manufacturer;
    }

    public void setManufacturer(int manufacturer) {
        Manufacturer = manufacturer;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDiscount(int discount) {
        Discount = discount;
    }

    public Boolean getActiveStatus() {
        return ActiveStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        ActiveStatus = activeStatus;
    }

    public int getHSNID() {
        return HSNID;
    }

    public void setHSNID(int HSNID) {
        this.HSNID = HSNID;
    }

    public String getMfgBarcode() {
        return mfgBarcode;
    }

    public void setMfgBarcode(String mfgBarcode) {
        this.mfgBarcode = mfgBarcode;
    }


    @Override
    public String toString() {
        return "ExcelItem{" +
                "ItemName='" + ItemName + '\'' +
                ", CategoryID=" + CategoryID +
                ", Description='" + Description + '\'' +
                ", Unit=" + Unit +
                ", MRP=" + MRP +
                ", SRate=" + SRate +
                ", PRate=" + PRate +
                ", RateInclusive=" + RateInclusive +
                ", Manufacturer=" + Manufacturer +
                ", Discount=" + Discount +
                ", ActiveStatus=" + ActiveStatus +
                ", HSNID=" + HSNID +
                ", mfgBarcode='" + mfgBarcode + '\'' +
                '}';
    }
}
