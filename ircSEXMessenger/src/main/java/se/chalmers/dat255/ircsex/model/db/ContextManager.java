package se.chalmers.dat255.ircsex.model.db;

import android.content.Context;

/**
 * Created by Oskar on 2013-09-18.
 */
public final class ContextManager {
    public static Context SERVER_CONTEXT;
    public static Context CHANNEL_CONTEXT;

    public static boolean isLoaded() {
        return SERVER_CONTEXT != null && CHANNEL_CONTEXT != null;
    }
}
