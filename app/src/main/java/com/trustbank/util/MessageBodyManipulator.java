package com.trustbank.util;

import android.util.Base64;


import com.trustbank.BuildConfig;

import org.json.JSONObject;

public class  MessageBodyManipulator {

    //private static final String secretKey = BuildConfig.enc_key;

    public static String encryptRequestBody(String data) throws Exception {
        if (AppConstants.isInterceptorEnabled) {
            JSONObject jsonRequest = new JSONObject();
            /*jsonRequest.put("d", AESEnDecryption.encrypt(Base64.decode(secretKey, Base64.DEFAULT),
                    data.getBytes()).replaceAll("\n", ""));*/
            jsonRequest.put("d", AESEnDecryption.encrypt(Base64.decode(JetPackSecurePreference.storeAndRetriveSecretKey(), Base64.DEFAULT),
                    data.getBytes()).replaceAll("\n", ""));
            return jsonRequest.toString();
        } else {
            return data;
        }
    }

    public static String decryptResponseBody(String data) throws Exception {
        if (AppConstants.isInterceptorEnabled) {

           // JetPackSecurePreference.storeAndRetriveSecretKey();
            JSONObject jsonResponse = new JSONObject(data);
            if (jsonResponse.has("response_code") && jsonResponse.getString("response_code").equals("1")) {
                byte[] base64DecodedBytes = Base64.decode(jsonResponse.getString("response"), Base64.DEFAULT);
               // return AESEnDecryption.decrypt(Base64.decode(secretKey, Base64.DEFAULT), base64DecodedBytes);
                return AESEnDecryption.decrypt(Base64.decode(JetPackSecurePreference.storeAndRetriveSecretKey(), Base64.DEFAULT), base64DecodedBytes);
            } else {
                return data;

            }
        } else {
            return data;
        }
    }

}
