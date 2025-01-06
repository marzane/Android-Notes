package com.marzane.notes_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.Locale;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.marzane.notes_app.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
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

            ListPreference languagePref = findPreference("language");

            if (languagePref != null) {
                languagePref.setOnPreferenceChangeListener((preference, newValue) -> {

                    String lang = Resources.getSystem().getConfiguration().locale.getLanguage();

                    if(!newValue.toString().equals("default")){
                        lang = newValue.toString();
                    }

                    setLocale(preference.getContext(), lang);

                    Intent intent = new Intent(preference.getContext(), SettingsActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                    return true; // Return true if the event is handled.
                });
            }


            SeekBarPreference fontSizeBar = findPreference("fontSize");
            if(fontSizeBar != null){
                fontSizeBar.setOnPreferenceChangeListener((preference, newValue) -> {
                    Toast.makeText(getContext(), newValue.toString(), Toast.LENGTH_SHORT).show();
                    /*
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("fontSize", newValue.toString());
                    editor.apply();
                    */
                    return true;
                });
            }

        }

        public void setLocale(Context context, String lang) {
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }


    }

}

// app:
// String AppLang = Resources.getConfiguration().locale.getLanguage();

// system:
// String DeviceLang = Resources.getSystem().getConfiguration().locale.getLanguage();