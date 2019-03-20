package fr.mds.chatroom.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import fr.mds.chatroom.R;
import fr.mds.chatroom.adapter.MessageAdapter;
import fr.mds.chatroom.application.App;
import fr.mds.chatroom.entity.Message;
import fr.mds.chatroom.service.ChatFirebasePushService;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "ChatActivity";
    public static final int ACTIVITY_CODE = 0x02;

    private ListView lv_chat_messages;
    private EditText et_chat_message;
    private Button btn_chat_send;

    private ArrayList<Message> messages;
    private MessageAdapter adapter;
    private BroadcastReceiver receiver;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver((receiver),
                new IntentFilter(ChatFirebasePushService.REQUEST_ACCEPT)
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(ChatActivity.this).unregisterReceiver(receiver);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.initComponent();

        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        lv_chat_messages.setAdapter(adapter);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String login = "";
                    String message = "";

                    if (!intent.getStringExtra(ChatFirebasePushService.EXTRA_LOGIN).isEmpty()) {
                        login = intent.getStringExtra(ChatFirebasePushService.EXTRA_LOGIN);
                    }

                    if (!intent.getStringExtra(ChatFirebasePushService.EXTRA_MESSAGE).isEmpty()) {
                        message = intent.getStringExtra(ChatFirebasePushService.EXTRA_MESSAGE);
                    }

                    ChatActivity.this.messages.add(new Message(login, message));
                    ChatActivity.this.adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        this.btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                final String message = ChatActivity.this.et_chat_message.getText().toString();

                if (!message.isEmpty()) {
                    SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
                    String login = sharedPreferences.getString(MainActivity.EXTRA_LOGIN, "");

                    Message sendMessage = new Message(login, message);
                    ChatActivity.this.et_chat_message.setText("");
                    ChatActivity.this.messages.add(sendMessage);
                    ChatActivity.this.adapter.notifyDataSetChanged();


                    new AsyncTask<Message, Void, Void>() {
                        @SuppressLint("WrongThread")
                        @Override
                        protected Void doInBackground(Message... messages) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                                    "{\n" +
                                            " \"to\": \"/topics/chat\",\n" +
                                            " \"data\": {\n" +
                                            "\t \"user\": \"" + messages[0].getLogin() + "\",\n" +
                                            " },\n" +
                                            "\t\"notification\":{\n" +
                                            "\t\t\"body\":\"" + messages[0].getMessage() + "\"\n" +
                                            "\t}\n" +
                                            "}"
                            );

//                            JSONObject jobject = new JSONObject();
//
//                            jobject.accumulate("data", )

                            Request request = new Request.Builder()
                                    .url("https://fcm.googleapis.com/fcm/send")
                                    .addHeader("Authorization", "key= AAAAwDk-sl0:APA91bFgaYBIm1E9cK7snWPiIGiiLxgbWYnqA-PDOOHJBRZg5Pog0QDmzer3-9YTN9OzLh1K7xHkI7QzNYKEKzpeb1a_gCvrLXqVAapP1QTci8yIb4u_jUdap-6bmXbwGdmgb7sXvn6U")
                                    .post(requestBody)
                                    .build();

                            try (Response response = client.newCall(request).execute()) {
                                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                                Headers responseHeaders = response.headers();
                                for (int i = 0; i < responseHeaders.size(); i++) {
                                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }

                                Log.d(TAG, response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                    }.execute(sendMessage);


                }
            }
        });
    }

    private void initComponent() {
        this.lv_chat_messages = findViewById(R.id.lv_chat_messages);
        this.et_chat_message = findViewById(R.id.et_chat_message);
        this.btn_chat_send = findViewById(R.id.btn_chat_send);
    }
}
