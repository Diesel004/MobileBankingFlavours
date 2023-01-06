package com.trustbank.Model;

public class PPSRequestModel {
    private String chequeNo;
    private String reqDate;
    private String chequeDate;
    private String amount;
    private String payeeName;
    private String status;
    private String channel;




    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
