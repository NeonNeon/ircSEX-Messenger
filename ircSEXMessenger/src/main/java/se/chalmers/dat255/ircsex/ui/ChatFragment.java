package se.chalmers.dat255.ircsex.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;

public class ChatFragment extends Fragment {
    public static final String ARG_CHANNEL_INDEX = "channelIndex";

    public ChatFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
<<<<<<< HEAD
        int i = getArguments().getInt(ARG_PLANET_NUMBER);
//        String planet = getResources().getStringArray(R.array.planets_array)[i];

        ((TextView) rootView.findViewById(R.id.textView)).setText("hest");
        getActivity().setTitle("hest");
        getActivity().getActionBar().setSubtitle("irc.hest" + ".com");
=======
        int i = getArguments().getInt(ARG_CHANNEL_INDEX);

        ((TextView) rootView.findViewById(R.id.textView)).setText("IRCChannel at index "+i);
>>>>>>> feature/join-part-channels
        return rootView;
    }
}