package com.trustbank.Model;

import java.io.Serializable;

/**
 * Created by Trust on 25-05-2018.
 */

public class CheckSimInfoModel implements Serializable {

    private String displayName;
    private String simSerialNumber;
    private String mobileNumber;
    private String slot;
    private String SIM_SUBSCRIPTION_ID="-1";

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getSIM_SUBSCRIPTION_ID() {
        return SIM_SUBSCRIPTION_ID;
    }

    public void setSIM_SUBSCRIPTION_ID(String SIM_SUBSCRIPTION_ID) {
        this.SIM_SUBSCRIPTION_ID = SIM_SUBSCRIPTION_ID;
    }

}
