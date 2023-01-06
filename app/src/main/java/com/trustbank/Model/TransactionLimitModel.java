package com.trustbank.Model;

import java.util.List;

public class TransactionLimitModel {

    String name;
    List<TransactionModel> transactionList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TransactionModel> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<TransactionModel> transactionList) {
        this.transactionList = transactionList;
    }
}
