package se.chalmers.dat255.ircsex.model.db;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import se.chalmers.dat255.ircsex.model.IrcServer;

public class IrcServerDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.SERVER_ID,
            MySQLiteHelper.SERVER_HOST,
            MySQLiteHelper.SERVER_PORT,
            MySQLiteHelper.SERVER_LOGIN,
            MySQLiteHelper.SERVER_NICK,
            MySQLiteHelper.SERVER_REALNAME};

    public IrcServerDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addServer(String host, int port, String login, String nick, String realname) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.SERVER_HOST, host);
        values.put(MySQLiteHelper.SERVER_PORT, port);
        values.put(MySQLiteHelper.SERVER_LOGIN, login);
        values.put(MySQLiteHelper.SERVER_NICK, nick);
        values.put(MySQLiteHelper.SERVER_REALNAME, realname);
        long insertId = database.insert(MySQLiteHelper.TABLE_SERVERS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SERVERS,
                allColumns, MySQLiteHelper.SERVER_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void removeServer(String host) {
        database.delete(MySQLiteHelper.TABLE_SERVERS, MySQLiteHelper.SERVER_HOST
                + " = " + host, null);
    }

    public Map<String, IrcServer> getAllIrcServers() {
        Map<String, IrcServer> servers = new HashMap<String, IrcServer>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SERVERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            IrcServer server = cursorToServer(cursor);
            servers.put(server.getHost(), server);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return servers;
    }

    private IrcServer cursorToServer(Cursor cursor) {
        IrcServer server = new IrcServer(   cursor.getString(1),
                                            cursor.getInt(2),
                                            cursor.getString(3),
                                            cursor.getString(4),
                                            cursor.getString(5));
        return server;
    }
}