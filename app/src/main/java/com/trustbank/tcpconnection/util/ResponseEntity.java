package com.trustbank.tcpconnection.util;

import com.google.gson.GsonBuilder;

public class ResponseEntity {
    public String error_code = "";
    public String error_message = "";
    public int response_code;
    public Object response = "";

    public static ResponseEntity CreateSuccess(Object response) {
        ResponseEntity res = new ResponseEntity();
        res.response_code = 1;
        res.response = response;
        //res.gsp_auth_token = gsp_auth_token
        return res;
    }

    public static ResponseEntity CreateError(String errorCode, String errorMessage) {
        ResponseEntity res = new ResponseEntity();
        res.response_code = 0;
        res.error_code = errorCode;
        res.error_message = errorMessage;
        //res.gsp_auth_token = gsp_auth_token
        return res;
    }

    public static ResponseEntity CreateError(String errorCode, String errorMessage, Object response) {
        ResponseEntity res = new ResponseEntity();
        res.response_code = 0;
        res.response = response;
        res.error_code = errorCode;
        res.error_message = errorMessage;
        // res.gsp_auth_token = gsp_auth_token
        return res;
    }

    public static String CreateSuccessJson(Object response) {
        ResponseEntity res = ResponseEntity.CreateSuccess(response);
        return (new GsonBuilder().disableHtmlEscaping().create()).toJson(res);
    }

    public static String CreateErrorJson(String errorCode__1, String errorMessage, Object response) {
        return (new GsonBuilder().disableHtmlEscaping().create()).toJson(ResponseEntity.CreateError(errorCode__1, errorMessage, response));
    }

    public String CreateResponseJson() {
        if (this.response_code == 0) {
            return ResponseEntity.CreateErrorJson(this.error_code, this.error_message, this.response);
        } else {
            return ResponseEntity.CreateSuccessJson(this.response);
        }
    }
}