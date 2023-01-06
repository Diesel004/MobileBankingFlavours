package com.trustbank.Model;

public class GetUserProfileModal {
    private String accNo, mmid, name, actType;

    String ac_status;
    String is_imps_reg;
    String accountid;
    String orgelementid;
    String acTypeCode;

    public GetUserProfileModal() {}

    public String getAcTypeCode() {
        return acTypeCode;
    }

    public void setAcTypeCode(String acTypeCode) {
        this.acTypeCode = acTypeCode;
    }

    public String getAc_status() {
        return ac_status;
    }

    public void setAc_status(String ac_status) {
        this.ac_status = ac_status;
    }

    public String getIs_imps_reg() {
        return is_imps_reg;
    }

    public void setIs_imps_reg(String is_imps_reg) {
        this.is_imps_reg = is_imps_reg;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getOrgelementid() {
        return orgelementid;
    }

    public void setOrgelementid(String orgelementid) {
        this.orgelementid = orgelementid;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getMmid() {
        return mmid;
    }

    public void setMmid(String mmid) {
        this.mmid = mmid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
