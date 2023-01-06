package com.trustbank.Model;

import java.io.Serializable;

public class BbpsComplaintTypeModel implements Serializable {

    private String complaint_type;
    private String disposition;

    public String getComplaint_type() {
        return complaint_type;
    }

    public void setComplaint_type(String complaint_type) {
        this.complaint_type = complaint_type;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }
}
