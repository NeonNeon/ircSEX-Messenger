package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcChannel;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.MessageArrayAdapter;

public class ChatFragment extends Fragment {
    public static final String ARG_CHANNEL_INDEX = "channelIndex";
    private ListView messageList;
    private MessageArrayAdapter messageArrayAdapter;
    private EditText messageEditText;
    private ChatMessageSendListener messageSendListener;
    private IrcChannel channel;

    public ChatFragment() {
        Log.e("IRCDEBUG", "Constructor: " + toString());
    }

    public ChatFragment(ChatMessageSendListener messageSendListener, IrcChannel channel) {
        Log.e("IRCDEBUG", "Constructor params: " +  toString());
        this.messageSendListener = messageSendListener;
        this.channel = channel;
    }

    public void bringUpToSpeed(ChatMessageSendListener messageSendListener, IrcChannel channel) {
        this.messageSendListener = messageSendListener;
        this.channel = channel;
        setArrayAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (channel != null) {
            setArrayAdapter();
        }
    }

    private void setArrayAdapter() {
        List<ChannelItem> backlog = new ArrayList<ChannelItem>(channel.getMessages().size());
        for (IrcMessage message : channel.getMessages()) {
            if (message.getUser().isSelf()) {
                backlog.add(new SentChatBubble(message.getMessage()));
            } else {
                backlog.add(new ReceivedChatBubble(message));
            }
        }
        messageArrayAdapter = new MessageArrayAdapter(getActivity(), backlog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        int i = getArguments().getInt(ARG_CHANNEL_INDEX);
        messageList = (ListView) rootView.findViewById(R.id.chat_message_list);
        messageList.setAdapter(messageArrayAdapter);
        scrollToBottom();
        messageEditText = (EditText) rootView.findViewById(R.id.fragment_chat_message);
        messageEditText.requestFocus();
        ((EditText) rootView.findViewById(R.id.fragment_chat_message)).setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            sendMessage();
                            return true;
                        }
                        return false;
                    }
                });
        rootView.findViewById(R.id.fragment_chat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        return rootView;
    }

    public void displaySearchResult(String query) {
        List<ChannelItem> searchResults = new ArrayList<ChannelItem>();
        Log.e("IRCDEBUG", query);
        for (IrcMessage ircMessage : channel.getMessages()) {
            if (ircMessage.getMessage().contains(query)) {
                if (ircMessage.getUser().isSelf()) {
                    searchResults.add(new SentChatBubble(ircMessage.getMessage()));
                } else {
                    searchResults.add(new ReceivedChatBubble(ircMessage));
                }
            }
        }
        messageArrayAdapter = new MessageArrayAdapter(getActivity(), searchResults);
        messageList.setAdapter(messageArrayAdapter);
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if (!message.equals("")) {
            messageSendListener.userSentMessage(message);
        }
    }

    public void addMessage(IrcMessage ircMessage) {
        Log.d("IRCDEBUG", ircMessage.getMessage());
        messageArrayAdapter.add(new ReceivedChatBubble(ircMessage));
        messageList.invalidate();
        scrollWhenNoBacklog();
    }

    public void addSentMessage(IrcMessage ircMessage) {
        messageArrayAdapter.add(new SentChatBubble(ircMessage.getMessage()));
        messageList.invalidate();
        messageEditText.setText("");
        scrollToBottom();
    }

    public void addInfoMessage(String infoMessage) {
        Log.d("IRCDEBUG", infoMessage);
        messageArrayAdapter.add(new InfoMessage(infoMessage));
        messageList.invalidate();
        scrollWhenNoBacklog();
    }

    public void scrollWhenNoBacklog() {
        Log.e("IRCDEBUG", "Last visible: " + messageList.getLastVisiblePosition() + " Count: " + messageArrayAdapter.getCount());
        if (messageList.getLastVisiblePosition() == messageArrayAdapter.getCount()-2) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        messageList.setSelection(messageArrayAdapter.getCount()-1);
    }

    public interface ChatMessageSendListener {
        public void userSentMessage(String string);
    }
}