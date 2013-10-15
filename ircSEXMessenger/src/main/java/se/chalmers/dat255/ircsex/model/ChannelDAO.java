package se.chalmers.dat255.ircsex.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Database adapter for channels table.
 *
 * Created by Oskar on 2013-09-18.
 */
public class ChannelDAO {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.CHANNEL_ID,
            DatabaseHelper.CHANNEL_SERVER,
            DatabaseHelper.CHANNEL_NAME};

    /**
     * Creates an object of ChannelDAO.
     */
    public ChannelDAO() {
        dbHelper = new DatabaseHelper(Session.context);
    }

    /**
     * Opens an SQL connection.
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes an SQL connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Adds a channel to the channel table.
     *
     * @param server - Server which the channel is on
     * @param name - Name of the channel
     */
    public void addChannel(String server, String name) {
        if (!getIrcChannelsByServer(server).contains(name)) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CHANNEL_SERVER, server);
            values.put(DatabaseHelper.CHANNEL_NAME, name);
            long insertId = database.insert(DatabaseHelper.TABLE_CHANNELS, null, values);
            Cursor cursor = database.query(DatabaseHelper.TABLE_CHANNELS,
                    allColumns, DatabaseHelper.CHANNEL_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
    }

    /**
     * Removes a channel from the channel table.
     *
     * @param channelName - Name of the channel to remove
     */
    public void removeChannel(String channelName) {
        database.delete(DatabaseHelper.TABLE_CHANNELS, DatabaseHelper.CHANNEL_NAME
                + " = '" + channelName + "'", null);
    }

    /**
     * Returns all connected channels on the current server.
     *
     * @return Channels as a Map with name as key and channel as value
     */
    public List<String> getIrcChannelsByServer(String server) {
        Set<String> channels = new HashSet<String>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_CHANNELS,
                allColumns, DatabaseHelper.CHANNEL_SERVER + " = '" + server +"'", null, null, null, DatabaseHelper.CHANNEL_ID);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            channels.add(cursorToChannel(cursor));
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return new ArrayList<String>(channels);
    }

    private String cursorToChannel(Cursor cursor) {
        return cursor.getString(2);
    }

    public void drop() {
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_CHANNELS);
    }
}