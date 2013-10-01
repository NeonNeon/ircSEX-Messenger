package se.chalmers.dat255.ircsex.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.ui.ChatBubble;
import se.chalmers.dat255.ircsex.ui.ReceivedChatBubble;

/**
 * @author Johan Magnusson
 * Created: 2013-09-24
 *
 *
 */
public class MessageArrayAdapter extends ArrayAdapter<ChatBubble> {
    private Context context;
    private List<ChatBubble> chatBubbles = new ArrayList<ChatBubble>();
    private RelativeLayout wrapper;
    private boolean animate = true;

    public MessageArrayAdapter(Context context, List<ChatBubble> backlog) {
        super(context, R.layout.received_chat_bubble);
        for (ChatBubble bubble : backlog) {
            add(bubble);
        }
        this.context = context;
    }

    @Override
    public void add(ChatBubble chatBubble) {
        chatBubbles.add(chatBubble);
        super.add(chatBubble);
        animate = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatBubble chatBubble = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(chatBubble.getLayoutID(), parent, false);
        wrapper = (RelativeLayout) rowView.findViewById(R.id.chat_bubble_wrapper);
        TextView messageView = (TextView) rowView.findViewById(R.id.chat_bubble_message);
        android.view.animation.Animation animation;
        if (chatBubble instanceof ReceivedChatBubble) {
            TextView nickView = (TextView) rowView.findViewById(R.id.chat_bubble_nick);
            TextView timestampView = (TextView) rowView.findViewById(R.id.chat_bubble_timestamp);
            nickView.setText(((ReceivedChatBubble) chatBubble).getNick());
            timestampView.setText(chatBubble.getTimestamp());
            animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        }
        else {
            animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        }
        messageView.setText(chatBubble.getMessage());
        wrapper.setGravity(chatBubble.getGravity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = chatBubble.getGravity();
        wrapper.setLayoutParams(params);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            wrapper.setBackgroundDrawable(createNinePatchDrawable(chatBubble));
        }
        else {
            wrapper.setBackground(createNinePatchDrawable(chatBubble));
        }
        if (animate) {
            rowView.startAnimation(animation);
            animate = false;
        }
        return rowView;
    }

    @Override
    public ChatBubble getItem(int index) {
        return chatBubbles.get(index);
    }

    private NinePatchDrawable createNinePatchDrawable(ChatBubble chatBubble) {
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, chatBubble.getNinePatchID());
        byte[] chunk = bitmap.getNinePatchChunk();
//        NinePatch ninePatch = new NinePatch(bitmap, chunk, null);
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(resources, bitmap, chunk, chatBubble.getPadding(), null);
        ninePatchDrawable.setColorFilter(chatBubble.getColor(), PorterDuff.Mode.MULTIPLY);
        return ninePatchDrawable;
    }
}
