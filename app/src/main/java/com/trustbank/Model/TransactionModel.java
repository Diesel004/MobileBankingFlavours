package com.trustbank.Model;

import java.io.Serializable;

public class TransactionModel implements Serializable {

    private int trfType;
    private String trfTypeText;
    private int limitType;
    private String limitTypeText;
    private String limit_value;


    public int getTrfType() {
        return trfType;
    }

    public void setTrfType(int trfType) {
        this.trfType = trfType;
    }

    public String getTrfTypeText() {
        return trfTypeText;
    }

    public void setTrfTypeText(String trfTypeText) {
        this.trfTypeText = trfTypeText;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public String getLimitTypeText() {
        return limitTypeText;
    }

    public void setLimitTypeText(String limitTypeText) {
        this.limitTypeText = limitTypeText;
    }

    public String getLimit_value() {
        return limit_value;
    }

    public void setLimit_value(String limit_value) {
        this.limit_value = limit_value;
    }
}
