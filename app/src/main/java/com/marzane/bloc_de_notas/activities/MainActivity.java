package com.marzane.bloc_de_notas.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.marzane.bloc_de_notas.R;
import com.marzane.bloc_de_notas.Threads.task.ListAllNotesTask;
import com.marzane.bloc_de_notas.Threads.TaskRunner;
import com.marzane.bloc_de_notas.adapters.NoteCustomAdapter;
import com.marzane.bloc_de_notas.models.NoteModel;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    public static final int OPEN_FILE_PROVIDER = 1;
    public static final int OPEN_FILE = 2;

    private TaskRunner taskRunner = new TaskRunner();
    private RecyclerView rvNoteList;
    private ArrayList<NoteModel> arrayRecentNotes = new ArrayList<>();
    private int screen_width;
    private int screen_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkStoragePermissions()) requestForStoragePermissions();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        rvNoteList = findViewById(R.id.rv_recent_notes);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

    }


    @Override
    protected void onStart() {
        // preparando el listado de notas recientes en un hilo
        taskRunner.executeAsync(new ListAllNotesTask(this), (data) -> {
            arrayRecentNotes = data;

            NoteCustomAdapter noteCustomAdapter = new NoteCustomAdapter(arrayRecentNotes);
            rvNoteList.setLayoutManager(new GridLayoutManager(this, screen_width/350));
            rvNoteList.setAdapter(noteCustomAdapter);

        });

        super.onStart();
    }



    // open file button listener
    public void openFileIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, OPEN_FILE_PROVIDER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == OPEN_FILE_PROVIDER){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (intent != null && intent.getData() != null) {

                        Uri uri = intent.getData();

                        Intent intentEditor = new Intent(this, EditorActivity.class);
                        intentEditor.putExtra("@string/extra_intent_uri_file", uri);
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
            openFileIntent();
            return true;
        } else if(id == R.id.close_app){
            // TODO: cerrar app con mensaje de confirmacion
            this.finishAffinity(); // cierra la app por completo (todas las activities)
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        /*
        switch (item.getItemId()) {
            case R.id.open_file:
                openFileIntent();
                return true;

            case R.id.new_file:
                loadEditorView();
                return true;

            default:
                // The user's action isn't recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        */
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

