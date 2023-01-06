package com.trustbank.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BBPSBillerResponseModel implements Serializable {

    private String customerName;
    private String amount;
    private String dueDate;
    private String billDate;
    private String billNumber;
    private String billPeriod;
    private HashMap<String, Object> billTagsMap;


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBillPeriod() {
        return billPeriod;
    }

    public void setBillPeriod(String billPeriod) {
        this.billPeriod = billPeriod;
    }

    public HashMap<String, Object> getBillTagsMap() {
        return billTagsMap;
    }

    public void setBillTagsMap(LinkedHashMap<String, Object> billTagsMap) {
        this.billTagsMap = billTagsMap;
    }
}
