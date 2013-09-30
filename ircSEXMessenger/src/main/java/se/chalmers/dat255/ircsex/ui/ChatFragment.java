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
    private final ChatMessageSendListener messageSendListener;
    private final IrcChannel channel;

    public ChatFragment(ChatMessageSendListener messageSendListener, IrcChannel channel) {
        this.messageSendListener = messageSendListener;
        this.channel = channel;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        List<ChatBubble> backlog = new ArrayList<ChatBubble>(channel.getMessages().size());
        for (IrcMessage message : channel.getMessages()) {
            backlog.add(new ReceivedChatBubble(message));
        }
        messageArrayAdapter = new MessageArrayAdapter(getActivity(), backlog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        int i = getArguments().getInt(ARG_CHANNEL_INDEX);
        messageList = (ListView) rootView.findViewById(R.id.chat_message_list);
        messageList.setAdapter(messageArrayAdapter);
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
            messageArrayAdapter.add(new SentChatBubble(message));
            messageList.invalidate();
            messageEditText.setText("");
            scrollToBottom();
        }
    }

    public void addMessage(IrcMessage ircMessage) {
        Log.d("IRCDEBUG", ircMessage.getMessage());
        messageArrayAdapter.add(new ReceivedChatBubble(ircMessage));
        messageList.invalidate();
        scrollToBottom();
    }

    public void scrollToBottom() {
        Log.e("IRCDEBUG", "Last visible: " + messageList.getLastVisiblePosition() + " Count: " + messageArrayAdapter.getCount());
        if (messageList.getLastVisiblePosition() == messageArrayAdapter.getCount()-2) {
            messageList.setSelection(messageArrayAdapter.getCount()-1);
        }
    }

    public interface ChatMessageSendListener {
        public void userSentMessage(String string);
    }
}