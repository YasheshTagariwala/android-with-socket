package com.example.yash.nodesocketmessaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SocketService extends Service {

    SocketClass socketClass;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        connectSocketAndInitialize();
        return START_STICKY;
    }

    public void connectSocketAndInitialize() {
        socketClass = new SocketClass();
        if (!SocketClass.getSocket().connected()) {
            socketClass.SocketIntialize();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketClass.disconnectSocket();
    }
}
