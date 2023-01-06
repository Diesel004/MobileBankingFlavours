package com.trustbank.Model;

public class Transaction_list_Model {
    String transaction_date;

    String transaction_id;
    String consumer_mob_number;
    String consumer_id;
    String bbpou_name;
    String biller_id;
    String biller_name;
    String category;
    String transaction_amt;
    String cust_convi_fees;
    String charges;
    String status;
    private String transactionRefNo ;
    private  String transactionType;


    public String getTransactionRefNo() {
        return transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getConsumer_mob_number() {
        return consumer_mob_number;
    }

    public void setConsumer_mob_number(String consumer_mob_number) {
        this.consumer_mob_number = consumer_mob_number;
    }

    public String getConsumer_id() {
        return consumer_id;
    }

    public void setConsumer_id(String consumer_id) {
        this.consumer_id = consumer_id;
    }

    public String getBbpou_name() {
        return bbpou_name;
    }

    public void setBbpou_name(String bbpou_name) {
        this.bbpou_name = bbpou_name;
    }

    public String getBiller_id() {
        return biller_id;
    }

    public void setBiller_id(String biller_id) {
        this.biller_id = biller_id;
    }

    public String getBiller_name() {
        return biller_name;
    }

    public void setBiller_name(String biller_name) {
        this.biller_name = biller_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTransaction_amt() {
        return transaction_amt;
    }

    public void setTransaction_amt(String transaction_amt) {
        this.transaction_amt = transaction_amt;
    }

    public String getCust_convi_fees() {
        return cust_convi_fees;
    }

    public void setCust_convi_fees(String cust_convi_fees) {
        this.cust_convi_fees = cust_convi_fees;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
