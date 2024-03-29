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
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChatIrcMessage;
import se.chalmers.dat255.ircsex.model.IrcHighlight;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.IrcUser;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;
import se.chalmers.dat255.ircsex.model.ServerConnectionData;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.model.SessionListener;
import se.chalmers.dat255.ircsex.ui.dialog.JoinChannelDialogFragment;
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
    private ProgressDialog serverConnectProgressDialog;
    private AlertDialog whoisProgressDialog;
    private AlertDialog whoisResultDialog;
    private List<IrcUser> users;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private IrcChannelSelector ircChannelSelector;
    private ChatFragment fragment;
    private String channelName;
    private Session session;
    private View whois;
    private int selected = -1;
    private Menu menu;

    private NetworkStateHandler networkStateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_channel_main);

        mTitle = mDrawerTitle = getTitle();
        ircChannelSelector = new IrcChannelSelector(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);

        leftDrawerContainer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawerContainer = (ViewGroup) findViewById(R.id.right_drawer);
        leftDrawer = (ListView) findViewById(R.id.left_drawer_list);
        leftDrawer.setAdapter(ircChannelSelector.getArrayAdapter());
        leftDrawer.setOnItemClickListener(new ChannelListOnClickListener());
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
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setSubtitle(null);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        session = Session.getInstance(this, this);
        if (session.containsServers()) {
            showConnectionDialog(getString(R.string.dialog_connect_reconnect));
        } else {
            startNoServersActivity();
        }

        networkStateHandler = NetworkStateHandler.getInstance();
        networkStateHandler.addListener(this);
        if (!networkStateHandler.isConnected()) {
            Intent intent = new Intent(this, NoInternetActivity.class);
            startActivity(intent);
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
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_main, menu);
        updateHighlightBadge();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            drawerLayout.closeDrawer(rightDrawerContainer);
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_join_channel:
                DialogFragment joinChannelDialogFragment = new JoinChannelDialogFragment();
                joinChannelDialogFragment.show(getSupportFragmentManager(), "joinchannel");
                break;
            case R.id.action_leave_channel:
                leaveActiveChannel();
                break;
            case R.id.action_change_nick:
                changeNick();
                break;
            case R.id.action_invite_user:
                inviteUser();
                break;
            case R.id.action_disconnect:
                session.getActiveServer().quitServer("");
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.search_messages:
                intent = new Intent(this, MessageSearchActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void showHighlight(View view) {
        List<IrcHighlight> highlights = session.getActiveServer().getHighlights();
        List<IrcHighlight> lastMessages = session.getActiveServer().getLastMessages();
        if (highlights.size() > 0 || lastMessages.size() > 0) {
            IrcHighlight highlight = highlights.size() > 0 ? highlights.get(0) : lastMessages.get(0);
            int index = ircChannelSelector.indexOf(highlight.getChannel().getChannelName());
            if (index != -1) {
                selectItem(index);
            }
            if (highlights.size() > 0) {
                session.getActiveServer().readHighlight(highlight);
            } else {
                session.getActiveServer().readLastMessage(highlight);
            }
        }
        updateHighlightBadge();
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
                        Log.d("IRCDEBUG", "Change nickname to " + newNick);
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

    private void queryUser(String user) {
        session.getActiveServer().queryUser(IrcUser.extractUserName(user));
    }

    private void leaveActiveChannel() {
        if (ircChannelSelector.getSize() == 1) {
            return;
        }
        String channelName = session.getActiveChannel().getChannelName();
        session.partChannel(session.getActiveServer().getHost(), channelName);
        int newPosition = ircChannelSelector.removeChannel(selected);
        if (ircChannelSelector.isIndexHeading(newPosition)) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            setTitle(mDrawerTitle);
            getActionBar().setSubtitle(null);
            return;
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
        String name = ((TextView)view.findViewById(R.id.chat_bubble_nick)).getText().toString();
        ((TextView)findViewById(R.id.fragment_chat_message)).append(name + ": ");
    }

    private void selectItem(int position) {
        channelName = ircChannelSelector.getItem(position).getText();
        session.setActiveChannel(channelName);
        session.getActiveServer().readHighlight(channelName);
        session.getActiveServer().readLastMessage(channelName);

        fragment = new ChatFragment(this, session.getActiveChannel());
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.channel_layout, fragment, CHAT_FRAGMENT_TAG)
                .addToBackStack(CHAT_FRAGMENT_TAG).commitAllowingStateLoss();

        leftDrawer.setItemChecked(position, true);
        setTitle(channelName);
        drawerLayout.closeDrawer(leftDrawerContainer);
        selected = position;
        if (session.getActiveChannel() != null) {
            updateUserList(session.getActiveChannel().getUsers());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case NoServersActivity.RESULT_RETURN_DATA:
                ServerConnectionData connectionData = new ServerConnectionData(
                        data.getStringExtra(NoServersActivity.EXTRA_SERVER),
                        Integer.parseInt(data.getStringExtra(NoServersActivity.EXTRA_PORT)),
                        data.getStringExtra(NoServersActivity.EXTRA_NICKNAME),
                        data.getStringExtra(NoServersActivity.EXTRA_USERNAME),
                        "Realname",
                        data.getStringExtra(NoServersActivity.EXTRA_PASSWORD),
                        false, // use ssl
                        data.getBooleanExtra(NoServersActivity.EXTRA_USE_SSH, false),
                        data.getStringExtra(NoServersActivity.EXTRA_SSH_HOSTNAME),
                        data.getStringExtra(NoServersActivity.EXTRA_SSH_USERNAME),
                        data.getStringExtra(NoServersActivity.EXTRA_SSH_PASSWORD)
                );
                startServer(connectionData);
                break;
            case SearchActivity.RESULT_RETURN_CHANNEL:
                String channel = data.getStringExtra(SearchActivity.EXTRA_CHANNEL);
                joinChannel(channel);
                break;
            case SearchActivity.RESULT_RETURN_QUERY:
                String queryTarget = data.getStringExtra(SearchActivity.EXTRA_CHANNEL);
                queryUser(queryTarget);
                drawerLayout.closeDrawer(rightDrawerContainer);
                break;
            case Activity.RESULT_CANCELED:
                if (requestCode == NoServersActivity.REQUEST_SERVER) {
                    finish();
                }
                break;
        }
    }

    private void startServer(ServerConnectionData data) {
        session.addServer(data, this);
        showConnectionDialog(getString(R.string.dialog_connect_connecting) + " " + data.getServer());
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        getActionBar().setSubtitle(session.getActiveServer().getHost());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnectionEstablished(String host) {
        Log.d("IRCDEBUG", "Opened connection " + host);
    }

    @Override
    public void onRegistrationCompleted(final String host) {
        Log.d("IRCDEBUG", "Registration completed");
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
    public void onServerDisconnect(final String host) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ircChannelSelector.isEmpty()) {
                    startNoServersActivity();
                } else {
                    ircChannelSelector.removeServer(ircChannelSelector.indexOf(host));
                }
            }
        });
    }

    @Override
    public void onServerJoin(final String host, final String channelName) {
        Log.d("IRCDEBUG", "Joined channel " + channelName);
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
    public void onChannelUserJoin(String host, String channel, IrcMessage joinMessage) {
        if (session.getActiveChannel() != null && session.getActiveChannel().getChannelName().equals(channel)) {
            addInfoMessage(joinMessage);
        }
    }

    @Override
    public void onChannelUserPart(String host, String channel, IrcMessage partMessage) {
        if (session.getActiveChannel() != null && session.getActiveChannel().getChannelName().equals(channel)) {
            addInfoMessage(partMessage);
        }
    }

    private void updateUserList(List<IrcUser> users) {
        this.users.clear();
        for (IrcUser user : users) {
            this.users.add(user);
        }
        userArrayAdapter.notifyDataSetChanged();
        adjustToConnectivity();
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
        queryUser(user);
        drawerLayout.closeDrawer(Gravity.END);
    }

    @Override
    public void onChannelMessage(String host, final String channel, final ChatIrcMessage message) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (channel.equals(channelName)) {
                    if (message.getUser().isSelf()) {
                        fragment.addSentMessage(message);
                    } else {
                        fragment.addMessage(message);
                    }
                    Log.d("IRCDEBUG", "onChannelMessage to: " + fragment.toString());
                }
            }
        });
    }

    @Override
    public void onHighlightChange() {
        updateHighlightBadge();
    }

    private void updateHighlightBadge() {
        LinearLayout highlightButton = (LinearLayout) menu.findItem(R.id.highlightbadge).getActionView();
        if (session.getActiveServer() != null && highlightButton.getChildAt(0) != null) {
            ChannelActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout highlightButton = (LinearLayout) menu.findItem(R.id.highlightbadge).getActionView();
                    int highlights = session.getActiveServer().getHighlights().size();
                    int lastMessages = session.getActiveServer().getLastMessages().size();
                    if (highlights > 0) {
                        highlightButton.getChildAt(0).setBackgroundResource(R.drawable.highlightbadge_background_highlight);
                        highlightButton.setBackground(getResources().getDrawable(R.drawable.user_click));
                        setHighlightButtonText(Integer.toString(highlights), highlightButton);
                    } else if (lastMessages > 0) {
                        highlightButton.getChildAt(0).setBackgroundResource(R.drawable.highlightbadge_background);
                        highlightButton.setBackground(getResources().getDrawable(R.drawable.user_click));
                        setHighlightButtonText(Integer.toString(lastMessages), highlightButton);
                    } else {
                        highlightButton.getChildAt(0).setBackgroundResource(R.drawable.highlightbadge_background_disabled);
                        highlightButton.setBackground(null);
                        setHighlightButtonText(Integer.toString(0), highlightButton);
                    }
                }
            });
        }
    }

    private void setHighlightButtonText(String text, LinearLayout highlightButton) {
        ((TextView) ((LinearLayout) highlightButton.getChildAt(0)).getChildAt(0))
                .setText(text);
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
    public void queryError(String message) {
        showToast(message);
    }

    @Override
    public void loginError(String message) {

    }

    @Override
    public void channelJoinError(String message) {
        showToast(message);
        DialogFragment joinChannelDialogFragment = new JoinChannelDialogFragment();
        joinChannelDialogFragment.show(getSupportFragmentManager(), "joinchannel");
    }

    @Override
    public void nickChangeError(String message) {
        showToast(message);
    }

    @Override
    public void inviteError(String message) {
        showToast(message);
    }

    /**
     * Displays an toast
     *
     * @param message Text to show in the toast
     */
    private void showToast(final String message) {
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChannelActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Launches a toast showing the connection error,
     * then resets the session and sends the user back
     * to the "New Connection" screen.
     */
    @Override
    public void serverConnectionError() {
        showToast("Could not connect to server");

        // Session doesn't exist yet so it's not possible to remove the server this way.
        //Log.e("IRCERROR", session.getActiveServer().getHost());
        //session.removeServer(session.getActiveServer().getHost());
        session.reset();
        serverConnectProgressDialog.dismiss();
        startNoServersActivity();
    }


    @Override
    public void onNickChange(String host, String channel, IrcMessage ircMessage) {
        if (channel.equals(channelName)) {
            addInfoMessage(ircMessage);
        }
    }

    private void addInfoMessage(final IrcMessage infoMessage) {
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
        if (menu != null) {
            menu.findItem(R.id.search_messages).setEnabled(true);
            menu.findItem(R.id.search_messages).setIcon(R.drawable.ic_action_search);
            menu.findItem(R.id.action_invite_user).setEnabled(true);
            menu.findItem(R.id.action_change_nick).setEnabled(true);
            menu.findItem(R.id.action_join_channel).setEnabled(true);
            menu.findItem(R.id.action_leave_channel).setEnabled(true);
            menu.findItem(R.id.action_disconnect).setEnabled(true);
            drawerLayout.findViewById(R.id.channel_search_drawer_button).setEnabled(true);
            ((LinearLayout) drawerLayout.findViewById(R.id.channel_search_drawer_button))
                    .getChildAt(0).setEnabled(true);
            ((TextView) ((LinearLayout) drawerLayout
                    .findViewById(R.id.channel_search_drawer_button)).getChildAt(0))
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
            adjustToConnectivity();
        }
    }

    @Override
    public void onOffline() {
        if (menu != null) {
            menu.findItem(R.id.search_messages).setEnabled(false);
            menu.findItem(R.id.search_messages).setIcon(android.R.drawable.ic_menu_search);
            menu.findItem(R.id.action_invite_user).setEnabled(false);
            menu.findItem(R.id.action_change_nick).setEnabled(false);
            menu.findItem(R.id.action_join_channel).setEnabled(false);
            menu.findItem(R.id.action_leave_channel).setEnabled(false);
            menu.findItem(R.id.action_disconnect).setEnabled(false);
            drawerLayout.findViewById(R.id.channel_search_drawer_button).setEnabled(false);
            ((TextView) ((LinearLayout) drawerLayout
                    .findViewById(R.id.channel_search_drawer_button)).getChildAt(0))
                    .setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0);
            adjustToConnectivity();
        }
    }

    private void adjustToConnectivity() {
        boolean connectivity = networkStateHandler.isConnected();
        for (int i=0; i<rightDrawer.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) rightDrawer.getChildAt(i);
            layout.setClickable(connectivity);
            layout.findViewById(R.id.userInfoButton).setClickable(connectivity);
        }
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
        startActivityForResult(intent, SearchActivity.REQUEST_CHANNEL);
    }

    public void rightDrawerSearch(View view) {
        Intent intent = new Intent(this, UserSearchActivity.class);
        startActivityForResult(intent, SearchActivity.REQUEST_CHANNEL);
    }
}