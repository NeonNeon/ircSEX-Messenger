package se.chalmers.dat255.ircsex.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
            if (listeners == null) {
                listeners = new ArrayList<ConnectionListener>();
            }
            started = true;

            ConnectivityManager cm =
                    (ConnectivityManager) Session.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            internet = cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();

            if (internet) {
                listener.onOnline();
            } else {
                listener.onOffline();
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

    public static void addListener(ConnectionListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<ConnectionListener>();
        }
        listeners.add(listener);
    }

    public static void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }

    public static void start() {
        new NetworkStateHandler();
    }

    public static void notify(ConnectionListener listener) {
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