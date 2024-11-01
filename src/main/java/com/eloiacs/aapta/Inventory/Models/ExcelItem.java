package com.eloiacs.aapta.Inventory.Models;

public class ExcelItem {

    String ItemID = null;
    String ItemCode = null;
    String ItemName = null;
    String Unit = null;
    String MRP = null;
    String SRate = null;
    String PRate = null;
    String MalayalamEnglish = null;
    String RateInclusivePurchase = null;
    String TempRate = null;
    String HSNID = null;
    String RptAccesslevel = null;
    String BlnOrder = null;
    String RateDiscPer = null;
    String PLUNo = null;


    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getSRate() {
        return SRate;
    }

    public void setSRate(String SRate) {
        this.SRate = SRate;
    }

    public String getPRate() {
        return PRate;
    }

    public void setPRate(String PRate) {
        this.PRate = PRate;
    }

    public String getMalayalamEnglish() {
        return MalayalamEnglish;
    }

    public void setMalayalamEnglish(String malayalamEnglish) {
        MalayalamEnglish = malayalamEnglish;
    }

    public String getRateInclusivePurchase() {
        return RateInclusivePurchase;
    }

    public void setRateInclusivePurchase(String rateInclusivePurchase) {
        RateInclusivePurchase = rateInclusivePurchase;
    }

    public String getTempRate() {
        return TempRate;
    }

    public void setTempRate(String tempRate) {
        TempRate = tempRate;
    }

    public String getHSNID() {
        return HSNID;
    }

    public void setHSNID(String HSNID) {
        this.HSNID = HSNID;
    }

    public String getRptAccesslevel() {
        return RptAccesslevel;
    }

    public void setRptAccesslevel(String rptAccesslevel) {
        RptAccesslevel = rptAccesslevel;
    }

    public String getBlnOrder() {
        return BlnOrder;
    }

    public void setBlnOrder(String blnOrder) {
        BlnOrder = blnOrder;
    }

    public String getRateDiscPer() {
        return RateDiscPer;
    }

    public void setRateDiscPer(String rateDiscPer) {
        RateDiscPer = rateDiscPer;
    }

    public String getPLUNo() {
        return PLUNo;
    }

    public void setPLUNo(String PLUNo) {
        this.PLUNo = PLUNo;
    }

    @Override
    public String toString() {
        return "ExcelItem{" +
                "ItemID=" + ItemID +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", Unit='" + Unit + '\'' +
                ", MRP=" + MRP +
                ", SRate=" + SRate +
                ", PRate=" + PRate +
                ", MalayalamEnglish='" + MalayalamEnglish + '\'' +
                ", RateInclusivePurchase=" + RateInclusivePurchase +
                ", TempRate=" + TempRate +
                ", HSNID=" + HSNID +
                ", RptAccesslevel=" + RptAccesslevel +
                ", BlnOrder=" + BlnOrder +
                ", RateDiscPer=" + RateDiscPer +
                ", PLUNo=" + PLUNo +
                '}';
    }
}
