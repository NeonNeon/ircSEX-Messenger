package se.chalmers.dat255.ircsex.ui.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChatIrcMessage;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.IrcUser;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.model.SessionListener;

/**
 * Created by Oskar on 2013-10-07.
 */
public class UserSearchActivity extends SearchActivity implements SessionListener {

    private ArrayList<String> result;
    private Session session;

    private View whois;
    private AlertDialog whoisProgressDialog;
    private AlertDialog whoisResultDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, this);
        content = session.getActiveServer().getKnownUsers();
    }

    @Override
    public BaseAdapter getAdapter() {
        return new ArrayAdapter<String>(this, R.layout.drawer_list_item, android.R.id.text1, result);
    }

    @Override
    public void clearAdapter() {
        result = new ArrayList<String>();
        super.clearAdapter();
    }

    @Override
    public void search(String search) {
        search = search.toLowerCase();
        clearAdapter();
        for (String entry : content) {
            if (entry.toLowerCase().contains(search)) {
                result.add(entry);
            }
        }
        update();
    }

    public void userInfo(View view) {
        View view1 = (View) view.getParent().getParent();
        String user = ((TextView) view1.findViewById(android.R.id.text1)).getText().toString();

        long time = System.currentTimeMillis();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        whoisProgressDialog = builder.setTitle(R.string.dialog_whois_title)
                .setView(new ProgressBar(this))
                .create();
        whoisProgressDialog.show();
        session.getActiveServer().whois(user);

        findViewById(R.id.action_search).clearFocus();
    }

    private void showWhoisDialog(final String nick) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            whoisProgressDialog.dismiss();
            LayoutInflater inflater = getLayoutInflater();
            whois = inflater.inflate(R.layout.dialog_whois, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            whoisResultDialog = builder.setTitle(getApplication().getString(R.string.dialog_whois_title) +" - "+ nick)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            whois = null;
                        }
                    }).setView(whois)
                    .setNegativeButton(R.string.dialog_generic_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            whois = null;
                        }
                    })
                    .create();

            whoisResultDialog.show();
        }
    }

    @Override
    public void onConnectionEstablished(String host) {

    }

    @Override
    public void onRegistrationCompleted(String host) {

    }

    @Override
    public void onDisconnect(String host) {

    }

    @Override
    public void onServerDisconnect(String host, String message) {

    }

    @Override
    public void onServerJoin(String host, String channelName) {

    }

    @Override
    public void onServerPart(String host, String channelName) {

    }

    @Override
    public void onChannelUserChange(String host, String channel, List<IrcUser> users) {

    }

    @Override
    public void onChannelUserJoin(String host, String channel, IrcMessage joinMessage) {

    }

    @Override
    public void onChannelUserPart(String host, String channel, IrcMessage partMessage) {

    }

    @Override
    public void onNickChange(String host, IrcMessage ircMessage) {

    }

    @Override
    public void onChannelMessage(String host, String channel, ChatIrcMessage message) {

    }

    @Override
    public void onSentMessage(String host, String channel, ChatIrcMessage message) {

    }

    @Override
    public void whoisChannels(final String nick, final List<String> channels) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (whois == null) {
                        showWhoisDialog(nick);
                    }
                    TextView textView = ((TextView) whois.findViewById(R.id.dialog_whois_channels));
                    String text = textView.getText().toString();
                    text += (text.equals("") ? "" : "\n") + channels.toString().replace("[", "").replace("]", "").replace(", ", "\n");
                    textView.setText(text);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                }
            });
        }
    }

    @Override
    public void whoisRealname(final String nick, final String realname) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (whois == null) {
                        showWhoisDialog(nick);
                    }
                    ((TextView) whois.findViewById(R.id.dialog_whois_realname)).setText(realname);
                }
            });
        }
    }

    @Override
    public void whoisIdleTime(final String nick, final String formattedIdleTime) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (whois == null) {
                        showWhoisDialog(nick);
                    }
                    ((TextView) whois.findViewById(R.id.dialog_whois_idle)).setText(formattedIdleTime);
                }
            });
        }
    }
}
