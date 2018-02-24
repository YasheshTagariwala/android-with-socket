package com.example.yash.nodesocketmessaging;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mInputMessageView;
    private static RecyclerView mMessagesView;
    private static List<Message> mMessages = new ArrayList<>();
    private static RecyclerView.Adapter mAdapter;
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkScheduler();
        startUp();
    }

    private void startUp() {
        mainActivity = (MainActivity) this;
        mMessagesView = findViewById(R.id.message_recycle);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter(mMessages);
        mMessagesView.setAdapter(mAdapter);
        ImageButton sendButton = findViewById(R.id.message_send);
        mInputMessageView = findViewById(R.id.message_content);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message);
        try {
            JSONObject data = new JSONObject();
//            data.put("to", "fenil@gmail.com");
//            data.put("from", "hemin@gmail.com");
            data.put("to", "fenil@gmail.com");
            data.put("from", "yashesh@gmail.com");
//            data.put("to", "yashesh@gmail.com");
//            data.put("from", "fenil@gmail.com");
            data.put("message", message);
            SocketClass.getSocket().emit("privateMessageEmit", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(String path) {
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("image", encodeImage(path));
            Bitmap bmp = decodeImage(sendData.getString("image"));
            addImage(bmp);
            SocketClass.getSocket().emit("privateMessage", sendData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addMessage(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).message(message).build());
        mAdapter = new MessageAdapter(mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }


    public static void addImage(Bitmap bmp) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE).image(bmp).build());
        mAdapter = new MessageAdapter(mMessages);
        mAdapter.notifyItemInserted(0);
        scrollToBottom();
    }

    private static void scrollToBottom() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkScheduler() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JobSchedulerUtils.JOB_ID) {
                hasBeenScheduled = true;
                break;
            }
        }

        if (!hasBeenScheduled) {
            JobSchedulerUtils.scheduleJob(getApplicationContext());
        }
    }
}
