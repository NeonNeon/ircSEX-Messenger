package se.chalmers.dat255.ircsex.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.chalmers.dat255.ircsex.model.database.DatabaseHelper;

/**
 * Created by Oskar on 2013-10-10.
 */
public class HighlightDAO {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = { DatabaseHelper.HIGHLIGHT_STRING };

    /**
     * Creates an object of HighlightDAO.
     */
    public HighlightDAO() {
        dbHelper = new DatabaseHelper(Session.context);
    }

    /**
     * Opens an SQL connection.
     *
     * @throws android.database.SQLException
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
     * Adds a highlight to the highlight table.
     *
     * @param string - New highlight
     */
    public void addHighlight(String string) {
        if (!getHighlights().contains(string)) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.HIGHLIGHT_STRING, string);
            long insertId = database.insert(DatabaseHelper.TABLE_HIGHLIGHTS, null, values);
            Cursor cursor = database.query(DatabaseHelper.TABLE_HIGHLIGHTS,
                    allColumns, DatabaseHelper.HIGHLIGHT_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
    }

    /**
     * Removes a highlight from the highlight table.
     *
     * @param string - Highlight to remove
     */
    public void removeHighlight(String string) {
        database.delete(DatabaseHelper.TABLE_HIGHLIGHTS, DatabaseHelper.HIGHLIGHT_STRING
                + " = '" + string + "'", null);
    }

    /**
     * Returns all highlights.
     *
     * @return List of all highlights
     */
    public List<String> getHighlights() {
        Set<String> highlights = new HashSet<String>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_HIGHLIGHTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            highlights.add(cursorToHighlight(cursor));
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return new ArrayList<String>(highlights);
    }

    private String cursorToHighlight(Cursor cursor) {
        return cursor.getString(0);
    }

    public void drop() {
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_HIGHLIGHTS);
    }
}
