package com.example.yash.nodesocketmessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yash on 17-02-2018.
 */

public class SocketClass {

    public static Socket socket;
    private Context context;

    public SocketClass() {
    }

    public void SocketInitialize(Context context, Socket socket) {
        this.context = context;
        SocketClass.socket = socket;
        SocketClass.socket.on("privateMessageGet", handleIncomingPrivateMessages);
        SocketClass.socket.on("groupMessage", handleIncomingGroupMessages);
        SocketClass.socket.on("connected", showConnectedInfo);
        SocketClass.socket.on("ping", heartBeat);
        SocketClass.socket.on("pushMessage", pushMessageNotification);
    }

    private Emitter.Listener pushMessageNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            class PushMessageNotification extends AsyncTask {
                @Override
                protected Object doInBackground(Object[] objects) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    NotificationUtils notificationUtils = new NotificationUtils();
                    try {
                        notificationUtils.showMessageNotification(context, Integer.parseInt(jsonObject.getString("type")), jsonObject.getString("message"), jsonObject.getString("doer"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            new PushMessageNotification().execute();
        }
    };

    private Emitter.Listener handleIncomingGroupMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            class GroupMessage extends AsyncTask {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        MainActivity.addMessage(((JSONObject) args[0]).getString("text"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        MainActivity.addImage(MainActivity.decodeImage(((JSONObject) args[0]).getString("image")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            new GroupMessage().execute();
        }
    };

    private Emitter.Listener heartBeat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            class HeartBeat extends AsyncTask {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        Log.e("HeartBeat", ((JSONObject) args[0]).getString("beat"));
//                        socket.emit("heartbeat", "beating");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            new HeartBeat().execute();
        }
    };

    private Emitter.Listener handleIncomingPrivateMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            class IncomingMessage extends AsyncTask {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        MainActivity.addMessage(((JSONObject) args[0]).getString("text"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        MainActivity.addImage(MainActivity.decodeImage(((JSONObject) args[0]).getString("image")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            new IncomingMessage().execute();
        }
    };

    private Emitter.Listener showConnectedInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            class ShowConnected extends AsyncTask {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        JSONObject data = new JSONObject();
//                        data.put("email", "fenil@gmail.com");
                        data.put("email", "yashesh@gmail.com");
                        data.put("socket_id", ((JSONObject) args[0]).getString("info"));
                        socket.emit("connectedDone", data.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            new ShowConnected().execute();
        }
    };

    public void disconnectSocket() {
        SocketClass.socket.disconnect();
        SocketClass.socket.off();
        SocketClass.socket = null;
    }

    public static void connectSocket(Context context) {
        Socket socket;
        String PROTOCOL = "http://";
        String SERVER_ADDRESS = "192.168.31.32";
        int SERVER_PORT = 3000;
        try {
            IO.Options options = new IO.Options();
            options.reconnection = false;
            options.reconnectionAttempts = 0;
            socket = IO.socket(PROTOCOL + SERVER_ADDRESS + ":" + SERVER_PORT + "/", options);
            socket.connect();
            final SocketClass socketClass = new SocketClass();
            socketClass.SocketInitialize(context, socket);
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socketClass.disconnectSocket();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket() {
        return socket;
    }
}
