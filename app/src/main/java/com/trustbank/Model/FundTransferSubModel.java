package com.trustbank.Model;

import java.io.Serializable;

public class FundTransferSubModel implements Serializable{

    private String accNo, remitterAccName, toBenName, benId, benAccName, benAccNo,
            benIfscCode, benMobNo, benMmid, amt, remark,toAccNo,upiId,billerId,billerName,refId,billNumber;
    boolean saveFutureCheck;

    public FundTransferSubModel() {
    }


    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getToAccNo() {
        return toAccNo;
    }

    public void setToAccNo(String toAccNo) {
        this.toAccNo = toAccNo;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getBenId() {
        return benId;
    }

    public void setBenId(String benId) {
        this.benId = benId;
    }

    public String getToBenName() {
        return toBenName;
    }

    public void setToBenName(String toBenName) {
        this.toBenName = toBenName;
    }

    public String getBenAccName() {
        return benAccName;
    }

    public void setBenAccName(String benAccName) {
        this.benAccName = benAccName;
    }

    public String getBenAccNo() {
        return benAccNo;
    }

    public void setBenAccNo(String benAccNo) {
        this.benAccNo = benAccNo;
    }

    public String getBenIfscCode() {
        return benIfscCode;
    }

    public void setBenIfscCode(String benIfscCode) {
        this.benIfscCode = benIfscCode;
    }

    public String getBenMobNo() {
        return benMobNo;
    }

    public void setBenMobNo(String benMobNo) {
        this.benMobNo = benMobNo;
    }

    public String getBenMmid() {
        return benMmid;
    }

    public void setBenMmid(String benMmid) {
        this.benMmid = benMmid;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isSaveFutureCheck() {
        return saveFutureCheck;
    }

    public void setSaveFutureCheck(boolean saveFutureCheck) {
        this.saveFutureCheck = saveFutureCheck;
    }

    public String getRemitterAccName() {
        return remitterAccName;
    }

    public void setRemitterAccName(String remitterAccName) {
        this.remitterAccName = remitterAccName;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
