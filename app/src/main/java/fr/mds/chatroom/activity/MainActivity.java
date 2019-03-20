package fr.mds.chatroom.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import fr.mds.chatroom.R;
import fr.mds.chatroom.application.App;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int ACTIVITY_CODE = 0x01;
    public static final String EXTRA_LOGIN = "EXTRA_LOGIN";
    public static final String PREF = "CHAT_ROOM";
    public static final String TOPIC = "chat";

    private EditText et_login_login;
    private Button btn_login_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initComponent();

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        this.btn_login_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainActivity.this.et_login_login.getText().toString().isEmpty()) {
                    String login = MainActivity.this.et_login_login.getText().toString();
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                    MainActivity.this.et_login_login.setText("");

                    SharedPreferences.Editor editor = App.getContext().getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE).edit();
                    editor.putString(MainActivity.EXTRA_LOGIN, login).apply();
                    editor.commit();

                    intent.putExtra(MainActivity.EXTRA_LOGIN, login);

                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Enter valid login", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initComponent() {
        this.et_login_login = findViewById(R.id.et_login_login);
        this.btn_login_go = findViewById(R.id.btn_login_go);
    }
}
