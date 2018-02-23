package com.example.yash.nodesocketmessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yash on 17-02-2018.
 */

public class SocketClass {

    public static Socket socket;
    private static String PROTOCOL = "http://";
    private static String SERVER_ADDRESS = "192.168.31.32";
    private static int SERVER_PORT = 3000;
    private int info = 1;
    private Context context;

    public SocketClass() {
        try {
            IO.Options options = new IO.Options();
//            options.forceNew = true;
            options.reconnection = false;
            socket = IO.socket(PROTOCOL + SERVER_ADDRESS + ":" + SERVER_PORT + "/", options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SocketInitialize(Context context) {
        this.context = context;
        socket.connect();
        socket.on("privateMessageGet", handleIncomingPrivateMessages);
        socket.on("groupMessage", handleIncomingGroupMessages);
        socket.on("connected", showConnectedInfo);
        socket.on("ping", heartBeat);
        socket.on("pushMessage", pushMessageNotification);
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
                        notificationUtils.showMessageNotification(context, 7, jsonObject.getString("message"), jsonObject.getString("doer"));
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
                        if (info == 1) {
                            info = 2;
                            JSONObject data = new JSONObject();
//                            JSONObject object = new JSONObject();
//                            object.put("user_name", "yashesh");
//                            object.put("user_name", "fenil");
//                            object.put("socket_id", ((JSONObject) args[0]).getString("info"));
//                            data.put("yashesh@gmail.com", object);
                            data.put("email", "fenil@gmail.com");
                            data.put("socket_id", ((JSONObject) args[0]).getString("info"));
                            socket.emit("connectedDone", data.toString());
                        }
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
        socket.disconnect();
        socket.off("privateMessageGet", handleIncomingPrivateMessages);
        socket.off("groupMessage", handleIncomingGroupMessages);
        socket.off("connected", showConnectedInfo);
        socket.off("ping", heartBeat);
        socket.off("pushMessage", pushMessageNotification);
    }

    public static Socket getSocket() {
        return socket;
    }
}
