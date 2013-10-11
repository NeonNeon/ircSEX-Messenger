package se.chalmers.dat255.ircsex.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.chalmers.dat255.ircsex.model.database.ContextManager;
import se.chalmers.dat255.ircsex.model.database.DatabaseHelper;

/**
 * Database adapter for servers table.
 *
 * Created by Oskar on 2013-09-18.
 */
public class ServerDAO {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.SERVER_ID,
            DatabaseHelper.SERVER_HOST,
            DatabaseHelper.SERVER_PORT,
            DatabaseHelper.SERVER_NICK,
            DatabaseHelper.SERVER_LOGIN,
            DatabaseHelper.SERVER_REALNAME,
            DatabaseHelper.SERVER_PASSWORD,
            DatabaseHelper.SERVER_USE_SSL,
            DatabaseHelper.SERVER_USE_SSH,
            DatabaseHelper.SERVER_SSH_HOSTNAME,
            DatabaseHelper.SERVER_SSH_USERNAME,
            DatabaseHelper.SERVER_SSH_PASSWORD
    };

    /**
     * Creates an object of ServerDAO.
     */
    public ServerDAO() {
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
     * @param data
     */
    public void addServer(ServerConnectionData data) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SERVER_HOST, data.getServer());
        values.put(DatabaseHelper.SERVER_PORT, data.getPort());
        values.put(DatabaseHelper.SERVER_LOGIN, data.getLogin());
        values.put(DatabaseHelper.SERVER_NICK, data.getNickname());
        values.put(DatabaseHelper.SERVER_REALNAME, data.getRealname());
        values.put(DatabaseHelper.SERVER_PASSWORD, data.getPassword());
        values.put(DatabaseHelper.SERVER_USE_SSL, data.isUsingSsl());
        values.put(DatabaseHelper.SERVER_USE_SSH, data.isUsingSsh());
        values.put(DatabaseHelper.SERVER_SSH_HOSTNAME, data.getSshHostname());
        values.put(DatabaseHelper.SERVER_SSH_USERNAME, data.getSshUsername());
        values.put(DatabaseHelper.SERVER_SSH_PASSWORD, data.getSshPassword());
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
     * Updates the saved nickname.
     *
     * @param host Server to change nickname on
     * @param nick New Nickname
     */
    public void updateNickname(String host, String nick) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SERVER_NICK, nick);
        database.update(DatabaseHelper.TABLE_SERVERS, values, DatabaseHelper.SERVER_HOST +"='"+ host +"'", null);
    }

    /**
     * Returns all saved servers.
     *
     * @return Servers as a Map with address as key and server as value
     */
    public Map<String, IrcServer> getAllIrcServers() {
        Map<String, IrcServer> servers = new ConcurrentHashMap<String, IrcServer>();

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
        ServerConnectionData data = new ServerConnectionData(
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getInt(7) == 1,
                cursor.getInt(8) == 1,
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11)
        );
        return new IrcServer(data);
    }

    public void drop() {
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_SERVERS);
    }
}