package se.chalmers.dat255.ircsex.model.db;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import se.chalmers.dat255.ircsex.model.IrcChannel;

public class IrcChannelDataSource {

    private SQLiteDatabase database;
    private final MySQLiteHelper dbHelper;
    private final String[] allColumns = { MySQLiteHelper.CHANNEL_ID,
            MySQLiteHelper.CHANNEL_SERVER,
            MySQLiteHelper.CHANNEL_NAME};

    public IrcChannelDataSource() {
        dbHelper = new MySQLiteHelper(ContextManager.CHANNEL_CONTEXT);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addChannel(String server, String name) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CHANNEL_SERVER, server);
        values.put(MySQLiteHelper.CHANNEL_NAME, name);
        long insertId = database.insert(MySQLiteHelper.TABLE_CHANNELS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHANNELS,
                allColumns, MySQLiteHelper.CHANNEL_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void removeChannel(String channelName) {
        database.delete(MySQLiteHelper.TABLE_CHANNELS, MySQLiteHelper.CHANNEL_NAME
                + " = " + channelName, null);
    }

    public Map<String, IrcChannel> getAllIrcChannels() {
        Map<String, IrcChannel> channels = new HashMap<String, IrcChannel>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHANNELS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IrcChannel channel = cursorToChannel(cursor);
            channels.put(channel.getChannelName(), channel);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return channels;
    }

    private IrcChannel cursorToChannel(Cursor cursor) {
        IrcChannel channel = new IrcChannel(cursor.getString(2));
        return channel;
    }
}