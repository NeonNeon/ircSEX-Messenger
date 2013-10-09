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
import se.chalmers.dat255.ircsex.model.IrcHighlight;
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
    public static final String TEXT2 = "text2";
    public static final String TEXT3 = "text3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, null);
        content = new ArrayList<Map<String, String>>();
        List<IrcHighlight> highlights = session.getActiveServer().getHighlights();
        for (IrcHighlight highlight : highlights) {
            Map<String, String> entry = new HashMap<String, String>();
            entry.put(TEXT1, highlight.getChannel().getChannelName());
            entry.put(TEXT2, highlight.getMessage().getReadableTimestamp());
            entry.put(TEXT3, highlight.getMessage().getMessage());
            content.add(entry);
        }

        adapter = new SimpleAdapter(
                this,
                content,
                R.layout.channel_search_list_item,
                new String[]{TEXT1, TEXT2, TEXT3},
                new int[]{android.R.id.text1, R.id.text2, R.id.text3});
        setListAdapter(adapter);
    }
}
