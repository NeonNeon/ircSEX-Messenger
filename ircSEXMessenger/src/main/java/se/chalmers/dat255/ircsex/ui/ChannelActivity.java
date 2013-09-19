package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.Session;

public class ChannelActivity extends FragmentActivity implements Session.SessionListener, /*ServerConnectDialogFragment.DialogListener,*/ JoinChannelDialogFragment.DialogListener {
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
    private ArrayAdapter<String> channelListArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_main);

        mTitle = mDrawerTitle = getTitle();
        connectedChannels = new ArrayList<String>();
        channelListArrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, connectedChannels);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawer = (ListView) findViewById(R.id.right_drawer);
        View.inflate(this, R.layout.drawer_left, leftDrawer);


        channelList = (ListView) leftDrawer.findViewById(R.id.channel_list);
        // set a custom shadow that overlays the channel_main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);
        // set up the drawer's list view with items and click listener
        channelList.setAdapter(channelListArrayAdapter);
        channelList.setOnItemClickListener(new DrawerItemClickListener());

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
                getActionBar().setTitle(mTitle);
                getActionBar().setSubtitle("irc." + mTitle.toString().toLowerCase() + ".com");
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
            selectItem(0);
            // Annan typ av check för persistence
            startActivityForResult(new Intent(this, NoServersActivity.class), NoServersActivity.REQUEST_SERVER);
            session = new Session(this);
            session.setActiveServer(IRC_CHALMERS_IT);
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
        session.joinChannel(session.getActiveServer().getHost(), "#" + channelName); //TODO: mappa aktiv server korrekt
    }

    private void leaveActiveChannel() {

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the channel_main content by replacing fragments
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ChatFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        channelList.setItemChecked(position, true);
//        setTitle(connectedChannels.get(position)); TODO
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
        session.addServer(server, port, nickname, this);
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
    public void fireSessionEvent(Session.SessionEvent event, String message) {
        switch (event) {
            case SERVER_CONNECT:
                Log.e("IRCDEBUG", "Opened connection " + message);
                break;
            case SERVER_JOIN:
                Log.e("IRCDEBUG", "Joined channel " + message);
                connectedChannels.add(message);
                session.setActiveChannel(message);
                ChannelActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        channelListArrayAdapter.notifyDataSetChanged();
                    }
                });
                break;

        }
    }
}