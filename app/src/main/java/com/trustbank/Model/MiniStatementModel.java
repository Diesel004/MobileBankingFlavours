package com.trustbank.Model;

public class MiniStatementModel {
    private String accNo, AvailBal, date, amt, remarks;
    private int accType;


    public MiniStatementModel() {
    }

    public int getAccType() {
        return accType;
    }

    public void setAccType(int accType) {
        this.accType = accType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAvailBal() {
        return AvailBal;
    }

    public void setAvailBal(String availBal) {
        AvailBal = availBal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
