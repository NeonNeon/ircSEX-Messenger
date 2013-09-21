package se.chalmers.dat255.ircsex.model.database;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import se.chalmers.dat255.ircsex.model.IrcServer;

/**
 * Database adapter for servers table.
 *
 * Created by Oskar on 2013-09-18.
 */
public class ServerDatabaseAdapter {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.SERVER_ID,
            DatabaseHelper.SERVER_HOST,
            DatabaseHelper.SERVER_PORT,
            DatabaseHelper.SERVER_LOGIN,
            DatabaseHelper.SERVER_NICK,
            DatabaseHelper.SERVER_REALNAME};

    /**
     * Creates an object of ServerDatabaseAdapter.
     */
    public ServerDatabaseAdapter() {
        dbHelper = new DatabaseHelper(ContextManager.SERVER_CONTEXT);
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
     * Adds a server to the servers table.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     * @param realname - IRL Name
     */
    public void addServer(String host, int port, String login, String nick, String realname) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SERVER_HOST, host);
        values.put(DatabaseHelper.SERVER_PORT, port);
        values.put(DatabaseHelper.SERVER_LOGIN, login);
        values.put(DatabaseHelper.SERVER_NICK, nick);
        values.put(DatabaseHelper.SERVER_REALNAME, realname);
        long insertId = database.insert(DatabaseHelper.TABLE_SERVERS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVERS,
                allColumns, DatabaseHelper.SERVER_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    /**
     * Removes a server from the servers table.
     *
     * @param host - Address to the server to remove
     */
    public void removeServer(String host) {
        database.delete(DatabaseHelper.TABLE_SERVERS, DatabaseHelper.SERVER_HOST
                + " = '" + host + "'", null);
    }

    /**
     * Returns all saved servers.
     *
     * @return Servers as a Map with address as key and server as value
     */
    public Map<String, IrcServer> getAllIrcServers() {
        Map<String, IrcServer> servers = new HashMap<String, IrcServer>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVERS,
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