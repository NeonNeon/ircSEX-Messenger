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

    /**
     * Returns the ArrayAdapter maintained by this class.
     * @return ArrayAdapter for use in left drawer.
     */
    public ArrayAdapter getArrayAdapter() {
        return channelListArrayAdapter;
    }

    /**
     * Add a new header (server) to the adapter and underlying data structures.
     * It will be shown in the list directly.
     * @param header Header with valid host.
     */
    public void addHeader(IrcServerHeader header) {
        headersToChannels.put(header, new ArrayList<IrcChannelItem>());
        servers.add(header);
        addItem(header);
    }

    /**
     * Add a new IrcChannelItem (Channel or Query) to the adapter and underlying data structures.
     * It will be shown in the list directly.
     * Returns on which index the channel has been placed.
     * @param host The host on which the operation was performed.
     * @param channel The item representing the new channel.
     * @return Index on which the new item has been placed.
     */
    public int addChannel(String host, IrcChannelItem channel) {
        int headerIndex = getHeader(host);
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

    /**
     * Gets the connection item at the specified index of the adapter's underlying list.
     * @param index Index of item.
     * @return Item at index.
     */
    public IrcConnectionItem getItem(int index) {
        return connections.get(index);
    }

    /**
     * Return the index of the channel with matching string.
     *
     * @param channel Name of channel
     * @return Index of channel
     */
    public int indexOf(String channel) {
        for (IrcConnectionItem item : connections) {
            if (item.getText().equals(channel)) {
                return connections.indexOf(item);
            }
        }
        return -1;
    }


    /**
     * Removes the channel from the view and underlying datastructure.
     * The list view is updated immediately.
     * Returns the new index the "caret" should be placed at.
     *  - One index down if the removed channel was not the first child of the server and the server still has children.
     *  - One index up if the removed channel was the first child of the server.
     * Thus, if the removed channel was the last child of the server, the server's index will be returned.
     * @param index Index of channel item to be removed.
     * @return New index to place the caret at.
     */
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


    /**
     * Removes the server and all its children from the view and underlying datastructure.
     * The list view is updated immediately.
     * Returns the new index the "caret" should be placed at, or -1 if this was the last server.
     * @param index Index of server to be removed.
     * @return New index to place the caret at.
     */
    public int removeServer(int index) {
        IrcConnectionItem removedServer = connections.remove(index);
        int indexOfHeading = servers.indexOf(removedServer);
        servers.remove(indexOfHeading);
        List<IrcChannelItem> children = headersToChannels.remove(removedServer);
        connections.removeAll(children);
        datasetChanged();
        return servers.isEmpty() ? NO_SERVERS_CONNECTED : indexOfHeading >= servers.size() ? indexOfHeading-1 : indexOfHeading;
    }


    /**
     * Called to notify the adapter the dataset has been changed,
     * invalidating the list view.
     */
    public void datasetChanged() {
        channelListArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Toggles menu expansion of header at index.
     * @param index Index of header to expand.
     */
    public void expandHeader(int index) {
        servers.get(0).expand();
    }

    /**
     * Sets the header to show as disconnected.
     * @param index Index of header to disconnect.
     */
    public void disconnect(int index) {
        servers.get(0).disconnect();
    }

    /**
     * Returns whether the given index in the list view is a header or not.
     * @param position Index to check.
     * @return True if the item at the index is a RowType.HEADER_ITEM, false otherwise.
     */
    public boolean isIndexHeading(int position) {
        return connections.get(position).getViewType() == IrcConnectionItemAdapter.RowType.HEADER_ITEM.ordinal();
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }
}
