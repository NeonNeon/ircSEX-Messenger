package se.chalmers.dat255.ircsex.ui.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.ui.ChannelItem;
import se.chalmers.dat255.ircsex.ui.MessageArrayAdapter;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-04
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
        try {
            for (IrcMessage message : messages) {
                if (message.getMessage().toLowerCase().contains(search)) {
                    Class<? extends ChannelItem> channelItemClass = message.getChannelItem();
                    ChannelItem ci = channelItemClass.getConstructor(IrcMessage.class).newInstance(message);
                    result.add(ci);
                }
            }
        } catch (java.lang.InstantiationException  | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.e("IRCDEBUG", "Could not instantiate search results for search string: " + search, e);
        } finally {
            super.clearAdapter();
            update();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //TODO: scroll the chat fragment list to the clicked view
        finish();
    }
}
