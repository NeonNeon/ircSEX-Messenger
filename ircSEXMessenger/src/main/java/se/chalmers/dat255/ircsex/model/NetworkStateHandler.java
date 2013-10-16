package se.chalmers.dat255.ircsex.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oskar on 2013-10-02.
 */
public class NetworkStateHandler extends BroadcastReceiver {

    private static NetworkStateHandler instance;

    private boolean internet;
    private ConnectionListener listener;
    private List<ConnectionListener> listeners;

    private NetworkStateHandler() {
        listener = new ConnectionListenerImpl();
        listeners = new ArrayList<ConnectionListener>();

        Session.context.getApplicationContext().registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        checkConnectivity();
    }

    public synchronized static NetworkStateHandler getInstance() {
        if (instance == null) {
            instance = new NetworkStateHandler();
        }
        return instance;
    }

    public void checkConnectivity() {
        if (Session.context != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) Session.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean internet = cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();

            notifyListeners(internet);
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getExtras() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                notifyListeners(true);
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                notifyListeners(false);
            }
        }
    }

    private void notifyListeners(boolean internet) {
        if (this.internet != internet) {
            if (internet) {
                listener.onOnline();
            } else {
                listener.onOffline();
            }
        }
    }

    public boolean isConnected() {
        return internet;
    }

    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    public void notify(ConnectionListener listener) {
        if (internet) {
            listener.onOnline();
        } else {
            listener.onOffline();
        }
    }

    private class ConnectionListenerImpl implements ConnectionListener {

        @Override
        public void onOnline() {
            Log.e("IRCDEBUG", "onOnline()");
            internet = true;
            for (ConnectionListener listener : listeners) {
                listener.onOnline();
            }
        }

        @Override
        public void onOffline() {
            Log.e("IRCDEBUG", "onOffline()");
            internet = false;
            for (ConnectionListener listener : listeners) {
                listener.onOffline();
            }
        }
    }

    public interface ConnectionListener {
        public void onOnline();
        public void onOffline();
    }
}