package se.chalmers.dat255.ircsex.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import se.chalmers.dat255.ircsex.R;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-10
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
