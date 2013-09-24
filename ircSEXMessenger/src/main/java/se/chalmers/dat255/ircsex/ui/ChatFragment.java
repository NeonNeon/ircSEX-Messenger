package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.MessageArrayAdapter;

public class ChatFragment extends Fragment {
    public static final String ARG_CHANNEL_INDEX = "channelIndex";
    private ListView messageList;
    private MessageArrayAdapter messageArrayAdapter;

    public ChatFragment() {
        // Empty constructor required for fragment subclasses

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

        addItems();
        return rootView;
    }

    public void addItems() {
        messageArrayAdapter.add(new SentChatBubble("GO TO BED PLS"));
        messageArrayAdapter.add(new ReceivedChatBubble("Alkohest", "ne :PPPPPPPPPPPPPPPPPPPPPPPP du är hyb asdjh asdjhas dasdas dasd asd asd asd asd s s XD"));
        messageArrayAdapter.add(new SentChatBubble("XDDDDDDDDDDDDDD"));
    }

    public void sendMessage(View view) {
        //String message = ((EditText) findViewById(R.id.activity_channel_main_message)).getText().toString();
        //messageArrayAdapter.add(new SentChatBubble("Me", message));
    }
}