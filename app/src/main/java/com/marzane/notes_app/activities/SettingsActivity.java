package com.marzane.notes_app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.marzane.notes_app.R;
import com.marzane.notes_app.SettingsService;

import br.com.onimur.handlepathoz.BuildConfig;

public class SettingsActivity extends AppCompatActivity {

    private static SettingsService settingsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        settingsService = new SettingsService();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference languagePref = findPreference(getResources().getString(R.string.language_setting));

            if (languagePref != null) {
                languagePref.setOnPreferenceChangeListener((preference, newValue) -> {

                    String lang = newValue.toString();

                    settingsService.setLocale(lang, preference.getContext());
                    settingsService.setLanguageChangedFlag();

                    Intent intent = new Intent(preference.getContext(), SettingsActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                    return true; // Return true if the event is handled.
                });
            }


            SeekBarPreference fontSizeBar = findPreference(getResources().getString(R.string.font_size_setting));
            if(fontSizeBar != null){
                fontSizeBar.setOnPreferenceChangeListener((preference, newValue) -> {

                    return true;
                });
            }

            Preference version = findPreference(getResources().getString(R.string.app_version_setting));
            if(version != null){
                version.setSummary(BuildConfig.VERSION_NAME);
            }

        }


    }

}

// app:
// String AppLang = Resources.getConfiguration().locale.getLanguage();

// system:
// String DeviceLang = Resources.getSystem().getConfiguration().locale.getLanguage();