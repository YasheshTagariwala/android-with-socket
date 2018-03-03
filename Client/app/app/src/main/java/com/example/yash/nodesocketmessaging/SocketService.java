package com.example.yash.nodesocketmessaging;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class SocketService extends JobService {
    public Socket socket;
    SocketClass socketClass = new SocketClass();

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (isNetworkAvailable()) {
            try {
                IO.Options options = new IO.Options();
                options.reconnection = false;
                options.reconnectionAttempts = 0;
                options.reconnectionDelay = 1000;
                options.reconnectionDelayMax = 2000;
                options.randomizationFactor = 0.1;
                options.timeout = 1000;
                socket = IO.socket(SocketUtils.PROTOCOL + SocketUtils.SERVER_ADDRESS, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!socket.connected()) {
                socket.connect();
                socketClass.SocketInitialize(getApplicationContext(), socket);
                socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        socket.disconnect();
                        onStopJob(jobParameters);
                    }
                });
            }
        }
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (SocketClass.getSocket() != null) {
            socketClass.disconnectSocket();
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
