package com.trustbank.Model;

import java.io.Serializable;

public class BBPSCustomeParamater implements Serializable {

    private String paramaName;
    private Object dataType;
    private boolean optional;
    private int minLenght;
    private int maxLenght;
    private String regex;

    public String getParamaName() {
        return paramaName;
    }

    public void setParamaName(String paramaName) {
        this.paramaName = paramaName;
    }

    public Object getDataType() {
        return dataType;
    }

    public void setDataType(Object dataType) {
        this.dataType = dataType;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getMinLenght() {
        return minLenght;
    }

    public void setMinLenght(int minLenght) {
        this.minLenght = minLenght;
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(int maxLenght) {
        this.maxLenght = maxLenght;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
