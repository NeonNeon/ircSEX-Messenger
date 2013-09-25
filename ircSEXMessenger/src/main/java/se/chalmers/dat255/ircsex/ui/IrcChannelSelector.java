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
    private ArrayAdapter channelListArrayAdapter;
    private List<IrcConnectionItem> connectedChannels;
    private List<Header> headers;

    public IrcChannelSelector(Context context) {
        connectedChannels = new ArrayList<IrcConnectionItem>();
        headers = new ArrayList<Header>();
        channelListArrayAdapter = new IrcConnectionItemAdapter(context, connectedChannels);
    }

    public ArrayAdapter getArrayAdapter() {
        return channelListArrayAdapter;
    }

    public void addHeader(IrcServerHeader header) {
        headers.add(new Header(header.getText(), headers.size()));
        addItem(header);
    }

    public int addChannel(String server, IrcChannelItem channel) {
        Header header = getHeader(server);
        int channelIndex = header.getNextChannelIndex();
        header.addChild(channel.getText());
        addItem(channelIndex, channel);
        for (int i = headers.indexOf(header)+1; i < headers.size()-1; i++) {
            headers.get(i).index++;
        }
        return channelIndex;
    }

    private void addItem(IrcConnectionItem item) {
        addItem(connectedChannels.size(), item);
    }

    private void addItem(int index, IrcConnectionItem item) {
        connectedChannels.add(index, item);
        datasetChanged();
    }

    public IrcConnectionItem getItem(int index) {
        return connectedChannels.get(index);
    }

    public int removeItem(int selected) {
        connectedChannels.remove(selected);
        datasetChanged();
        // TODO: Ta bort ifrån headern
        // TODO: Gör andra saker om det är en server
        return connectedChannels.size()-1;
    }

    public void datasetChanged() {
        channelListArrayAdapter.notifyDataSetChanged();
    }

    public void expandHeader() {
//        ((IrcServerHeader) connectedChannels)
    }

    public void disconnect(int index) {
        // TODO
    }

    public boolean isIndexHeading(int position) {
        for (Header header : headers) {
            if (header.index == position) return true;
        }
        return false;
    }

    private Header getHeader(String name) {
        for (Header header : headers) {
            if (header.name == name) {
                return header;
            }
        }
        return null;
    }

    private class Header {
        private final String name;
        private List<String> children;
        private int index;

        public Header(String name, int index) {
            this.index = index;
            this.name = name;
            this.children = new ArrayList<String>();
        }

        private int getNextChannelIndex() {
            return index + children.size() + 1;
        }

        private void addChild(String child) {
            children.add(child);
        }

        private void removeChild(String child) {
            children.remove(child);
        }

        private void removeChild(int index) {
            children.remove(index);
        }
    }
}
