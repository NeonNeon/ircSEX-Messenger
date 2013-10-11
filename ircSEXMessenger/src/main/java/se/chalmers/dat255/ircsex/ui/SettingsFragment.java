package se.chalmers.dat255.ircsex.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.IrcServer;
import se.chalmers.dat255.ircsex.model.Session;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-10
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(SettingsActivity.PREF_MESSAGE_FONT_SIZE)) {
            findPreference(s).setSummary(sharedPreferences.getString(SettingsActivity.PREF_MESSAGE_FONT_SIZE, ""));
        }
        else if (s.equals(SettingsActivity.PREF_HIGHLIGHT)) {
            IrcServer ircServer = Session.getInstance().getActiveServer();
            List<String> databaseHighlights = ircServer.getHighlightWords();
            String[] highlightArray = sharedPreferences.getString(SettingsActivity.PREF_HIGHLIGHT, "").split(";");
            List<String> highlights = Arrays.asList(highlightArray);
            for (String highlight : highlightArray) {
                highlight = highlight.trim();
                if (!databaseHighlights.contains(highlight)) {
                    Log.e("IRCDEBUG", "ADDED HIGHLIGHT" + highlight);
                    ircServer.addHighlight(highlight);
                }
            }
            List<String> highlightsToBeRemoved = new ArrayList<String>();
            for (String highlight : databaseHighlights) {
                if (!highlights.contains(highlight) && !highlight.equals(ircServer.getUser().getNick())) {
                    Log.e("IRCDEBUG", "REMOVED HIGHLIGHT" + highlight);
                    highlightsToBeRemoved.add(highlight);
                }
            }
            for (String highlightToBeRemoved : highlightsToBeRemoved) {
                ircServer.removeHighlight(highlightToBeRemoved);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
