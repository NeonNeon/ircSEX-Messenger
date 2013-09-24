package se.chalmers.dat255.ircsex.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.ui.ChatBubble;

/**
 * Created by Johan on 2013-09-24.
 */
public class MessageArrayAdapter extends ArrayAdapter<ChatBubble> {
    private Context context;
    private List<ChatBubble> chatBubbles = new ArrayList<ChatBubble>();
    private LinearLayout wrapper;

    public MessageArrayAdapter(Context context) {
        super(context, R.layout.chat_bubble);
        this.context = context;
    }

    @Override
    public void add(ChatBubble chatBubble) {
        chatBubbles.add(chatBubble);
        super.add(chatBubble);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.chat_bubble, parent, false);
        }
        wrapper = (LinearLayout) rowView.findViewById(R.id.chat_bubble_wrapper);
        ChatBubble chatBubble = getItem(position);
        TextView nickView = (TextView) rowView.findViewById(R.id.chat_bubble_nick);
        TextView messageView = (TextView) rowView.findViewById(R.id.chat_bubble_message);
        nickView.setText(chatBubble.getNick());
        messageView.setText(chatBubble.getMessage());
        wrapper.setBackgroundColor(chatBubble.getBackgroundColor());
        wrapper.setGravity(chatBubble.getGravity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = chatBubble.getGravity();
        wrapper.setLayoutParams(params);
        return rowView;
    }

    public ChatBubble getItem(int index) {
        return chatBubbles.get(index);
    }
}
