package com.example.yash.nodesocketmessaging;

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
    private static String PROTOCOL = "http://";
    private static String SERVER_ADDRESS = "192.168.31.193";
    private static int SERVER_PORT = 3000;

    public SocketClass() {
        try {
            socket = IO.socket(PROTOCOL + SERVER_ADDRESS + ":" + SERVER_PORT + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SocketInitialize() {
        socket.connect();
        socket.on("privateMessage", handleIncomingPrivateMessages);
        socket.on("groupMessage", handleIncomingGroupMessages);
        socket.on("connected", showConnectedInfo);
        socket.on("ping", heartBeat);
    }

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
                        data.put("email", "yashesh@gamil.com");
                        data.put("user_name", "yashesh");
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
        socket.disconnect();
    }

    public static Socket getSocket() {
        return socket;
    }
}
