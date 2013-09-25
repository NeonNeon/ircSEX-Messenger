package se.chalmers.dat255.ircsex.ui;

/**
 * Created by Wilhelm on 2013-09-24.
 */

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.view.IrcChannelItem;
import se.chalmers.dat255.ircsex.view.IrcConnectionItem;
import se.chalmers.dat255.ircsex.view.IrcConnectionItemAdapter;
import se.chalmers.dat255.ircsex.view.IrcServerHeader;

/**
 *
 */
public class IrcChannelSelector {
    private ArrayAdapter channelListArrayAdapter;
    private List<IrcConnectionItem> connectedChannels;
    private Set<Integer> headingIndices;

    public IrcChannelSelector(Context context) {
        connectedChannels = new ArrayList<IrcConnectionItem>();
        headingIndices = new HashSet<Integer>();
        channelListArrayAdapter = new IrcConnectionItemAdapter(context, connectedChannels);
    }

    public ArrayAdapter getArrayAdapter() {
        return channelListArrayAdapter;
    }

    public void addHeader(IrcServerHeader header) {
        addItem(header);
    }

    public int addChannel(String server, IrcChannelItem channel) {
        addItem(channel);
        return 1; // TODO: returnera vilket index kanalen hamnade på
    }

    private void addItem(IrcConnectionItem item) {
        connectedChannels.add(item);
        datasetChanged();
    }

    public IrcConnectionItem getItem(int index) {
        return connectedChannels.get(index);
    }

    public int removeItem(int selected) {
        connectedChannels.remove(selected);
        datasetChanged();
        // TODO: Gör andra saker om det är en server
        return connectedChannels.size()-1;
    }

    public void datasetChanged() {
        channelListArrayAdapter.notifyDataSetChanged();
    }

    public void expandHeader() {
//        ((IrcServerHeader) connectedChannels)
    }

    public boolean isIndexHeading(int newPosition) {
        return false; // TODO
    }

    public void disconnect() {
        // TODO
    }
}
