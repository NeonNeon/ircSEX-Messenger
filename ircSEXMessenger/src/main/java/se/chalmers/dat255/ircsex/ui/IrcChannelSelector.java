package se.chalmers.dat255.ircsex.ui;

/**
 * Created by Wilhelm on 2013-09-24.
 */

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.dat255.ircsex.view.IrcChannelItem;
import se.chalmers.dat255.ircsex.view.IrcConnectionItem;
import se.chalmers.dat255.ircsex.view.IrcConnectionItemAdapter;
import se.chalmers.dat255.ircsex.view.IrcServerHeader;

/**
 *
 */
public class IrcChannelSelector {
//    private ArrayAdapter channelListArrayAdapter;
    private List<IrcConnectionItem> connections;
    private List<IrcServerHeader> servers;
    private Map<IrcServerHeader, List<IrcChannelItem>> headersToChannels;

    public IrcChannelSelector(Context context) {
        connections = new ArrayList<IrcConnectionItem>();
        servers = new ArrayList<IrcServerHeader>();
        headersToChannels = new HashMap<IrcServerHeader, List<IrcChannelItem>>();
//        channelListArrayAdapter = new IrcConnectionItemAdapter(context, connections);
    }

    public ArrayAdapter getArrayAdapter() {
        return null;
    }

    public void addHeader(IrcServerHeader header) {
        header.index = connections.size();
        headersToChannels.put(header, new ArrayList<IrcChannelItem>());
        servers.add(header);
        addItem(header);
    }

    public int addChannel(String server, IrcChannelItem channel) {
        int headerIndex = getHeader(server);
        IrcServerHeader serverHeader = servers.get(headerIndex);
        List<IrcChannelItem> serverChildren = headersToChannels.get(serverHeader);
        serverChildren.add(channel);
        int channelIndex = serverChildren.size();
        for (int i = 0; i < headerIndex; i++) {
            channelIndex+=headersToChannels.get(servers.get(i)).size()+1;
        }
        connections.add(channelIndex, channel);
        return channelIndex;
    }

    private int getHeader(String server) {
        for (int i = 0; i < servers.size(); i++) {
            IrcServerHeader ircServerHeader = servers.get(i);
            if (ircServerHeader.getText().equals(server)) {
                return i;
            }
        }
        return -1;
    }

    private void addItem(IrcConnectionItem item) {
        addItem(connections.size(), item);
    }

    private void addItem(int index, IrcConnectionItem item) {
        connections.add(index, item);
        datasetChanged();
    }

    public IrcConnectionItem getItem(int index) {
        return connections.get(index);
    }

    public int removeChannel(int index) {
        IrcConnectionItem item = connections.remove(index);
        List<IrcChannelItem> childList = null;
        for (List<IrcChannelItem> list : headersToChannels.values()) {
            if (list.remove(item)) {
                childList = list;
            }
        }
        datasetChanged();
        int newIndex = index-1;
        if (isIndexHeading(newIndex) && !childList.isEmpty()) {
            newIndex++;
        }
        return newIndex;
    }

    public void datasetChanged() {
//        channelListArrayAdapter.notifyDataSetChanged();
    }

    public void expandHeader() {
//        ((IrcServerHeader) connections)
    }

    public void disconnect(int index) {
        // TODO
    }

    public boolean isIndexHeading(int position) {
        return connections.get(position).getViewType() == IrcConnectionItemAdapter.RowType.HEADER_ITEM.ordinal();
    }
}
