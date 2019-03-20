package fr.mds.chatroom.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static java.nio.file.Paths.get;

public class ChatFirebasePushService extends FirebaseMessagingService {
    private static final String TAG = "ChatFirebasePushService";
    public static final String REQUEST_ACCEPT = "";

    private static final String DATA_LOGIN = "user";
    private static final String DATA_MESSAGE = "message";

    public static final String EXTRA_LOGIN = "EXTRA_LOGIN";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken" + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived " + remoteMessage.toString());
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        Intent intent = new Intent(REQUEST_ACCEPT);

        String login = "";
        String message = "";

        if (remoteMessage.getData().size() > 0) {
            login = remoteMessage.getData().get(DATA_LOGIN);
            message = remoteMessage.getNotification().getBody();
        }

        intent.putExtra(EXTRA_LOGIN, login);
        intent.putExtra(EXTRA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }


}
