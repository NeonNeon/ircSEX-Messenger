package se.chalmers.dat255.ircsex.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.model.database.ContextManager;

/**
 * Created by Oskar on 2013-10-02.
 */
public class NetworkStateHandler extends BroadcastReceiver {

    private static boolean started;
    private static boolean internet;
    private ConnectionListener listener;
    private static List<ConnectionListener> listeners;

    public NetworkStateHandler() {
        listener = new ConnectionListenerImpl();

        if (!started) {
            listeners = new ArrayList<ConnectionListener>();
            try {
                ConnectivityManager cm =
                        (ConnectivityManager) ContextManager.SERVER_CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE);
                internet = cm.getActiveNetworkInfo().isConnectedOrConnecting();
            } catch (NullPointerException e) {
                internet = false;
            } finally {
                started = true;

                if (internet) {
                    listener.onOnline();
                } else {
                    listener.onOffline();
                }
            }
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                if (!internet) {
                    listener.onOnline();
                }
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                if (internet) {
                    listener.onOffline();
                }
            }
        }
    }

    public static boolean isConnected() {
        return internet;
    }

    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    private class ConnectionListenerImpl implements ConnectionListener {

        @Override
        public void onOnline() {
            internet = true;
            for (ConnectionListener listener : listeners) {
                listener.onOnline();
            }
        }

        @Override
        public void onOffline() {
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