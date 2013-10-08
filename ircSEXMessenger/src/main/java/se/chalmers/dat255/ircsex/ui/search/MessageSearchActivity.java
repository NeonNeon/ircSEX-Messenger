package se.chalmers.dat255.ircsex.ui.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.ui.ChannelItem;
import se.chalmers.dat255.ircsex.ui.MessageArrayAdapter;
import se.chalmers.dat255.ircsex.ui.ReceivedChatBubble;
import se.chalmers.dat255.ircsex.ui.SentChatBubble;

/**
 * Created by Johan on 2013-10-07.
 */
public class MessageSearchActivity extends SearchActivity {
    private ArrayList<ChannelItem> result;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = Session.getInstance(this, null);
        handleIntent(getIntent());
        getListView().setDivider(null);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    @Override
    public BaseAdapter getAdapter() {
        return new MessageArrayAdapter(this, result);
    }

    @Override
    public void clearAdapter() {
        result = new ArrayList<ChannelItem>();
        super.clearAdapter();
    }

    @Override
    public void search(String search) {
        List<IrcMessage> messages = session.getActiveChannel().getMessages();
        search = search.toLowerCase();
        clearAdapter();
        for (IrcMessage message : messages) {
            if (message.getMessage().toLowerCase().contains(search)) {
                if (message.getUser().isSelf()) {
                    result.add(new SentChatBubble(message.getMessage()));
                }
                else {
                    result.add(new ReceivedChatBubble(message));
                }
            }
        }
        super.clearAdapter();
        update();
    }
}
