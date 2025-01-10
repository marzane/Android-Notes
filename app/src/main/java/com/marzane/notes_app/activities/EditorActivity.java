package com.marzane.notes_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.Utils.MyClipboardManager;
import com.marzane.notes_app.Utils.TextViewUndoRedo;
import com.marzane.notes_app.Utils.RecyclerViewNotesManager;
import com.marzane.notes_app.customDialogs.CustomDialogClass;
import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Threads.task.InsertOrUpdateFile;
import com.marzane.notes_app.Threads.task.deleteByPathTask;
import com.marzane.notes_app.Utils.FileUtil;
import com.marzane.notes_app.customDialogs.CustomDialogFileInfo;
import com.marzane.notes_app.models.NoteModel;

import java.time.LocalDateTime;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;

public class EditorActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri{

    private Intent intent;
    private String texto;  // el texto que contiene el archivo
    private int fontSize;
    private NoteModel note;
    private Resources resources;
    private TextViewUndoRedo textViewUndoRedo;
    private MyClipboardManager myClipboardManager = new MyClipboardManager();

    // layout elements
    private EditText etEditor;
    private MenuItem saveButton;

    private static HandlePathOz handlePathOz;
    private static TaskRunner taskRunner = new TaskRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        note = new NoteModel();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontSize = sharedPreferences.getInt("fontSize", 17);

        // initialize toolbar
        Toolbar toolbarTop = findViewById(R.id.toolbar_editor);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);

        resources = getResources();

        handlePathOz = new HandlePathOz(this, this);

        etEditor = findViewById(R.id.et_editor);  // editText donde se escribe el contenido del archivo
        etEditor.setTextSize(fontSize);
        etEditor.addTextChangedListener(textWatcher);
        etEditor.requestFocus();
        textViewUndoRedo = new TextViewUndoRedo(etEditor);

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
                        RecyclerViewNotesManager.deleteItemAndData(note);
                    });
                } else {
                    etEditor.setText(texto);
                    handlePathOz.getRealPath(uriFile);

                    //show keyboard
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

            }

        }

        textViewUndoRedo = new TextViewUndoRedo(etEditor);

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

        startActivityForResult(intent, ActionValues.SAVE_FILE_AS.getID());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ActionValues.SAVE_FILE_AS.getID()) {  // si lo que se ha hecho es crear un archivo
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

                        saveButton.setIcon(R.drawable.file_save);

                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        } else if(requestCode == ActionValues.OPEN_FILE_PROVIDER.getID()){
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
            Toast.makeText(this, resources.getString(R.string.opening_file) + " " + pathOz.getPath(), Toast.LENGTH_SHORT).show();
            note.setlastOpened(LocalDateTime.now());
            taskRunner.executeAsync(new InsertOrUpdateFile(this, note), (dataResult) -> {
                if(dataResult > 0){
                    RecyclerViewNotesManager.insertOrUpdateItem(note);

                    // update note position to first in mainActivity list
                    RecyclerViewNotesManager.moveItem(0, note);
                }
            });
        }

        //Handle Exception (Optional)
        if (tr != null) {
            Toast.makeText(this, tr.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar menu
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        saveButton = menu.findItem(R.id.save_file);

        // Inflate and initialize the bottom menu
        ActionMenuView bottomBar = findViewById(R.id.bottom_tools);
        Menu bottomMenu = bottomBar.getMenu();
        getMenuInflater().inflate(R.menu.menu_tools_editor, bottomMenu);
        for (int i = 0; i < bottomMenu.size(); i++) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(this::onOptionsItemSelected);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save_file_as){  // save as
            saveFileAs();
            return true;

        } else if (id == R.id.save_file){   // overwrite existing file or create it

            if (note.getPath() != null) {
                texto = etEditor.getText().toString();
                boolean result = FileUtil.overwriteFile(note.getRealPath(), texto, this);
                if(result) {
                    getSupportActionBar().setTitle(note.getTitle());
                    saveButton.setIcon(R.drawable.file_save);
                }
            } else {
                saveFileAs();
            }
            return true;

        } else if (id == R.id.close_app){  // close app

            CustomDialogClass cdd = new CustomDialogClass(this, resources.getString(R.string.dialog_close_app), ActionValues.CLOSE_APP.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;

        } else if(id == R.id.open_file) {  // open file
            CustomDialogClass cdd = new CustomDialogClass(this, resources.getString(R.string.dialog_open_file), ActionValues.OPEN_FILE_PROVIDER.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;

        } else if(id == R.id.new_file_editor) {  // new file
            CustomDialogClass cdd = new CustomDialogClass(this, resources.getString(R.string.dialog_new_file), ActionValues.NEW_FILE.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;

        } else if (id == R.id.file_info){  // shows file info
            CustomDialogFileInfo cdd = new CustomDialogFileInfo(this, note);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;

        } else if(id == android.R.id.home){
            CustomDialogClass cdd = new CustomDialogClass(this, resources.getString(R.string.dialog_close_file), ActionValues.CLOSE_FILE.getID());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;

            // text tools (bottom toolbar)
        } else if(id == R.id.button_undo) {  // undo
            textViewUndoRedo.undo();
            return true;

        } else if(id == R.id.button_redo) {  // redo
            textViewUndoRedo.redo();
            return true;

        } else if (id == R.id.button_cut) {  // cut
             int start = Math.max(etEditor.getSelectionStart(), 0);
             int end = Math.max(etEditor.getSelectionEnd(), 0);
             if(start != end){
                 texto = etEditor.getText().toString();
                 Boolean r = myClipboardManager.copyToClipboard(this, texto.substring(start, end));
                 etEditor.getText().replace(Math.min(start, end), Math.max(start, end), "", 0, 0);
             }

            return true;

        } else if(id == R.id.button_copy) {  // copy
            int start = etEditor.getSelectionStart();
            int end = etEditor.getSelectionEnd();

            if( start != end){
                texto = etEditor.getText().toString();

                Boolean r = myClipboardManager.copyToClipboard(this, texto.substring(start, end));
                if (r) Toast.makeText(this, resources.getText(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
            }

            return true;

        } else if(id == R.id.button_paste) {  // paste
            String paste = myClipboardManager.readFromClipboard(this);
            if (!paste.isEmpty()){
                int start = Math.max(etEditor.getSelectionStart(), 0);
                int end = Math.max(etEditor.getSelectionEnd(), 0);

                etEditor.getText().replace(Math.min(start, end), Math.max(start, end), paste, 0, paste.length());

            }

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            getSupportActionBar().setTitle(note.getTitle() + "*");
            if(saveButton != null) saveButton.setIcon(R.drawable.file_unsaved);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };


}