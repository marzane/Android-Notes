package com.marzane.notepad.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

        if(locale != null)
            settingsService.setLocale(locale.getLanguage(), this);

        setContentView(R.layout.activity_main);


        if(!checkStoragePermissions()){
            requestForStoragePermissions();
        }

        resources = getResources();
        createDialog = new CreateDialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        rvNoteList = findViewById(R.id.rv_recent_notes);


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



    // - STORAGE PERMISSIONS -

    // This code checks if Storage Permissions have been granted and returns a boolean:
    public boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }


    private static final int STORAGE_PERMISSION_CODE = 23;

    // Request For Storage Permissions
    private void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            }catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        }else{
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }

    }

    // Handle Permission Request Result
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    o -> {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                            //Android is 11 (R) or above
                            if(Environment.isExternalStorageManager()){
                                //Manage External Storage Permissions Granted
                                Log.d(TAG, "onActivityResult: Manage External Storage Permissions Granted");
                                Toast.makeText(this, resources.getString(R.string.permission_storage_granted), Toast.LENGTH_SHORT).show();
                            }else{
                                //Manage External Storage Permissions denied
                                createDialog.information(resources.getString(R.string.permission_storage_denied_main), ActionValues.NOACTION.getID());
                            }
                        }else{
                            //Below android 11
                        }
                    });

    // To handle permission request results for Android Versions below 11:
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0){
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(read && write){
                    Toast.makeText(this, resources.getString(R.string.permission_storage_granted), Toast.LENGTH_SHORT).show();

                }else{
                    createDialog.information(resources.getString(R.string.permission_storage_denied_main), ActionValues.NOACTION.getID());
                }
            }
        }
    }

}

