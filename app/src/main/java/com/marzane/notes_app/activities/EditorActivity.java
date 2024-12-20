package com.marzane.notes_app.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Threads.task.InsertOrUpdateFile;
import com.marzane.notes_app.Threads.task.deleteByPathTask;
import com.marzane.notes_app.Utils.FileUtil;
import com.marzane.notes_app.models.NoteModel;

import java.time.LocalDateTime;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;

public class EditorActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri{

    private static final int OPEN_FILE_PROVIDER = 1;
    private static final int SAVE_FILE_AS = 2;
    private static final int NEW_FILE = 3;
    private static final int CLOSE_APP = 4;

    private Intent intent;
    private String texto;  // el texto que contiene el archivo
    private NoteModel note;
    private Resources resources;

    // layout elements
    private EditText etEditor;

    private static HandlePathOz handlePathOz;
    private static TaskRunner taskRunner = new TaskRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        note = new NoteModel();

        // initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_editor);
        setSupportActionBar(toolbar);

        resources = getResources();

        handlePathOz = new HandlePathOz(this, this);

        etEditor = findViewById(R.id.et_editor);  // editText donde se escribe el contenido del archivo
        etEditor.addTextChangedListener(textWatcher);
        etEditor.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        // si no tengo ningun titulo para el archivo le pongo uno por defecto Ej: "nueva_nota.txt"
        if(note.getTitle() == null) {
            note.setTitle(resources.getString(R.string.default_title));

            // se muestra el nombre del archivo en la pantalla (textView)
            getSupportActionBar().setTitle(note.getTitle());
        }


        intent = getIntent();
        Bundle b = intent.getExtras();  // en el caso de querer abrir un archivo que ya existe
                                        // deberia recibir la uri
        if(b!=null)
        {
            Uri uriFile = (Uri) b.get(resources.getString(R.string.extra_intent_uri_file));

            if(uriFile != null) {  // obtengo la ruta real y el nombre del archivo

                getContentResolver().takePersistableUriPermission(uriFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uriFile, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                note.setPath(uriFile);
                texto = FileUtil.readFile(uriFile, this);

                if(texto == null){
                    taskRunner.executeAsync(new deleteByPathTask(this, uriFile.toString()), (dataResult) -> {
                        Toast.makeText(this, dataResult + "", Toast.LENGTH_LONG).show();
                    });
                    finish();                                                  // close this activity
                } else {
                    etEditor.setText(texto);
                    handlePathOz.getRealPath(uriFile);
                }

            }

        }

    }


    private void saveFileAs() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT); // cargar el selector de archivos
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, note.getTitle());

        // opcional, especifica la ubicacion que deberia abrirse al crear el archivo
        if(note.getPath() != null){
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, note.getPath());
        }

        startActivityForResult(intent, SAVE_FILE_AS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SAVE_FILE_AS) {  // si lo que se ha hecho es crear un archivo
            texto = etEditor.getText().toString();
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (intent != null && intent.getData() != null) {
                        Uri uri = intent.getData();
                        note.setPath(uri);
                        handlePathOz.getRealPath(note.getPath());
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        if(note.getRealPath() == null){
                            FileUtil.writeFile(note.getPath(), texto, this);
                        } else {
                            FileUtil.overwriteFile(note.getRealPath(), texto, this);
                        }

                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        } else if(requestCode == OPEN_FILE_PROVIDER){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (intent != null && intent.getData() != null) {

                        Uri uri = intent.getData();

                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        note.setPath(uri);
                        handlePathOz.getRealPath(uri);

                        texto = FileUtil.readFile(uri, this);
                        etEditor.setText(texto);

                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }


    public void onRequestHandlePathOz(@NonNull PathOz pathOz, Throwable tr) {

        String rutaRealArchivo = pathOz.getPath();
        String nombreArchivo = rutaRealArchivo.substring(rutaRealArchivo.lastIndexOf("/") + 1);

        getSupportActionBar().setTitle(nombreArchivo);
        note.setTitle(nombreArchivo);
        note.setRealPath(pathOz.getPath());

        if(!rutaRealArchivo.isEmpty()) {
            Toast.makeText(this, "opening file " + pathOz.getPath(), Toast.LENGTH_SHORT).show();
            note.setlastOpened(LocalDateTime.now());
            taskRunner.executeAsync(new InsertOrUpdateFile(this, note), (dataResult) -> {});
        }

        //Handle Exception (Optional)
        if (tr != null) {
            Toast.makeText(this, tr.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.save_file_as){  // save as
            saveFileAs();
            return true;

        } else if (id == R.id.save_file){   // overwrite existing file or create it

            if (note.getPath() != null) {
                texto = etEditor.getText().toString();
                boolean result = FileUtil.overwriteFile(note.getRealPath(), texto, this);
                if(result) getSupportActionBar().setTitle(note.getTitle());
            } else {
                saveFileAs();
            }
            return true;

        } else if (id == R.id.close_app){

            onCreateDialogClose(resources.getString(R.string.dialog_close_app), resources.getString(R.string.dialog_button_close_app), resources.getString(R.string.dialog_button_cancel), CLOSE_APP).show();
            return true;

        } else if(id == R.id.open_file) {
            onCreateDialogClose(resources.getString(R.string.dialog_open_file), resources.getString(R.string.dialog_button_open_file), resources.getString(R.string.dialog_button_cancel), OPEN_FILE_PROVIDER).show();
            return true;

        } else if(id == R.id.new_file_editor){
            onCreateDialogClose(resources.getString(R.string.dialog_new_file), resources.getString(R.string.dialog_button_new_file), resources.getString(R.string.dialog_button_cancel), NEW_FILE).show();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }

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

        startActivityForResult(intent, MainActivity.OPEN_FILE_PROVIDER);
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            getSupportActionBar().setTitle(note.getTitle() + "*");

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };


    public Dialog onCreateDialogClose(String message, String positive, String negative, int action) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(positive, (dialog, id) -> {
                    switch (action){
                        case CLOSE_APP:
                            this.finishAffinity();
                            break;
                        case OPEN_FILE_PROVIDER:
                            openFileIntent();
                            break;
                        case NEW_FILE:
                            Intent intentNew = new Intent(this, EditorActivity.class);
                            startActivity(intentNew);
                            finish();
                            break;
                    }

                })
                .setNegativeButton(negative, (dialog, id) -> {
                    // User cancels the dialog.
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }


}