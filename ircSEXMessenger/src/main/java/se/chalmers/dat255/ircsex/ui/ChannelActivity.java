package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.Session;
<<<<<<< HEAD
=======
import se.chalmers.dat255.ircsex.model.SessionListener;
>>>>>>> feature/join-part-channels

public class ChannelActivity extends FragmentActivity implements SessionListener, /*ServerConnectDialogFragment.DialogListener,*/ JoinChannelDialogFragment.DialogListener {
    public static final String IRC_CHALMERS_IT = "irc.chalmers.it";
    private DrawerLayout mDrawerLayout;
    private ViewGroup leftDrawer;
    private ListView channelList;
    private ListView rightDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private List<String> connectedChannels;
    private boolean drawerOpen;

    private Session session;
<<<<<<< HEAD
=======
    private ArrayAdapter<String> channelListArrayAdapter;
    private ProgressDialog serverConnectProgressDialog;
>>>>>>> feature/join-part-channels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_main);

        mTitle = mDrawerTitle = getTitle();
        connectedChannels = new ArrayList<String>();
<<<<<<< HEAD
=======
        channelListArrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, connectedChannels);
>>>>>>> feature/join-part-channels
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawer = (ListView) findViewById(R.id.right_drawer);
        View.inflate(this, R.layout.drawer_left, leftDrawer);


        channelList = (ListView) leftDrawer.findViewById(R.id.channel_list);
        // set a custom shadow that overlays the channel_main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);
        // set up the drawer's list view with items and click listener
<<<<<<< HEAD
        leftDrawer.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, connectedChannels));
        leftDrawer.setOnItemClickListener(new DrawerItemClickListener());
=======
        channelList.setAdapter(channelListArrayAdapter);
        channelList.setOnItemClickListener(new DrawerItemClickListener());
>>>>>>> feature/join-part-channels

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                setTitle(mTitle);
                getActionBar().setSubtitle(IRC_CHALMERS_IT);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.closeDrawer(drawerView == rightDrawer ? leftDrawer : rightDrawer);
                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setSubtitle(null);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
<<<<<<< HEAD
            selectItem(0);
            // Annan typ av check för persistence
            startActivityForResult(new Intent(this, NoServersActivity.class), NoServersActivity.REQUEST_SERVER);
            session = new Session();
=======
            // Annan typ av check för persistence
            startNoServersActivity();
            session = new Session(this);
            session.setActiveServer(IRC_CHALMERS_IT);
>>>>>>> feature/join-part-channels
        }
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
        drawerOpen = (mDrawerLayout.isDrawerOpen(leftDrawer) || mDrawerLayout.isDrawerOpen(rightDrawer));
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
            case R.id.action_user_list:
                mDrawerLayout.openDrawer(Gravity.END);
                drawerOpen = true;
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
        connectedChannels.remove(channelName);
        channelListArrayAdapter.notifyDataSetChanged();
        selectItem(connectedChannels.size()-1);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void startNoServersActivity() {
        startActivityForResult(new Intent(this, NoServersActivity.class), NoServersActivity.REQUEST_SERVER);
    }

    private void selectItem(int position) {
        if (position < 0) {
            session.removeServer(IRC_CHALMERS_IT);
            startNoServersActivity();
            return;
        }
        // update the channel_main content by replacing fragments
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ChatFragment.ARG_CHANNEL_INDEX, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
<<<<<<< HEAD
        leftDrawer.setItemChecked(position, true);
//        setTitle(connectedChannels.get(position)); TODO
=======
        channelList.setItemChecked(position, true);
        String channelName = connectedChannels.get(position);
        setTitle(channelName);
        getActionBar().setSubtitle(IRC_CHALMERS_IT);
        session.setActiveChannel(channelName);
>>>>>>> feature/join-part-channels
        mDrawerLayout.closeDrawer(leftDrawer);
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
<<<<<<< HEAD
        session.addServer(server, port, nickname);
//        connectedChannels.add();
=======
        session.addServer(server, port, nickname, this);
        serverConnectProgressDialog = new ProgressDialog(this);
        serverConnectProgressDialog.setIndeterminate(true);
        serverConnectProgressDialog.setMessage("Connecting to " + server);
        serverConnectProgressDialog.show();
>>>>>>> feature/join-part-channels
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
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
    public void onRegistrationCompleted(String host) {
        Log.e("IRCDEBUG", "Registration completed");
        serverConnectProgressDialog.dismiss();
        session.setActiveServer(IRC_CHALMERS_IT);
    }

    @Override
    public void onServerDisconnect(String host, String message) {

    }

    @Override
    public void onServerJoin(String host, String channelName) {
        Log.e("IRCDEBUG", "Joined channel " + channelName);
        connectedChannels.add(channelName);
        ChannelActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                channelListArrayAdapter.notifyDataSetChanged();
                selectItem(connectedChannels.size()-1);
            }
        });
    }

    @Override
    public void onServerPart(String host, String channelName) {

    }

    @Override
    public void onChannelJoin(String host, String channel, String message) {

    }

    @Override
    public void onChannelPart(String host, String channel, String message) {

    }

    @Override
    public void onChannelMessage(String host, String channel, String message) {

    }
}