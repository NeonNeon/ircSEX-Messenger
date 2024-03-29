package se.chalmers.dat255.ircsex.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;
import se.chalmers.dat255.ircsex.model.SearchlistChannelItem;
import se.chalmers.dat255.ircsex.model.Session;

/**
 * Created by Oskar on 2013-10-07.
 */
public class ChannelSearchActivity extends SearchActivity {

    private ArrayList<HashMap<String,String>> result;
    private List<SearchlistChannelItem> channels;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, null);
        session.getActiveServer().listChannels();
        channels = Collections.synchronizedList(new ArrayList<SearchlistChannelItem>());

        search("");
    }

    @Override
    public BaseAdapter getAdapter() {
        return new SimpleAdapter(
                this,
                result,
                R.layout.channel_search_list_item,
                new String[]{TEXT1, TEXT2, TEXT3},
                new int[]{android.R.id.text1, R.id.text2, R.id.text3});
    }

    @Override
    public void clearAdapter() {
        result = new ArrayList<HashMap<String,String>>();
        super.clearAdapter();
    }

    @Override
    public void search(String search) {
        channels = session.getActiveServer().getChannels();

        search = search.toLowerCase();

        clearAdapter();
        for (SearchlistChannelItem entry : channels) {
            if (entry.getName().toLowerCase().contains(search) // TODO: Improve efficiency
                    || entry.getTopic().toLowerCase().contains(search)) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(TEXT1, entry.getName());
                item.put(TEXT2, entry.getUsers() + " users");
                item.put(TEXT3, entry.getTopic());
                result.add(item);
            }
        }
        update();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (NetworkStateHandler.getInstance().isConnected()) {
            super.onListItemClick(l, v, position, id);
            String text = ((TextView) v.findViewById(android.R.id.text1)).getText().toString();
            Intent data = new Intent();
            data.putExtra(EXTRA_CHANNEL, text);
            setResult(RESULT_RETURN_CHANNEL, data);
            finish();
        }
    }
}
