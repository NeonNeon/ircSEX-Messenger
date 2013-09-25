package se.chalmers.dat255.ircsex.ui;

/**
 * Created by Wilhelm on 2013-09-24.
 */

import android.content.Context;
import android.util.Log;
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
    public static final int NO_SERVERS_CONNECTED = -1;
    private ArrayAdapter channelListArrayAdapter;
    private List<IrcConnectionItem> connections;
    private List<IrcServerHeader> servers;
    private Map<IrcServerHeader, List<IrcChannelItem>> headersToChannels;

    public IrcChannelSelector(Context context) {
        connections = new ArrayList<IrcConnectionItem>();
        servers = new ArrayList<IrcServerHeader>();
        headersToChannels = new HashMap<IrcServerHeader, List<IrcChannelItem>>();
        channelListArrayAdapter = new IrcConnectionItemAdapter(context, connections);
    }

    public ArrayAdapter getArrayAdapter() {
        return channelListArrayAdapter;
    }

    public void addHeader(IrcServerHeader header) {
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
        datasetChanged();
        return newIndex;
    }

    public int removeServer(int index) {
        IrcConnectionItem removedServer = connections.remove(index);
        int indexOfHeading = servers.indexOf(removedServer);
        servers.remove(indexOfHeading);
        List<IrcChannelItem> children = headersToChannels.remove(removedServer);
        connections.removeAll(children);
        datasetChanged();
        return servers.isEmpty() ? NO_SERVERS_CONNECTED : indexOfHeading >= servers.size() ? indexOfHeading-1 : indexOfHeading;
    }

    public void datasetChanged() {
        channelListArrayAdapter.notifyDataSetChanged();
    }

    public void expandHeader(int index) {
        servers.get(0).expand();
    }

    public void disconnect(int index) {
        servers.get(0).disconnect();
    }

    public boolean isIndexHeading(int position) {
        return connections.get(position).getViewType() == IrcConnectionItemAdapter.RowType.HEADER_ITEM.ordinal();
    }
}
