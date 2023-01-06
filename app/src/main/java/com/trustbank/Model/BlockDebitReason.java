package com.trustbank.Model;

public class BlockDebitReason {

    private String modeid;
    private String modeText;


    public String getModeid() {
        return modeid;
    }

    public void setModeid(String modeid) {
        this.modeid = modeid;
    }

    public String getModeText() {
        return modeText;
    }

    public void setModeText(String modeText) {
        this.modeText = modeText;
    }

    @Override
    public String toString() {
        return this.modeText; // What to display in the Spinner list.
    }
}
