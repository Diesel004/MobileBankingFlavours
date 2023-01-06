package com.trustbank.Model;

public class LastFiveImpsTransactionModel {
    private String date, amount, transactionType, beneficiery, refNo;

    public LastFiveImpsTransactionModel(String date, String amount, String transactionType, String beneficiery, String refNo) {
        this.date = date;
        this.amount = amount;
        this.transactionType = transactionType;
        this.beneficiery = beneficiery;
        this.refNo = refNo;
    }

    public LastFiveImpsTransactionModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getBeneficiery() {
        return beneficiery;
    }

    public void setBeneficiery(String beneficiery) {
        this.beneficiery = beneficiery;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }
}
