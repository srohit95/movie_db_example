package com.example.movie_db_example;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet_status {
    NetworkInfo nwfo;
    ConnectivityManager cm;
    Context context;

    public Internet_status(Context context){
        this.context=context;
    }

    public boolean internet_status(){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        nwfo = cm.getActiveNetworkInfo();
        if (nwfo != null && nwfo.isConnected()) {
            return nwfo.isAvailable();
        }
        return false;

    }
}
