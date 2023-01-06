package com.trustbank.Model;

import java.util.List;

public class ChequeBookRequestModel {

    String accountType;
    List<NoOfLeavesModel> noOfLeavesModels;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<NoOfLeavesModel> getNoOfLeavesModels() {
        return noOfLeavesModels;
    }

    public void setNoOfLeavesModels(List<NoOfLeavesModel> noOfLeavesModels) {
        this.noOfLeavesModels = noOfLeavesModels;
    }
}
