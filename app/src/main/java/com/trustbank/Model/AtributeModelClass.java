package com.trustbank.Model;

import android.view.View;

public  class AtributeModelClass {

    private  String name;
    private View view;
    private  BBPSCustomeParamater bbpsCustomeParamater;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public BBPSCustomeParamater getBbpsCustomeParamater() {
        return bbpsCustomeParamater;
    }

    public void setBbpsCustomeParamater(BBPSCustomeParamater bbpsCustomeParamater) {
        this.bbpsCustomeParamater = bbpsCustomeParamater;
    }
}
