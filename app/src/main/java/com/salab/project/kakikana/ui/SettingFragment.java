package com.salab.project.kakikana.ui;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.widget.Toast;

import com.salab.project.kakikana.R;

/**
 * Settings for app customization.
 **/
public class SettingFragment extends PreferenceFragmentCompat {

    // constants
    private static final String TAG = SettingFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);

        // setting summary of the Language option list
        // ref: https://developer.android.com/guide/topics/ui/settings/customize-your-settings
        ListPreference langPreference = findPreference(getString(R.string.pref_key_lang));
        if (langPreference != null) {
            langPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        }

        // setup callback for link with google account action
        Preference actionLinkGoogle = findPreference(getString(R.string.pref_key_action_link_google));
        if (actionLinkGoogle != null){
            actionLinkGoogle.setOnPreferenceClickListener(preference -> {
                // TODO: implement real code to link anonymous account with google
                Toast.makeText(requireContext(), "action link google", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

    }
}