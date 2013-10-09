package se.chalmers.dat255.ircsex.ui.search;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
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
        channels = new ArrayList<SearchlistChannelItem>();
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
}
