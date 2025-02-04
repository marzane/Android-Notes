package com.marzane.notepad.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.marzane.notepad.R;
import com.marzane.notepad.SettingsService;

import java.util.Locale;



public class SettingsActivity extends AppCompatActivity {

    private static SettingsService settingsService;
    private Locale locale;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("LOCALE", locale);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        settingsService = new SettingsService();
        if (savedInstanceState == null) {
            locale = new Locale(settingsService.getLanguage(this));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        } else {
            locale = (Locale) savedInstanceState.getSerializable("LOCALE");
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
                try{
                    String versionName = getContext().getPackageManager()
                            .getPackageInfo(getContext().getPackageName(), 0).versionName;;
                    version.setSummary(versionName);
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            Preference about = findPreference(getResources().getString(R.string.about_setting));
            if(about != null){
                about.setOnPreferenceClickListener((preference) -> {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_web_app)));
                    startActivity(browserIntent);

                    return true;
                });
            }

        }


    }

}

// app:
// String AppLang = Resources.getConfiguration().locale.getLanguage();

// system:
// String DeviceLang = Resources.getSystem().getConfiguration().locale.getLanguage();