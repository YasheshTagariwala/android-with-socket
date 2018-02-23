package com.example.yash.nodesocketmessaging;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SocketService extends JobService {
    SocketClass socketClass = new SocketClass();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (isNetworkAvailable()) {
            if (!SocketClass.getSocket().connected()) {
                socketClass.SocketInitialize(getApplicationContext());
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        socketClass.disconnectSocket();
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
