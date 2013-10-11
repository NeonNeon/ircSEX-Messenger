package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import se.chalmers.dat255.ircsex.R;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-10
 */
public class SettingsActivity extends Activity {
    public static final String PREF_SHOW_JOIN_LEAVE = "pref_show_join_leave";
    public static final String PREF_MESSAGE_FONT_SIZE = "pref_message_font_size";
    public static final String PREF_HIGHLIGHT = "pref_highlight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.action_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
