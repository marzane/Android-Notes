package com.marzane.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notepad.ActionValues;
import com.marzane.notepad.GridAutofitLayoutManager;
import com.marzane.notepad.R;
import com.marzane.notepad.SettingsService;
import com.marzane.notepad.Utils.CreateDialog;
import com.marzane.notepad.adapters.NoteCustomAdapter;
import com.marzane.notepad.customDialogs.CustomDialogYesNo;
import com.marzane.notepad.Threads.TaskRunner;
import com.marzane.notepad.Threads.task.ListAllNotesTask;
import com.marzane.notepad.Utils.FileUtil;
import com.marzane.notepad.Utils.RecyclerViewNotesManager;

import java.util.Locale;


public class MainActivity extends AppCompatActivity{

    private TaskRunner taskRunner = new TaskRunner();
    private RecyclerView rvNoteList;
    private int screen_width, screen_height;
    private Resources resources;
    private CustomDialogYesNo cd;
    private SettingsService settingsService;
    private Locale locale;
    private CreateDialog createDialog;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("LOCALE", locale);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsService = new SettingsService();

        if (savedInstanceState != null) {
            locale = (Locale) savedInstanceState.getSerializable("LOCALE");
        } else {
            locale = new Locale(settingsService.getLanguage(this));
        }
        settingsService.setLocale(locale.getLanguage(), this);

        setContentView(R.layout.activity_main);

        resources = getResources();
        createDialog = new CreateDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        rvNoteList = findViewById(R.id.rv_recent_notes);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;


        // initialize recent notes list
        taskRunner.executeAsync(new ListAllNotesTask(this), (data) -> {

            NoteCustomAdapter noteCustomAdapter = new NoteCustomAdapter(data,MainActivity.this);
            rvNoteList.setLayoutManager(new GridAutofitLayoutManager(this, 320));
            rvNoteList.setAdapter(noteCustomAdapter);

            RecyclerViewNotesManager.setDataList(data);
            RecyclerViewNotesManager.setRecyclerView(rvNoteList);

            RecyclerViewNotesManager.updateRecyclerViewVisibility();
        });

    }


    @Override
    protected void onResume() {

        if(settingsService.isLanguageWasChanged()){
            locale = new Locale(settingsService.getLanguage(this));
            this.recreate();
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == ActionValues.OPEN_FILE_PROVIDER.getID()){
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (intent != null && intent.getData() != null) {

                            Uri uri = intent.getData();

                            Intent intentEditor = new Intent(this, EditorActivity.class);
                            intentEditor.putExtra(resources.getString(R.string.extra_intent_uri_file), uri);
                            startActivity(intentEditor);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
        }
    }


    // carga la pantalla del editor
    private void loadEditorView(){
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.new_file){    // open EditorActivity without extras
            loadEditorView();
            return true;

        } else if (id == R.id.open_file) {
            FileUtil.openFileIntent(this);
            return true;

        } else if(id == R.id.close_app){
            this.finishAffinity(); // cierra la app por completo (todas las activities)
            return true;

        } else if(id == R.id.clear_list){
            createDialog.yesNo(resources.getString(R.string.dialog_clear_list), ActionValues.CLEAR_LIST.getID());
            return true;

        }else if(id == R.id.settings) {  // open settings
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }

    }


}

