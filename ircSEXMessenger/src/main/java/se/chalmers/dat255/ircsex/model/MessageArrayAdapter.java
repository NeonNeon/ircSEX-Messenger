package se.chalmers.dat255.ircsex.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.ui.ChannelItem;
import se.chalmers.dat255.ircsex.ui.DayChangeMessage;
import se.chalmers.dat255.ircsex.ui.ReceivedChatBubble;
import se.chalmers.dat255.ircsex.ui.SentChatBubble;

/**
 * @author Johan Magnusson
 * Created: 2013-09-24
 *
 *
 */
public class MessageArrayAdapter extends ArrayAdapter<ChannelItem> {
    private Context context;
    private List<ChannelItem> channelItems = new ArrayList<ChannelItem>();
    private RelativeLayout wrapper;
    private boolean animate = true;
    private Time time;
    private int dayOfMonth;

    public MessageArrayAdapter(Context context, List<ChannelItem> backlog) {
        super(context, R.layout.received_chat_bubble);
        for (ChannelItem item : backlog) {
            add(item);
        }
        this.context = context;
        time = new Time();
        time.setToNow();
        dayOfMonth = time.monthDay;
    }

    @Override
    public void add(ChannelItem channelItem) {
        checkForDateChange();
        channelItems.add(channelItem);
        super.add(channelItem);
        animate = true;
    }

    private void checkForDateChange() {
        time.setToNow();
        if (time.monthDay != dayOfMonth) {
            dayOfMonth = time.monthDay;
            DayChangeMessage dayChangeMessage = new DayChangeMessage(time.monthDay + " " + getMonth(time.month));
            channelItems.add(dayChangeMessage);
            super.add(dayChangeMessage);
        }
    }

    private String getMonth(int month) {
        return DateFormatSymbols.getInstance(Locale.getDefault()).getMonths()[month];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelItem channelItem = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(channelItem.getLayoutID(), parent, false);
        wrapper = (RelativeLayout) rowView.findViewById(R.id.channel_item_wrapper);
        TextView messageView = (TextView) rowView.findViewById(R.id.channel_item_message);
        android.view.animation.Animation animation;
        if (channelItem instanceof ReceivedChatBubble) {
            TextView nickView = (TextView) rowView.findViewById(R.id.chat_bubble_nick);
            TextView timestampView = (TextView) rowView.findViewById(R.id.chat_bubble_timestamp);
            nickView.setText(((ReceivedChatBubble) channelItem).getNick());
            timestampView.setText(((ReceivedChatBubble)channelItem).getTimestamp());
            animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        }
        else if (channelItem instanceof SentChatBubble){
            animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        }
        else {
            animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
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
        if (animate) {
            rowView.startAnimation(animation);
            animate = false;
        }
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
//        NinePatch ninePatch = new NinePatch(bitmap, chunk, null);
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(resources, bitmap, chunk, channelItem.getPadding(), null);
        ninePatchDrawable.setColorFilter(channelItem.getColor(), PorterDuff.Mode.MULTIPLY);
        return ninePatchDrawable;
    }
}
