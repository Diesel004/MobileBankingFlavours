package com.trustbank.Model;

public class GenerateStanRRNModel {

    String rrn;
    String cbs_rrn;
    String stan;
    String channel_ref_no;
    String error;

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCbs_rrn() {
        return cbs_rrn;
    }

    public void setCbs_rrn(String cbs_rrn) {
        this.cbs_rrn = cbs_rrn;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getChannel_ref_no() {
        return channel_ref_no;
    }

    public void setChannel_ref_no(String channel_ref_no) {
        this.channel_ref_no = channel_ref_no;
    }
}
