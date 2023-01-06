package com.trustbank.Model;

import java.io.Serializable;

public class IMPSTransactionRequestModel implements Serializable {

    private String logTime;
    private String switchReqType;
    private String switchResCode;
    private String rrn;
    private String channelRefNo;
    private String amount;
    private String benMobile;
    private String benAcno;
    private String benIfsc;
    private String benMmid;
    private String benName;
    private String remMobile;
    private String remAcno;
    private String remName;
    private String tranType;
    private String impsResultMessage;
    private String transDateTime;
    private String benUpiId;
    private String bbpsBillerId;


    public String getBenUpiId() {
        return benUpiId;
    }

    public void setBenUpiId(String benUpiId) {
        this.benUpiId = benUpiId;
    }

    public String getTransDateTime() {
        return transDateTime;
    }

    public void setTransDateTime(String transDateTime) {
        this.transDateTime = transDateTime;
    }

    public String getImpsResultMessage() {
        return impsResultMessage;
    }

    public void setImpsResultMessage(String impsResultMessage) {
        this.impsResultMessage = impsResultMessage;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getSwitchReqType() {
        return switchReqType;
    }

    public void setSwitchReqType(String switchReqType) {
        this.switchReqType = switchReqType;
    }

    public String getSwitchResCode() {
        return switchResCode;
    }

    public void setSwitchResCode(String switchResCode) {
        this.switchResCode = switchResCode;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getChannelRefNo() {
        return channelRefNo;
    }

    public void setChannelRefNo(String channelRefNo) {
        this.channelRefNo = channelRefNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBenMobile() {
        return benMobile;
    }

    public void setBenMobile(String benMobile) {
        this.benMobile = benMobile;
    }

    public String getBenAcno() {
        return benAcno;
    }

    public void setBenAcno(String benAcno) {
        this.benAcno = benAcno;
    }

    public String getBenIfsc() {
        return benIfsc;
    }

    public void setBenIfsc(String benIfsc) {
        this.benIfsc = benIfsc;
    }

    public String getBenMmid() {
        return benMmid;
    }

    public void setBenMmid(String benMmid) {
        this.benMmid = benMmid;
    }

    public String getBenName() {
        return benName;
    }

    public void setBenName(String benName) {
        this.benName = benName;
    }

    public String getRemMobile() {
        return remMobile;
    }

    public void setRemMobile(String remMobile) {
        this.remMobile = remMobile;
    }

    public String getRemAcno() {
        return remAcno;
    }

    public void setRemAcno(String remAcno) {
        this.remAcno = remAcno;
    }

    public String getRemName() {
        return remName;
    }

    public void setRemName(String remName) {
        this.remName = remName;
    }


    public String getBbpsBillerId() {
        return bbpsBillerId;
    }

    public void setBbpsBillerId(String bbpsBillerId) {
        this.bbpsBillerId = bbpsBillerId;
    }
}
