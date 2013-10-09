package se.chalmers.dat255.ircsex.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcChannel;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.Session;


/**
 * Created by Oskar on 2013-10-09.
 */
public class HighlightActivity extends ListActivity {

    private List<Map<String, String>> content;
    private SimpleAdapter adapter;
    private Session session;

    public static final String TEXT1 = "text1";
    public static final String TEXT2 = "text3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, null);
        content = new ArrayList<Map<String, String>>();
        Map<IrcChannel, IrcMessage> highlights = session.getActiveServer().getHighlights();
        for (IrcChannel channel : highlights.keySet()) {
            Map<String, String> entry = new HashMap<String, String>();
            entry.put(TEXT1, channel.getChannelName());
            entry.put(TEXT2, highlights.get(channel).getMessage());
            content.add(entry);
        }

        adapter = new SimpleAdapter(
                this,
                content,
                R.layout.channel_search_list_item,
                new String[]{TEXT1, TEXT2},
                new int[]{android.R.id.text1, R.id.text2});
        setListAdapter(adapter);
    }
}
