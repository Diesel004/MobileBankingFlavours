package com.trustbank.Model;

import java.io.Serializable;

public class ComplaintsListModel implements Serializable {
    private String txnReferenceId;
    private String amount;
    private String txnDate;
    private String agentId;
    private String billerId;
    private String txnStatus;

    public String getTxnReferenceId() {
        return txnReferenceId;
    }

    public void setTxnReferenceId(String txnReferenceId) {
        this.txnReferenceId = txnReferenceId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }
}
