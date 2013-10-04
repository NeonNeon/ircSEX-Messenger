package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcMessage;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-04
 */
public class SearchFragment extends Fragment {
    private MessageArrayAdapter messageArrayAdapter;
    private List<IrcMessage> messages;
    private ListView messageList;
    private String searchString;

    public SearchFragment() {

    }

    public SearchFragment(List<IrcMessage> messages) {
        this.messages = messages;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setMessageArrayAdapter();
    }

    public void setMessageArrayAdapter() {
        List<ChannelItem> searchResults = new ArrayList<ChannelItem>();
        for (IrcMessage ircMessage : messages) {
            if (ircMessage.getMessage().contains(searchString)) {
                if (ircMessage.getUser().isSelf()) {
                    searchResults.add(new SentChatBubble(ircMessage.getMessage()));
                }
                else {
                    searchResults.add(new ReceivedChatBubble(ircMessage));
                }
            }
        }
        messageArrayAdapter = new MessageArrayAdapter(getActivity(), searchResults);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        messageList = (ListView) rootView.findViewById(R.id.search_messages_list);
        messageList.setAdapter(messageArrayAdapter);
        return rootView;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
