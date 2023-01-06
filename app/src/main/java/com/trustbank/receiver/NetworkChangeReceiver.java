package com.trustbank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.trustbank.interfaces.NetworkChangeListner;
import com.trustbank.util.TrustMethods;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private NetworkChangeListner internetConnectionListner;

    public void initNetworkChangeListner(NetworkChangeListner internetConnectionListner) {
        this.internetConnectionListner = internetConnectionListner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                Log.e("IMPS", "Conectivity Failure !!! ");
                TrustMethods.message(context, "Internet Connected");
            } else {
                Log.e("IMPS", "Conectivity Failure !!! ");
                TrustMethods.message(context, "Internet Dis-connected");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
