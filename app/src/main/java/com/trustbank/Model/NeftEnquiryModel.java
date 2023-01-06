package com.trustbank.Model;

import java.io.Serializable;

public class NeftEnquiryModel implements Serializable {

    private String TransactionID;
    private String Amount;
    private String Tran_Date;
    private String Debit_Account;
    private String Ben_Name;
    private String To_AccountNo;
    private String IFSC;
    private String Status;
    private String Remarks;
    private String Error;
    private String utr;private String resUtr;

    public NeftEnquiryModel(String transactionID, String amount, String tran_Date, String debit_Account, String ben_Name, String to_AccountNo, String IFSC, String status, String remarks, String error, String utr,String resUtr) {
        this.TransactionID = transactionID;
        this.Amount = amount;
        this.Tran_Date = tran_Date;
        this.Debit_Account = debit_Account;
        this.Ben_Name = ben_Name;
        this.To_AccountNo = to_AccountNo;
        this.IFSC = IFSC;
        this.Status = status;
        this.Remarks = remarks;
        this.Error = error;
        this.utr = utr;
        this.resUtr = resUtr;
    }

    public String getResUtr() {
        return resUtr;
    }

    public void setResUtr(String resUtr) {
        this.resUtr = resUtr;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        this.TransactionID = transactionID;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        this.Amount = amount;
    }

    public String getTran_Date() {
        return Tran_Date;
    }

    public void setTran_Date(String tran_Date) {
        this.Tran_Date = tran_Date;
    }

    public String getDebit_Account() {
        return Debit_Account;
    }

    public void setDebit_Account(String debit_Account) {
        this.Debit_Account = debit_Account;
    }

    public String getBen_Name() {
        return Ben_Name;
    }

    public void setBen_Name(String ben_Name) {
        this.Ben_Name = ben_Name;
    }

    public String getTo_AccountNo() {
        return To_AccountNo;
    }

    public void setTo_AccountNo(String to_AccountNo) {
        this.To_AccountNo = to_AccountNo;
    }

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        this.Remarks = remarks;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        this.Error = error;
    }

    public String getUtr() {
        return utr;
    }

    public void setUtr(String utr) {
        this.utr = utr;
    }

}
