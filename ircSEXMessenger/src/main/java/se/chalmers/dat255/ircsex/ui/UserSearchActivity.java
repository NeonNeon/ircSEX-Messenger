package se.chalmers.dat255.ircsex.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.model.WhoisListener;

/**
 * Created by Oskar on 2013-10-07.
 */
public class UserSearchActivity extends SearchActivity implements WhoisListener, NetworkStateHandler.ConnectionListener {

    private ArrayList<String> result;
    private Session session;
    private NetworkStateHandler networkStateHandler;

    private View whois;
    private AlertDialog whoisProgressDialog;
    private AlertDialog whoisResultDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, this);
        content = session.getActiveServer().getKnownUsers();

        networkStateHandler = NetworkStateHandler.getInstance();
        networkStateHandler.addListener(this);
        networkStateHandler.notify(this);
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
        if (networkStateHandler.isConnected()) {
            View view1 = (View) view.getParent().getParent();
            String user = ((TextView) view1.findViewById(android.R.id.text1)).getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            whoisProgressDialog = builder.setTitle(R.string.dialog_whois_title)
                    .setView(new ProgressBar(this))
                    .create();
            whoisProgressDialog.show();
            session.getActiveServer().whois(user);

            findViewById(R.id.action_settings).clearFocus();
        }
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

    public void queryUser(View view) {
        if (networkStateHandler.isConnected()) {
            view = ((LinearLayout) view).getChildAt(0);
            String user = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
            session.removeListener(this);
            Intent data = new Intent();
            data.putExtra(EXTRA_CHANNEL, user);
            setResult(RESULT_RETURN_QUERY, data);
            finish();
        }
    }

    @Override
    public void onOnline() {
        adjustToConnectivity();
    }

    @Override
    public void onOffline() {
        adjustToConnectivity();
    }

    private void adjustToConnectivity() {
        boolean connectivity = networkStateHandler.isConnected();
        for (int i=0; i<getListView().getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) getListView().getChildAt(i);
            layout.setClickable(connectivity);
            layout.findViewById(R.id.userInfoButton).setClickable(connectivity);
        }
    }
}
