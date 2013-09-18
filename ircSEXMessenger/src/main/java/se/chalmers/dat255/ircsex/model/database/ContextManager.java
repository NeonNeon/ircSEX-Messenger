package se.chalmers.dat255.ircsex.model.database;

import android.content.Context;

/**
 * Manages Contexts to prevent sending them through all model-classes.
 *
 * Created by Oskar on 2013-09-18.
 */
public final class ContextManager {
    public static Context SERVER_CONTEXT;
    public static Context CHANNEL_CONTEXT;

    /**
     * Checks if all context-variables are initialized.
     *
     * @return If context-variables are set
     */
    public static boolean isLoaded() {
        return SERVER_CONTEXT != null && CHANNEL_CONTEXT != null;
    }
}
