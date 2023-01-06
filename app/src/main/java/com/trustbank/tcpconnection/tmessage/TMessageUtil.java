package com.trustbank.tcpconnection.tmessage;

import android.content.Context;
import android.util.Log;

import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import org.json.JSONObject;

import java.lang.*;
import java.text.*;
import java.util.*;

public class TMessageUtil {
    public static final String MSG_ORIGINATING_CHANNEL = "Mobile";
    public static String MSG_INSTITUTION_ID = "";
    public static String SERVER = "";
    public static int PORT = 0;
    public static String response = null;
    private static String result;
    private static String error = "NA";
    private static String stan;
    private static String rrn;
    private static String rrn_cbs;
    private static String channel_ref_no;
    private static GenerateStanRRNModel generateStanRRNModel;


    public static void Init(String INSTITUTION_ID, String server, int port) {
        TMessageUtil.MSG_INSTITUTION_ID = INSTITUTION_ID;
        TMessageUtil.SERVER = server;
        TMessageUtil.PORT = port;
    }

    public static String GetLocalTxnDtTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        Log.d("DateTime", dateFormat.format(date));
        return dateFormat.format(date);
    }


    public static GenerateStanRRNModel GetNextStanRrn(Context context, String jsonString, String url, String auth_token) {
        try {
            if (!url.equals("")) {
                result = HttpClientWrapper.postWitAuthHeader(url, jsonString, auth_token);
            }
            generateStanRRNModel = new GenerateStanRRNModel();
            if (result == null || result.equals("")) {
                error = AppConstants.SERVER_NOT_RESPONDING;
                generateStanRRNModel.setError(error);
            }
            JSONObject jsonResponse = (new JSONObject(result));
            if (jsonResponse.has("error")) {
                error = jsonResponse.getString("error");
                generateStanRRNModel.setError(error);
            }
            String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

            if (responseCode.equals("1")) {
                JSONObject dataObject = jsonResponse.getJSONObject("response");
                if (dataObject.has("error")) {
                    error = dataObject.getString("error");
                    generateStanRRNModel.setError(error);
                }
                stan = dataObject.has("stan") ? dataObject.getString("stan") : "";
                rrn = dataObject.has("rrn") ? dataObject.getString("rrn") : "";
                rrn_cbs = dataObject.has("rrn_cbs") ? dataObject.getString("rrn_cbs") : "";
                channel_ref_no = dataObject.has("channel_ref_no") ? dataObject.getString("channel_ref_no") : "";
                generateStanRRNModel.setStan(stan);
                generateStanRRNModel.setRrn(rrn);
                generateStanRRNModel.setCbs_rrn(rrn_cbs);
                generateStanRRNModel.setChannel_ref_no(channel_ref_no);
            } else {
                String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                generateStanRRNModel.setError(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generateStanRRNModel;
    }
}