package com.marzane.notes_app.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.task.ListAllNotesTask;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Utils.FileUtil;
import com.marzane.notes_app.adapters.NoteCustomAdapter;
import com.marzane.notes_app.customDialogs.CustomDialogClass;
import com.marzane.notes_app.models.NoteModel;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    private TaskRunner taskRunner = new TaskRunner();
    private RecyclerView rvNoteList;
    private ArrayList<NoteModel> arrayRecentNotes = new ArrayList<>();
    private int screen_width;
    private int screen_height;
    private Resources resources;
    private CustomDialogClass cdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkStoragePermissions()) requestForStoragePermissions();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        resources = getResources();

        rvNoteList = findViewById(R.id.rv_recent_notes);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

    }


    @Override
    protected void onStart() {
        // initialize recent notes list
        taskRunner.executeAsync(new ListAllNotesTask(this), (data) -> {
            arrayRecentNotes = data;
            NoteCustomAdapter noteCustomAdapter = new NoteCustomAdapter(arrayRecentNotes, MainActivity.this, resources);
            rvNoteList.setLayoutManager(new GridLayoutManager(this, screen_width/350));
            rvNoteList.setAdapter(noteCustomAdapter);

        });

        super.onStart();
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

        if(id == R.id.new_file){
            loadEditorView();
            return true;

        } else if (id == R.id.open_file) {
            FileUtil.openFileIntent(this);
            return true;

        } else if(id == R.id.close_app){
            this.finishAffinity(); // cierra la app por completo (todas las activities)
            return true;

        } else if(id == R.id.clear_list){
            cdd = new CustomDialogClass(this, resources.getString(R.string.dialog_clear_list), ActionValues.CLEAR_LIST.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
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
    // https://medium.com/@kezzieleo/manage-external-storage-permission-android-studio-java-9c3554cf79a7

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
    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    o -> {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                            //Android is 11 (R) or above
                            if(Environment.isExternalStorageManager()){
                                //Manage External Storage Permissions Granted
                                Log.d(TAG, "onActivityResult: Manage External Storage Permissions Granted");
                            }else{
                                Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                                this.finishAffinity();
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
                    Toast.makeText(MainActivity.this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
            }
        }
    }


}

