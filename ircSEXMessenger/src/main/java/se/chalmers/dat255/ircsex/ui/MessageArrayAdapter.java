package se.chalmers.dat255.ircsex.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
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
import se.chalmers.dat255.ircsex.model.ChannelItem;
import se.chalmers.dat255.ircsex.model.ChatBubble;
import se.chalmers.dat255.ircsex.model.InfoMessage;
import se.chalmers.dat255.ircsex.model.ReceivedChatBubble;
import se.chalmers.dat255.ircsex.model.SentChatBubble;
import se.chalmers.dat255.ircsex.ui.SettingsActivity;
import se.chalmers.dat255.ircsex.ui.UnreadLine;

/**
 * @author Johan Magnusson
 * Created: 2013-09-24
 */
public class MessageArrayAdapter extends ArrayAdapter<ChannelItem> {
    private Context context;
    private List<ChannelItem> channelItems = new ArrayList<ChannelItem>();
    private RelativeLayout wrapper;
    private boolean animate = true;
    private SharedPreferences sharedPreferences;

    public MessageArrayAdapter(Context context, List<ChannelItem> backlog) {
        super(context, R.layout.received_chat_bubble);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean unreadLinePlaced = false;
        for (ChannelItem item : backlog) {
            if (!(item instanceof SentChatBubble) && !unreadLinePlaced && !item.getIrcMessage().isRead()) {
                add(new UnreadLine());
                unreadLinePlaced = true;
            }
            add(item);
            animate = false;
        }
        this.context = context;
    }

    @Override
    public void add(ChannelItem channelItem) {
        if (!(channelItem instanceof InfoMessage) || sharedPreferences.getBoolean(SettingsActivity.PREF_SHOW_JOIN_LEAVE, true)) {
            channelItems.add(channelItem);
            super.add(channelItem);
            animate = true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelItem channelItem = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(channelItem.getLayoutID(), parent, false);
        if (channelItem instanceof UnreadLine) {
            return rowView;
        }
        wrapper = (RelativeLayout) rowView.findViewById(R.id.channel_item_wrapper);
        TextView messageView = (TextView) rowView.findViewById(R.id.channel_item_message);
        messageView.setTextSize(Float.parseFloat(sharedPreferences.getString(SettingsActivity.PREF_MESSAGE_FONT_SIZE, "14")));
        android.view.animation.Animation animation;
        if (channelItem instanceof ReceivedChatBubble) {
            TextView nickView = (TextView) rowView.findViewById(R.id.chat_bubble_nick);
            nickView.setText(((ReceivedChatBubble) channelItem).getNick());
            animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        }
        else if (channelItem instanceof SentChatBubble){
            animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        }
        else {
            animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        }
        if (channelItem instanceof ChatBubble) {
            TextView timestampView = (TextView) rowView.findViewById(R.id.chat_bubble_timestamp);
            timestampView.setText(((ChatBubble)channelItem).getTimestamp());
        }
        messageView.setText(channelItem.getMessage());
        wrapper.setGravity(channelItem.getGravity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = channelItem.getGravity();
        wrapper.setLayoutParams(params);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            wrapper.setBackgroundDrawable(createNinePatchDrawable(channelItem));
        }
        else {
            wrapper.setBackground(createNinePatchDrawable(channelItem));
        }
        if (animate && !channelItem.getIrcMessage().isRead()) {
            rowView.startAnimation(animation);
            animate = false;
        }
        channelItem.getIrcMessage().read();
        return rowView;
    }

    @Override
    public ChannelItem getItem(int index) {
        return channelItems.get(index);
    }

    private NinePatchDrawable createNinePatchDrawable(ChannelItem channelItem) {
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, channelItem.getNinePatchID());
        byte[] chunk = bitmap.getNinePatchChunk();
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(resources, bitmap, chunk, channelItem.getPadding(), null);
        ninePatchDrawable.setColorFilter(channelItem.getColor(), PorterDuff.Mode.MULTIPLY);
        return ninePatchDrawable;
    }
}
