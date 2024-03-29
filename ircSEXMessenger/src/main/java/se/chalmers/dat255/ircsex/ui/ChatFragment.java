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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChannelItem;
import se.chalmers.dat255.ircsex.model.ChatIrcMessage;
import se.chalmers.dat255.ircsex.model.InfoMessage;
import se.chalmers.dat255.ircsex.model.IrcChannel;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;
import se.chalmers.dat255.ircsex.model.ReceivedChatBubble;
import se.chalmers.dat255.ircsex.model.SentChatBubble;

public class ChatFragment extends Fragment implements NetworkStateHandler.ConnectionListener {
    private ListView messageList;
    private MessageArrayAdapter messageArrayAdapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatMessageSendListener messageSendListener;
    private IrcChannel channel;

    public ChatFragment() {
        // Empty constructor for resuming state.
    }

    public ChatFragment(ChatMessageSendListener messageSendListener, IrcChannel channel) {
        this.messageSendListener = messageSendListener;
        this.channel = channel;
        NetworkStateHandler.getInstance().addListener(this);
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
        try {
            for (IrcMessage message : channel.getMessages()) {
                Class<? extends ChannelItem> channelItemClass = message.getChannelItem();
                ChannelItem ci = channelItemClass.getConstructor(IrcMessage.class).newInstance(message);
                backlog.add(ci);
            }
        } catch (InvocationTargetException | NoSuchMethodException | java.lang.InstantiationException | IllegalAccessException e) {
            Log.e("IRCDEBUG", "Could not instantiate backlog of channel " + channel.getChannelName(), e);
        } finally {
            messageArrayAdapter = new MessageArrayAdapter(getActivity(), backlog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        messageList = (ListView) rootView.findViewById(R.id.chat_message_list);
        messageList.setAdapter(messageArrayAdapter);
        scrollToBottom();
        sendButton = (ImageButton) rootView.findViewById(R.id.fragment_chat_send);
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

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if (!message.equals("")) {
            messageSendListener.userSentMessage(message);
        }
    }

    public void addMessage(ChatIrcMessage ircMessage) {
        messageArrayAdapter.add(new ReceivedChatBubble(ircMessage));
        messageList.invalidate();
        scrollWhenNoBacklog();
    }

    public void addSentMessage(ChatIrcMessage ircMessage) {
        messageArrayAdapter.add(new SentChatBubble(ircMessage));
        messageList.invalidate();
        messageEditText.setText("");
        scrollToBottom();
    }

    public void addInfoMessage(IrcMessage infoMessage) {
        Log.d("IRCDEBUG", infoMessage.toString());
        messageArrayAdapter.add(new InfoMessage(infoMessage));
        messageList.invalidate();
        scrollWhenNoBacklog();
    }

    public void scrollWhenNoBacklog() {
        Log.d("IRCDEBUG", "Last visible: " + messageList.getLastVisiblePosition() + " Count: " + messageArrayAdapter.getCount());
        if (hasBacklog()) {
            scrollToBottom();
        }
    }

    private boolean hasBacklog() {
        return messageList.getLastVisiblePosition() == messageArrayAdapter.getCount()-2;
    }

    private void scrollToBottom() {
        if (messageArrayAdapter != null) {
            messageList.setSelection(messageArrayAdapter.getCount()-1);
        }
    }

    @Override
    public void onOnline() {
        sendButton.setEnabled(true);
        messageEditText.setEnabled(true);
    }

    @Override
    public void onOffline() {
        sendButton.setEnabled(false);
        messageEditText.setEnabled(false);
    }

    public interface ChatMessageSendListener {
        public void userSentMessage(String string);
    }
}