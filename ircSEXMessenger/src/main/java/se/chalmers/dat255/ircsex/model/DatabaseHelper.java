package se.chalmers.dat255.ircsex.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class.
 *
 * Created by Oskar on 2013-09-18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_SERVERS = "servers";
    public static final String SERVER_ID = "id";
    public static final String SERVER_HOST = "host";
    public static final String SERVER_PORT = "port";
    public static final String SERVER_NICK = "nick";
    public static final String SERVER_LOGIN = "login";
    public static final String SERVER_REALNAME = "realname";
    public static final String SERVER_PASSWORD = "password";
    public static final String SERVER_USE_SSL = "useSsl";
    public static final String SERVER_USE_SSH = "useSsh";
    public static final String SERVER_SSH_HOSTNAME = "sshHostname";
    public static final String SERVER_SSH_USERNAME = "sshUsername";
    public static final String SERVER_SSH_PASSWORD = "sshPassword";

    public static final String TABLE_CHANNELS = "channels";
    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_SERVER = "server";
    public static final String CHANNEL_NAME = "name";

    public static final String TABLE_HIGHLIGHTS = "highlights";
    public static final String HIGHLIGHT_ID = "id";
    public static final String HIGHLIGHT_STRING = "string";

    private static final String DATABASE_NAME = "ircSEX.database";
    private static final int DATABASE_VERSION = 10;

    // Database creation sql statement
    private static final String TABLE_SERVERS_CREATE = "create table " + TABLE_SERVERS + "("
            + SERVER_ID + " integer primary key autoincrement, "
            + SERVER_HOST + " text, "
            + SERVER_PORT + " integer, "
            + SERVER_NICK + " text, "
            + SERVER_LOGIN + " text, "
            + SERVER_REALNAME + " text, "
            + SERVER_PASSWORD + " text, "
            + SERVER_USE_SSL + " integer, "
            + SERVER_USE_SSH + " integer, "
            + SERVER_SSH_HOSTNAME + " text, "
            + SERVER_SSH_USERNAME + " text, "
            + SERVER_SSH_PASSWORD + " text" +
            ");";
    private static final String TABLE_CHANNELS_CREATE = "create table " + TABLE_CHANNELS + "("
            + CHANNEL_ID + " integer primary key autoincrement, "
            + CHANNEL_SERVER + " text, "
            + CHANNEL_NAME + " text);";
    private static final String TABLE_HIGHLIGHTS_CREATE = "create table " + TABLE_HIGHLIGHTS + "("
            + HIGHLIGHT_ID + " integer primary key autoincrement, "
            + HIGHLIGHT_STRING + " text);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_SERVERS_CREATE);
        database.execSQL(TABLE_CHANNELS_CREATE);
        database.execSQL(TABLE_HIGHLIGHTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHLIGHTS);
        onCreate(db);
    }
}
