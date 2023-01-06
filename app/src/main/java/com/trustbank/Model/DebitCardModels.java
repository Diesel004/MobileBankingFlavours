package com.trustbank.Model;

import androidx.annotation.NonNull;

public class DebitCardModels {

    private String debitCardNo;
    private String debitCardNoForDisplay;
    private String status;


    public String getDebitCardNo() {
        return debitCardNo;
    }

    public void setDebitCardNo(String debitCardNo) {
        this.debitCardNo = debitCardNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDebitCardNoForDisplay() {
        return debitCardNoForDisplay;
    }

    public void setDebitCardNoForDisplay(String debitCardNoForDisplay) {
        this.debitCardNoForDisplay = debitCardNoForDisplay;
    }

    @NonNull
    @Override
    public String toString() {
        return this.debitCardNoForDisplay; // What to display in the Spinner list.
    }
}
