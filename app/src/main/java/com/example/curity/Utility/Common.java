package com.example.curity.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {
    public static boolean isConnectedToInternet(Context context) {

        boolean wifiConnected;
        boolean mobileConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if (connectivityManager != null) {
            if (activeInfo != null && activeInfo.isConnected()) {
                wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
                mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;

                if(wifiConnected || mobileConnected){
                    return true;
                }
            }
        }
        return false;
    }
}