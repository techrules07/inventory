package com.eloiacs.aapta.Inventory.Responses;

public class UnitResponseModel {

    private  int id = 0;

    private  String unitName = null;

    private String unitSmall = null;

    private String createdBy = null;

    private String modifiedBy = null;

    private String createdAt = null;

    private  String modifiedAt = null;

    private boolean isActive = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitSmall() {
        return unitSmall;
    }

    public void setUnitSmall(String unitSmall) {
        this.unitSmall = unitSmall;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
