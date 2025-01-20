package com.marzane.notes_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class SettingsService {

    private boolean auto_save_current_file = false;
    private int fontSize;
    private String language;

    private static boolean languageWasChanged = false;


    public SettingsService() {}


    public int getFontSize(Context context) {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        fontSize = sharedPreferences.getInt(context.getResources().getString(R.string.font_size_setting), 17);
        return fontSize;
    }


    public String getLanguage(Context context) {
        String DeviceLang = context.getResources().getSystem().getConfiguration().locale.getLanguage();
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        language = sharedPreferences.getString(context.getResources().getString(R.string.language_setting), DeviceLang);
        return language;
    }

    public boolean isAutosavingActive(Context context) {
        boolean isActive = false;

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        isActive = sharedPreferences.getBoolean(context.getResources().getString(R.string.autosave_setting), false);

        return isActive;
    }

    public boolean isToolbarActive(Context context) {
        boolean isActive = false;

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        isActive = sharedPreferences.getBoolean(context.getResources().getString(R.string.show_toolbar_setting), true);

        return isActive;
    }

     public  void setLanguageChangedFlag() {
        languageWasChanged = true;
    }

     public  boolean isLanguageWasChanged() {
        boolean value = languageWasChanged;
        languageWasChanged = false;
        return value;
    }

    public void setLocale(String lang, Context context) {

        if(lang.equals("default")){
            lang = Resources.getSystem().getConfiguration().locale.getLanguage();
        }

        Locale locale2 = new Locale(lang);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;

        context.getResources().updateConfiguration(config2, null);
    }


    private void setSettingValue(String name, String value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.apply();
    }

    private void setSettingValue(String name, int value, Context context) {
        SharedPreferences settings = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    private void setSettingValue(String name, boolean value, Context context) {
        SharedPreferences settings = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }


}
