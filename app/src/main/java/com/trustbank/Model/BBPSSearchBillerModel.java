package com.trustbank.Model;

import java.io.Serializable;

public class BBPSSearchBillerModel implements Serializable {

    private String billerId;
    private String billerName;
    private String billerAliasName;
    private String billerCategoryName;
    private String billerMode;
    private String billerAcceptsAdhoc;
    private String parentBiller;
    private String parentBillerId;
    private String billerCoverage;
    private String fetchRequirement;
    private String paymentAmountExactness;
    private String supportBillValidation;
    private String billerEffctvFrom;
    private String status;


    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getBillerAliasName() {
        return billerAliasName;
    }

    public void setBillerAliasName(String billerAliasName) {
        this.billerAliasName = billerAliasName;
    }

    public String getBillerCategoryName() {
        return billerCategoryName;
    }

    public void setBillerCategoryName(String billerCategoryName) {
        this.billerCategoryName = billerCategoryName;
    }

    public String getBillerMode() {
        return billerMode;
    }

    public void setBillerMode(String billerMode) {
        this.billerMode = billerMode;
    }

    public String getBillerAcceptsAdhoc() {
        return billerAcceptsAdhoc;
    }

    public void setBillerAcceptsAdhoc(String billerAcceptsAdhoc) {
        this.billerAcceptsAdhoc = billerAcceptsAdhoc;
    }

    public String getParentBiller() {
        return parentBiller;
    }

    public void setParentBiller(String parentBiller) {
        this.parentBiller = parentBiller;
    }

    public String getParentBillerId() {
        return parentBillerId;
    }

    public void setParentBillerId(String parentBillerId) {
        this.parentBillerId = parentBillerId;
    }

    public String getBillerCoverage() {
        return billerCoverage;
    }

    public void setBillerCoverage(String billerCoverage) {
        this.billerCoverage = billerCoverage;
    }

    public String getFetchRequirement() {
        return fetchRequirement;
    }

    public void setFetchRequirement(String fetchRequirement) {
        this.fetchRequirement = fetchRequirement;
    }

    public String getPaymentAmountExactness() {
        return paymentAmountExactness;
    }

    public void setPaymentAmountExactness(String paymentAmountExactness) {
        this.paymentAmountExactness = paymentAmountExactness;
    }

    public String getSupportBillValidation() {
        return supportBillValidation;
    }

    public void setSupportBillValidation(String supportBillValidation) {
        this.supportBillValidation = supportBillValidation;
    }

    public String getBillerEffctvFrom() {
        return billerEffctvFrom;
    }

    public void setBillerEffctvFrom(String billerEffctvFrom) {
        this.billerEffctvFrom = billerEffctvFrom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
