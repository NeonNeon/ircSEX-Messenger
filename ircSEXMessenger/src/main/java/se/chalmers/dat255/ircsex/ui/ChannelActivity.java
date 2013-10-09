package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChatIrcMessage;
import se.chalmers.dat255.ircsex.model.IrcUser;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.model.SessionListener;
import se.chalmers.dat255.ircsex.ui.dialog.JoinChannelDialogFragment;
import se.chalmers.dat255.ircsex.ui.dialog.ServerConnectDialogFragment;
import se.chalmers.dat255.ircsex.ui.search.ChannelSearchActivity;
import se.chalmers.dat255.ircsex.ui.search.MessageSearchActivity;
import se.chalmers.dat255.ircsex.ui.search.SearchActivity;
import se.chalmers.dat255.ircsex.ui.search.UserSearchActivity;
import se.chalmers.dat255.ircsex.view.IrcChannelItem;
import se.chalmers.dat255.ircsex.view.IrcServerHeader;

public class ChannelActivity extends FragmentActivity implements SessionListener,
        JoinChannelDialogFragment.DialogListener, ChatFragment.ChatMessageSendListener,
        NetworkStateHandler.ConnectionListener {

    private static final String CHAT_FRAGMENT_TAG = "chat_fragment";

    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private ListView rightDrawer;
    private ViewGroup leftDrawerContainer;
    private ViewGroup rightDrawerContainer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<IrcUser> userArrayAdapter;
    private List<IrcUser> users;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private IrcChannelSelector ircChannelSelector;
    private boolean drawerOpen;
    private ChatFragment fragment;
    private String channelName;

    private Session session;
    private ProgressDialog serverConnectProgressDialog;
    private AlertDialog whoisProgressDialog;
    private AlertDialog whoisResultDialog;
    private View whois;
    private static int selected = -1;
    private ChannelListOnClickListener channelDrawerOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_channel_main);

        NetworkStateHandler.addListener(this);

        mTitle = mDrawerTitle = getTitle();
        if (ircChannelSelector == null) {
            ircChannelSelector = new IrcChannelSelector(this);
        }
        channelDrawerOnClickListener = new ChannelListOnClickListener();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);

        leftDrawerContainer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawerContainer = (ViewGroup) findViewById(R.id.right_drawer);
        leftDrawer = (ListView) findViewById(R.id.left_drawer_list);
        leftDrawer.setAdapter(ircChannelSelector.getArrayAdapter());
        leftDrawer.setItemsCanFocus(false);
        leftDrawer.setOnItemClickListener(channelDrawerOnClickListener);
        leftDrawer.setItemsCanFocus(false);
        rightDrawer = (ListView) findViewById(R.id.right_drawer_list);
        users = new ArrayList<IrcUser>();
        userArrayAdapter = new ArrayAdapter<IrcUser>(this, R.layout.drawer_list_item, android.R.id.text1, users);

        rightDrawer.setAdapter(userArrayAdapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setSubtitle(null);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        if (session == null) {
            session = Session.getInstance(this, this);
            if (session.containsServers()) {
                showConnectionDialog(getString(R.string.dialog_connect_reconnect));
            } else {
                startNoServersActivity();
            }
        }
        else {
            fragment = (ChatFragment) getFragmentManager().findFragmentByTag(CHAT_FRAGMENT_TAG);
            fragment.bringUpToSpeed(this, session.getActiveChannel());
            setTitle(channelName);
            updateUserList(session.getActiveChannel().getUsers());
            Log.e("IRCDEBUG", "Post select: " +  fragment.toString());
        }
    }

    private void showConnectionDialog(String message) {
        serverConnectProgressDialog = new ProgressDialog(this);
        serverConnectProgressDialog.setIndeterminate(true);
        serverConnectProgressDialog.setMessage(message);
        serverConnectProgressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        drawerOpen = (drawerLayout.isDrawerOpen(leftDrawerContainer) || drawerLayout.isDrawerOpen(rightDrawerContainer));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            drawerLayout.closeDrawer(rightDrawerContainer);
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_add_server:
                DialogFragment serverConnectDialogFragment = new ServerConnectDialogFragment();
                serverConnectDialogFragment.show(getSupportFragmentManager(), "serverconnect");
                break;
            case R.id.action_join_channel:
                DialogFragment joinChannelDialogFragment = new JoinChannelDialogFragment();
                joinChannelDialogFragment.show(getSupportFragmentManager(), "joinchannel");
                break;
            case R.id.action_leave_channel:
                leaveActiveChannel();
                break;
//            case R.id.action_user_list:
//                drawerLayout.openDrawer(Gravity.END);
//                drawerOpen = true;
//                break;
            case R.id.action_settings:
                changeNick();
                break;
            case R.id.action_invite_user:
                inviteUser();
                break;
            case R.id.search_messages:
                Intent intent = new Intent(this, MessageSearchActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void inviteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_invite_user, null);
        final EditText userToInvite = (EditText) view.findViewById(android.R.id.text1);
        userToInvite.setHint(getString(R.string.dialog_invite_hint));
        builder.setTitle(R.string.dialog_invite_title)
                .setView(view)
                .setPositiveButton(getString(R.string.dialog_generic_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String user = userToInvite.getText().toString();
                        session.getActiveServer().inviteUser(user, session.getActiveChannel());
                    }
                })
                .setNegativeButton(R.string.dialog_generic_cancel, null)
                .create().show();
    }

    private void changeNick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_nick, null);
        final EditText nickEditText = (EditText) view.findViewById(android.R.id.text1);
        nickEditText.setHint(getString(R.string.dialog_nick_hint) + " " + session.getActiveServer().getHost());
        builder.setTitle(R.string.dialog_nick_title)
                .setView(view)
                .setPositiveButton(getString(R.string.dialog_generic_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newNick = nickEditText.getText().toString();
                        Log.e("IRCDEBUG", "Change nickname to " + newNick);
                        session.changeNick(newNick);
                    }
                })
                .setNegativeButton(R.string.dialog_generic_cancel, null)
                .create().show();
    }

    @Override
    public void onJoinDialogAccept(DialogFragment dialog) {
        String channelName = ((TextView) dialog.getDialog().findViewById(R.id.dialog_join_channel_channel_name)).getText().toString();
        joinChannel(channelName);
    }

    private void joinChannel(String channelName) {
        session.joinChannel(session.getActiveServer().getHost(), channelName);
    }

    private void leaveActiveChannel() {
        String channelName = session.getActiveChannel().getChannelName();
        session.partChannel(session.getActiveServer().getHost(), channelName);
        int newPosition = ircChannelSelector.removeChannel(selected);
        if (ircChannelSelector.isIndexHeading(newPosition)) {
            newPosition = ircChannelSelector.removeServer(newPosition);
            session.removeServer(session.getActiveServer().getHost());
            if (newPosition == IrcChannelSelector.NO_SERVERS_CONNECTED) {
                startNoServersActivity();
                return;
            }
        }
        selectItem(newPosition);
    }

    public void disconnectServer(View view) {
        ircChannelSelector.disconnect(0);
    }

    private void startNoServersActivity() {
        startActivityForResult(new Intent(this, NoServersActivity.class), NoServersActivity.REQUEST_SERVER);
    }

    /**
     *  The click listener for ListView in the left drawer, the Channel List.
     */
    private class ChannelListOnClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!ircChannelSelector.isIndexHeading(position)) {
                selectItem(position);
            }
            else {
//                ircChannelSelector.expandHeader(0); Enable once model/backend supports it.
                leftDrawer.setItemChecked(selected, true);
            }
        }
    }

    public void bubbleClicked(View view) {
        String name = ((TextView)view.findViewById(R.id.chat_bubble_nick))
                .getText().toString();
        ((TextView)findViewById(R.id.fragment_chat_message))
                .append(name + ": ");
    }

    private void selectItem(int position) {
        channelName = ircChannelSelector.getItem(position).getText();
        session.setActiveChannel(channelName);

        fragment = new ChatFragment(this, session.getActiveChannel());
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.channel_layout, fragment, CHAT_FRAGMENT_TAG).addToBackStack(CHAT_FRAGMENT_TAG).commit();

        leftDrawer.setItemChecked(position, true);
        setTitle(channelName);
        drawerLayout.closeDrawer(leftDrawerContainer);
        selected = position;
        updateUserList(session.getActiveChannel().getUsers());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case NoServersActivity.RESULT_RETURN_DATA:
                String server = data.getStringExtra(NoServersActivity.EXTRA_SERVER);
                String port = data.getStringExtra(NoServersActivity.EXTRA_PORT);
                String nickname = data.getStringExtra(NoServersActivity.EXTRA_NICKNAME);
                // Maybe validate here, or maybe somewhere else? Should we even validate?
                startServer(server, Integer.parseInt(port), nickname);
                break;
            case SearchActivity.RESULT_RETURN_CHANNEL:
                String channel = data.getStringExtra(SearchActivity.EXTRA_CHANNEL);
                joinChannel(channel);
                break;
            case Activity.RESULT_CANCELED:
                if (requestCode == NoServersActivity.REQUEST_SERVER) {
                    finish();
                }
                break;
        }
    }

    private void startServer(String server, int port, String nickname) {
        session.addServer(server, port, nickname, this);
        showConnectionDialog(getString(R.string.dialog_connect_connecting) + " " + server);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        getActionBar().setSubtitle(session.getActiveServer().getHost());
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnectionEstablished(String host) {
        Log.e("IRCDEBUG", "Opened connection " + host);
    }

    @Override
    public void onRegistrationCompleted(final String host) {
        Log.e("IRCDEBUG", "Registration completed");
        serverConnectProgressDialog.dismiss();
        session.setActiveServer(host);
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ircChannelSelector.addHeader(new IrcServerHeader(host));
            }
        });
    }

    @Override
    public void onDisconnect(String host) {

    }

    @Override
    public void onServerDisconnect(String host, String message) {

    }

    @Override
    public void onServerJoin(final String host, final String channelName) {
        Log.e("IRCDEBUG", "Joined channel " + channelName);
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int channelIndex = ircChannelSelector.addChannel(host, new IrcChannelItem(channelName));
                selectItem(channelIndex);
            }
        });
    }

    @Override
    public void onServerPart(String host, String channelName) {

    }

    @Override
    public void onChannelUserChange(String host, String channel, final List<IrcUser> users) {
        if (session.getActiveChannel() != null && session.getActiveChannel().getChannelName().equals(channel)) {
            ChannelActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUserList(users);
                    userArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onChannelUserJoin(String host, String channel, IrcUser user) {
        if (session.getActiveChannel() != null && session.getActiveChannel().getChannelName().equals(channel)) {
            addInfoMessage(user + " has joined the channel");
        }
    }

    @Override
    public void onChannelUserPart(String host, String channel, String nick) {
        if (session.getActiveChannel() != null && session.getActiveChannel().getChannelName().equals(channel)) {
            addInfoMessage(nick + " has left the channel");
        }
    }

    private void updateUserList(List<IrcUser> users) {
        this.users.clear();
        for (IrcUser user : users) {
            this.users.add(user);
        }
        userArrayAdapter.notifyDataSetChanged();
    }

    public void userInfo(View view) {
        View view1 = (View) view.getParent().getParent();
        String user = ((TextView) view1.findViewById(android.R.id.text1)).getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        whoisProgressDialog = builder.setTitle(R.string.dialog_whois_title)
                .setView(new ProgressBar(this))
                .create();
        whoisProgressDialog.show();
        session.getActiveServer().whois(user);
    }

    public void queryUser(View view) {
        view = ((LinearLayout) view).getChildAt(0);
        String user = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
        session.getActiveServer().queryUser(IrcUser.extractUserName(user));
        drawerLayout.closeDrawer(Gravity.END);
    }

    @Override
    public void onChannelMessage(String host, final String channel, final ChatIrcMessage message) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (channel.equals(channelName)) {
                    fragment.addMessage(message);
                    Log.e("IRCDEBUG", "onChannelMessage to: " + fragment.toString());
                }
            }
        });
    }

    @Override
    public void onSentMessage(String host, final String channel, final ChatIrcMessage message) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (channel.equals(channelName)) {
                    fragment.addSentMessage(message);
                }
            }
        });
    }

    @Override
    public void onNickChange(String host, final String oldNick, final String newNick) {
        addInfoMessage(oldNick + " is now known as " + newNick);
    }

    private void addInfoMessage(final String infoMessage) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.addInfoMessage(infoMessage);
            }
        });
    }

    @Override
    public void userSentMessage(String string) {
        session.getActiveServer().sendMessage(session.getActiveChannel().getChannelName(), string);
    }

    @Override
    public void whoisChannels(final String nick, final List<String> channels) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            ChannelActivity.this.runOnUiThread(new Runnable() {
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
            ChannelActivity.this.runOnUiThread(new Runnable() {
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
            ChannelActivity.this.runOnUiThread(new Runnable() {
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

    @Override
    public void onOnline() {
    }

    @Override
    public void onOffline() {
        Intent noInternetIntent = new Intent(this, NoInternetActivity.class);
        startActivity(noInternetIntent);
    }

    private void showWhoisDialog(final String nick) {
        if (whoisProgressDialog != null && whoisProgressDialog.isShowing()) {
            whoisProgressDialog.dismiss();
            LayoutInflater inflater = getLayoutInflater();
            whois = inflater.inflate(R.layout.dialog_whois, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelActivity.this);
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

    public void leftDrawerSearch(View view) {
        Intent intent = new Intent(this, ChannelSearchActivity.class);
        startActivity(intent);
    }

    public void rightDrawerSearch(View view) {
        Intent intent = new Intent(this, UserSearchActivity.class);
        startActivity(intent);

    }
}