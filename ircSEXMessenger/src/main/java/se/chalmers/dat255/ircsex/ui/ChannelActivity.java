package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.awt.Button;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcUser;
import se.chalmers.dat255.ircsex.model.Session;
import se.chalmers.dat255.ircsex.model.SessionListener;
import se.chalmers.dat255.ircsex.ui.dialog.JoinChannelDialogFragment;
import se.chalmers.dat255.ircsex.ui.dialog.ServerConnectDialogFragment;
import se.chalmers.dat255.ircsex.view.IrcChannelItem;
import se.chalmers.dat255.ircsex.view.IrcServerHeader;

public class ChannelActivity extends FragmentActivity implements SessionListener, JoinChannelDialogFragment.DialogListener {
    private DrawerLayout drawerLayout;
    private ListView leftDrawer;
    private ListView rightDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<IrcUser> userArrayAdapter;
    private List<IrcUser> users;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private IrcChannelSelector ircChannelSelector;
    private boolean drawerOpen;

    private Session session;
    private ProgressDialog serverConnectProgressDialog;
    private int selected = -1;
    private ChannelListOnClickListener channelDrawerOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_main);

        mTitle = mDrawerTitle = getTitle();
        ircChannelSelector = new IrcChannelSelector(this);
        channelDrawerOnClickListener = new ChannelListOnClickListener();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);

        leftDrawer = (ListView) findViewById(R.id.left_drawer);
        leftDrawer.setAdapter(ircChannelSelector.getArrayAdapter());
        leftDrawer.setItemsCanFocus(false);
        leftDrawer.setOnItemClickListener(channelDrawerOnClickListener);
        leftDrawer.setItemsCanFocus(false);
        rightDrawer = (ListView) findViewById(R.id.right_drawer);
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
                drawerLayout.closeDrawer(drawerView == rightDrawer ? leftDrawer : rightDrawer);
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setSubtitle(null);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        session = new Session(this, this);
        if (!session.containsServers()) {
            startNoServersActivity();
        } else {
            showConnectionDialog(getString(R.string.dialog_connect_reconnect));
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
        drawerOpen = (drawerLayout.isDrawerOpen(leftDrawer) || drawerLayout.isDrawerOpen(rightDrawer));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onJoinDialogAccept(DialogFragment dialog) {
        String channelName = ((TextView) dialog.getDialog().findViewById(R.id.dialog_join_channel_channel_name)).getText().toString();
        session.joinChannel(session.getActiveServer().getHost(), "#" + channelName);
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

    private void selectItem(int position) {
        // update the channel_main content by replacing fragments
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ChatFragment.ARG_CHANNEL_INDEX, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.channel_layout, fragment).commit();

        // update selected item and title, then close the drawer
        leftDrawer.setItemChecked(position, true);
        String channelName = ircChannelSelector.getItem(position).getText();
        setTitle(channelName);
        session.setActiveChannel(channelName);
        drawerLayout.closeDrawer(leftDrawer);
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
            case Activity.RESULT_CANCELED:
                finish();
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

        session.getActiveServer().whois(user);
    }

    @Override
    public void onChannelMessage(String host, String channel, String message) {

    }

    @Override
    public void onNickChange(String host, String oldNick, String newNick) {

    }
}