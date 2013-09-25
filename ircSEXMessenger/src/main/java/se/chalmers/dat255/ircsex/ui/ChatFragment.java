package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        messageArrayAdapter.add(new ReceivedChatBubble("Alkohest", "ne :PPPPPPPPPPPPPPPPPPPPPPPP du är hyb asdjh asdjhas dasdas dasd asd asd asd asd s s XD \n pls gib \n ur money"));
        messageArrayAdapter.add(new SentChatBubble("XDDDDDDDDDDDDDD"));
        messageArrayAdapter.add(new SentChatBubble("ogm"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "pls"));
        messageArrayAdapter.add(new ReceivedChatBubble("Alkohest", "ne :PPPPPPPPPPPPPPPPPPPPPPPP du är hyb asdjh asdjhas dasdas dasd asd asd asd asd s s XD \n pls gib \n ur money"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "pls"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "pls"));
        messageArrayAdapter.add(new ReceivedChatBubble("Arnold", "JAG FATTAR NOLL :("));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "vad är felet"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "ogm"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "money"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "asdjh asdjhas dasdas dasd"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "Nationalensykolpedin"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "Nationalensykolpedin"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "Nationalensykolpedin"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "Nationalensykolpedin"));
        messageArrayAdapter.add(new ReceivedChatBubble("Mormor", "XDDDDDDDDDDDDDD"));
        messageArrayAdapter.add(new SentChatBubble("uuuh i lajk your style"));
    }

    public void sendMessage(View view) {
        String message = ((EditText) getView().findViewById(R.id.activity_channel_main_message)).getText().toString();
        messageArrayAdapter.add(new SentChatBubble(message));
    }
}