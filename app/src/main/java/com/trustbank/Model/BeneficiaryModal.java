package com.trustbank.Model;

import java.io.Serializable;

public class BeneficiaryModal implements Serializable {
    private String benId;
    private String benNickname;
    private String banAccName;
    private String benType;
    private String benAccNo;
    private String benIfscCode;
    private String benMobNo;
    private String benMmid;
    private String benUpiId;

    public BeneficiaryModal(String benId, String benNickname, String banAccName, String benAccNo, String benIfscCode, String benMobNo, String benMmid) {
        this.benId = benId;
        this.benNickname = benNickname;
        this.banAccName = banAccName;
        this.benAccNo = benAccNo;
        this.benIfscCode = benIfscCode;
        this.benMobNo = benMobNo;
        this.benMmid = benMmid;
    }

    public BeneficiaryModal() {
    }

    public String getBenUpiId() {
        return benUpiId;
    }

    public void setBenUpiId(String benUpiId) {
        this.benUpiId = benUpiId;
    }

    public String getBenId() {
        return benId;
    }

    public void setBenId(String benId) {
        this.benId = benId;
    }

    public String getBenNickname() {
        return benNickname;
    }

    public void setBenNickname(String benNickname) {
        this.benNickname = benNickname;
    }

    public String getBanAccName() {
        return banAccName;
    }

    public void setBanAccName(String banAccName) {
        this.banAccName = banAccName;
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

    public String getBenType() {
        return benType;
    }

    public void setBenType(String benType) {
        this.benType = benType;
    }

}
