package fr.mds.chatroom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.mds.chatroom.R;
import fr.mds.chatroom.activity.MainActivity;
import fr.mds.chatroom.application.App;
import fr.mds.chatroom.entity.Message;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, ArrayList<Message> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        TextView tv_message_message = convertView.findViewById(R.id.tv_message_message);
        TextView tv_message_login = convertView.findViewById(R.id.tv_message_login);
        LinearLayout cl_message = convertView.findViewById(R.id.cl_message);
        LinearLayout ll_message_parent = convertView.findViewById(R.id.ll_message_parent);

        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
        String login = sharedPreferences.getString(MainActivity.EXTRA_LOGIN, "");

        if (!login.isEmpty() && login.contentEquals(message.getLogin())) {
            cl_message.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.chat_bubble));
            ll_message_parent.setGravity(Gravity.LEFT);
        } else {
            cl_message.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.chat_bubble_green));
            ll_message_parent.setGravity(Gravity.RIGHT);
        }

        tv_message_login.setText(message.getLogin());
        tv_message_message.setText(message.getMessage());

        return convertView;
    }
}
