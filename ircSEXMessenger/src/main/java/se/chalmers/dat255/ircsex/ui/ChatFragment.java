package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.MessageArrayAdapter;

public class ChatFragment extends Fragment {
    public static final String ARG_CHANNEL_INDEX = "channelIndex";
    private ListView messageList;
    private MessageArrayAdapter messageArrayAdapter;
    private EditText messageEditText;
    private final ChatMessageSendListener messageSendListener;

    public ChatFragment(ChatMessageSendListener messageSendListener) {
        this.messageSendListener = messageSendListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        messageArrayAdapter = new MessageArrayAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        int i = getArguments().getInt(ARG_CHANNEL_INDEX);
        messageList = (ListView) rootView.findViewById(R.id.chat_message_list);
        messageList.setAdapter(messageArrayAdapter);
        messageEditText = (EditText) rootView.findViewById(R.id.fragment_chat_message);
        rootView.findViewById(R.id.fragment_chat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                messageSendListener.userSentMessage(message);
                messageArrayAdapter.add(new SentChatBubble(message));
                messageList.invalidate();
                messageEditText.setText("");
                scrollToBottom();
            }
        });
        return rootView;
    }

    public void addMessage(IrcMessage ircMessage) {
        Log.d("IRC", ircMessage.getMessage());
        messageArrayAdapter.add(new ReceivedChatBubble(ircMessage));
        messageList.invalidate();
        scrollToBottom();
    }

    public void scrollToBottom() {
        messageList.setSelection(messageArrayAdapter.getCount()-1);
    }

    public interface ChatMessageSendListener {
        public void userSentMessage(String string);
    }
}