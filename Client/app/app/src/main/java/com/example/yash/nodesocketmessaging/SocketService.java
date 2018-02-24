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
    private String PROTOCOL = "http://";
    private String SERVER_ADDRESS = "192.168.31.32";
    private int SERVER_PORT = 3000;
    SocketClass socketClass = new SocketClass();

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (isNetworkAvailable()) {
            try {
                IO.Options options = new IO.Options();
                options.reconnection = false;
                options.reconnectionAttempts = 0;
                socket = IO.socket(PROTOCOL + SERVER_ADDRESS + ":" + SERVER_PORT + "/", options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!socket.connected()) {
                socket.connect();
                socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        onStopJob(jobParameters);
                    }
                });
                socketClass.SocketInitialize(getApplicationContext(), socket);
            }
        }
        jobFinished(jobParameters, false);
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
