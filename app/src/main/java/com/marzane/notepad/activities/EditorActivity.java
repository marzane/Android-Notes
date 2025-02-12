package com.marzane.notepad.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.marzane.notepad.ActionValues;
import com.marzane.notepad.R;
import com.marzane.notepad.SettingsService;
import com.marzane.notepad.Threads.TaskRunner;
import com.marzane.notepad.Threads.task.InsertOrUpdateFile;
import com.marzane.notepad.Threads.task.deleteByPathTask;
import com.marzane.notepad.Utils.CreateDialog;
import com.marzane.notepad.Utils.TextTools;
import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.Utils.FileUtil;
import com.marzane.notepad.Utils.RecyclerViewNotesManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;

public class EditorActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri{

    private Intent intent;
    private String text;  // el texto que contiene el archivo
    private int fontSize;
    private NoteModel note;
    private Resources resources;
    private SettingsService settingsService;
    private Locale locale;
    private CreateDialog createDialog;

    private boolean saveOnDatabase = true;   // file can be saved on database? (recent notes history)
    private boolean enableSaveFile = true;   // save action can be performed?
    private boolean saveFileAs = false;      // this is for the "save fila as"
    private boolean unsavedChanged;          // are there unsaved changes?
    private boolean isAutosaveEnabled;
    private boolean isToolbarEnabled;

    // layout elements
    private EditText etEditor;
    private MenuItem saveButton;
    private ActionMenuView bottomBar;
    private LinearLayout contentToolbar;

    private static HandlePathOz handlePathOz;
    private static TaskRunner taskRunner = new TaskRunner();
    private TextTools textTools;
    
    private static final String LOCALE_STATE = "LOCALE";
    private static final String NOTE_STATE = "NOTE";
    private static final String TEXT_STATE = "TEXT";
    private static final String UNSAVED_STATE = "UNSAVEDCHANGES";
    private static final String SAVE_ENABLED = "SAVEENABLED";

    private final String TEMP_DIR = "temp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        resources = getResources();
        handlePathOz = new HandlePathOz(this, this);
        settingsService = new SettingsService();
        createDialog = new CreateDialog(this);
        contentToolbar = findViewById(R.id.content_toolbar);
        updateSettingsValues();

        // initialize toolbar
        Toolbar toolbarTop = findViewById(R.id.toolbar_editor);
        setSupportActionBar(toolbarTop);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        }


        // initialize main editText
        etEditor = findViewById(R.id.et_editor);
        etEditor.setSaveEnabled(false);
        etEditor.requestFocus();
        etEditor.setTextSize(fontSize);

        unsavedChanged = false;

        textTools = new TextTools(this, etEditor);

        // actualize state
        if (savedInstanceState != null) {
            locale = (Locale) savedInstanceState.getSerializable(LOCALE_STATE);
            note = (NoteModel) savedInstanceState.getSerializable(NOTE_STATE);
            text = savedInstanceState.getString(TEXT_STATE);
            unsavedChanged = savedInstanceState.getBoolean(UNSAVED_STATE);
            enableSaveFile = savedInstanceState.getBoolean(SAVE_ENABLED);

            etEditor.setText(text);

        } else {
            locale = new Locale(settingsService.getLanguage(this));
            note = new NoteModel();

        }

        settingsService.setLocale(locale.getLanguage(), this);


        // si no tengo ningun titulo para el archivo le pongo uno por defecto Ej: "nuevo.txt"
        if (note.getTitle() == null) {
            note.setTitle(resources.getString(R.string.default_title));
        }

        getSupportActionBar().setTitle(note.getTitle());


        // this part is in case we are expecting an intent containing the uri file via "open file" or "open with"
        intent = getIntent();
        String action = intent.getAction();
        Bundle b = intent.getExtras();
        Uri uriFile = null;

        if (Intent.ACTION_VIEW.equals(action)) {  // "open with"
            if (intent.getData() != null) {
                uriFile = intent.getData();
                intent.setData(null);
                saveOnDatabase = false;  // when file is open this way, cannot be edited
                enableSaveFile = false;
            }

        } else if (b != null) {  // "open file"
            uriFile = (Uri) b.get(resources.getString(R.string.extra_intent_uri_file));
            if (uriFile != null) {
                if(FileUtil.isGoogleDriveUri(uriFile)) {
                    saveOnDatabase = false;
                    enableSaveFile = false;

                } else {
                    getContentResolver().takePersistableUriPermission(uriFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uriFile, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    saveOnDatabase = true;


                }
            }

        }


        if (uriFile != null) {  // obtengo la ruta real y el nombre del archivo

            text = FileUtil.readFile(uriFile, this);
            note.setPath(uriFile.toString());

            if (text == null) {  // no existe el archivo en la ubicacion proporcionada; se elimina de la lista

                    taskRunner.executeAsync(new deleteByPathTask(this, uriFile.toString()), (dataResult) -> {
                        RecyclerViewNotesManager.deleteItem(note);
                    });

                    createDialog.information(resources.getString(R.string.dialog_error_read_file), ActionValues.CLOSE_ACTIVITY.getID());

            } else {
                etEditor.setText(text);

                handlePathOz.getRealPath(uriFile);
                intent.removeExtra(resources.getString(R.string.extra_intent_uri_file));
                showKeyboard();
            }

        }


    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putSerializable(LOCALE_STATE, locale);
        savedInstanceState.putSerializable(NOTE_STATE, note);
        savedInstanceState.putSerializable(TEXT_STATE, etEditor.getText().toString());
        savedInstanceState.putBoolean(UNSAVED_STATE, unsavedChanged);
        savedInstanceState.putBoolean(SAVE_ENABLED, enableSaveFile);

        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    protected void onResume() {

        etEditor.addTextChangedListener(textWatcher);

        updateSettingsValues();

        if(settingsService.isLanguageWasChanged()){
            locale = new Locale(settingsService.getLanguage(this));
            this.recreate();
        }

        updateToolbarVisibility(isToolbarEnabled);

        if(etEditor.getTextSize() != fontSize){
            etEditor.setTextSize(fontSize);
        }

        super.onResume();
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
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (intent != null && intent.getData() != null) {

                        Uri uri = intent.getData();

                        if(!FileUtil.isGoogleDriveUri(uri)){
                            text = etEditor.getText().toString();

                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            intent.removeExtra(resources.getString(R.string.extra_intent_uri_file));

                            note = new NoteModel();
                            note.setPath(uri.toString());
                            saveOnDatabase = true;
                            enableSaveFile = true;
                            saveFileAs = true;
                            handlePathOz.getRealPath(uri);
                        } else {
                            Toast.makeText(this, resources.getString(R.string.file_not_saved), Toast.LENGTH_SHORT).show();
                        }

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

                        Intent intentOpenFile = new Intent(this, EditorActivity.class);
                        intentOpenFile.putExtra(resources.getString(R.string.extra_intent_uri_file), uri);
                        this.finish();
                        startActivity(intentOpenFile);

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

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(nombreArchivo);
        note.setTitle(nombreArchivo);
        note.setRealPath(rutaRealArchivo);

        if(!rutaRealArchivo.isEmpty()) {

            if(saveFileAs) {
                if(!checkStoragePermissions()){
                    requestForStoragePermissions();
                } else {
                    saveFileManageResult();
                    saveFileAs = false;
                }

            } else {
                Toast.makeText(this, resources.getString(R.string.opening_file) + " " + pathOz.getPath(), Toast.LENGTH_SHORT).show();
            }

            // save this note in recent notes history?
            if(saveOnDatabase){
                note.setlastOpened(LocalDateTime.now());
                taskRunner.executeAsync(new InsertOrUpdateFile(this, note), (dataResult) -> {
                    if(dataResult > 0){
                        updateNoteOnList();
                        RecyclerViewNotesManager.updateRecyclerViewVisibility();
                    }
                });
            }

        }

        //Handle Exception (Optional)
        if (tr != null) {
            Log.e("tr", tr.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar menu
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        saveButton = menu.findItem(R.id.save_file);
        updateUnsavedChangesState();


        bottomBar = findViewById(R.id.bottom_tools);
        // Inflate and initialize the bottom menu
        Menu bottomMenu = bottomBar.getMenu();
        getMenuInflater().inflate(R.menu.menu_tools_editor, bottomMenu);
        for (int i = 0; i < bottomMenu.size(); i++) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(this::onOptionsItemSelected);
        }

        updateToolbarVisibility(isToolbarEnabled);

        if(!enableSaveFile) {
            disableSaveOption();
            etEditor.removeTextChangedListener(textWatcher);
        }

        return true;
    }


    private void disableSaveOption(){
        saveButton.setIcon(R.drawable.file_save_disabled);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save_file_as){  // save as
            saveFileAs();
            return true;

        } else if (id == R.id.save_file){   // overwrite existing file or create it
            if(enableSaveFile){
                if(!checkStoragePermissions()){
                    requestForStoragePermissions();
                } else {
                    saveFileManageResult();
                }
            }

            return true;

        } else if (id == R.id.close_app){  // close app

            if(unsavedChanged){
                createDialog.yesNo(resources.getString(R.string.dialog_close_app), ActionValues.CLOSE_APP.getID());

            } else {
                this.finishAffinity();
            }

            return true;

        } else if(id == R.id.open_file) {  // open file

            if(unsavedChanged){
                createDialog.yesNo(resources.getString(R.string.dialog_open_file), ActionValues.OPEN_FILE_PROVIDER.getID());

            } else {
                FileUtil.openFileIntent(this);
            }
            return true;

        } else if(id == R.id.new_file_editor) {  // new file

            if(unsavedChanged){
                createDialog.yesNo(resources.getString(R.string.dialog_new_file), ActionValues.NEW_FILE.getID());

            } else {
                Intent intentNew = new Intent(this, EditorActivity.class);
                this.startActivity(intentNew);
                this.finish();
            }
            return true;

        } else if (id == R.id.file_info) {  // shows file info
            createDialog.fileInfo(note);
            return true;

        }else if(id == R.id.settings) {  // open settings
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if(id == android.R.id.home){  // go back to main activity || close editorActivity

            if(unsavedChanged){
                createDialog.yesNo(resources.getString(R.string.dialog_close_file), ActionValues.CLOSE_EDITOR.getID());

            } else {
                finish();
            }

            return true;

            // text tools (bottom toolbar)
        } else if (id == R.id.button_cut) {  // cut
            textTools.cut();
            return true;

        } else if(id == R.id.button_copy) {  // copy
            if(textTools.copy())
                Toast.makeText(this, resources.getText(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();

            return true;

        } else if(id == R.id.button_paste) {  // paste
            textTools.paste();
            return true;

        } else if(id == R.id.button_select_all) {  // select all
            textTools.selectAll();
            return true;

        } else if(id == R.id.button_select) {  // select current word
            textTools.select();
            return true;
/*
        } else if(id == R.id.button_move_start){
            textTools.cursorToLeft();
            return true;

        } else if(id == R.id.button_move_end){
            textTools.cursorToRight();
            return true;
*/
        } else {
            return super.onOptionsItemSelected(item);
        }

    }



    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            unsavedChanged = true;
            updateUnsavedChangesState();

        }

        final Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
        Runnable workRunnable;

        @Override public void afterTextChanged(Editable s) {

            if(isAutosaveEnabled){
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, 300 /*delay*/);
            }

        }

        private void doSmth(String str){
            try{
                saveFile();
            } catch (Exception e){
                Log.getStackTraceString(e);
            }

        }

    };



    private void updateUnsavedChangesState(){
        if(saveButton != null){
            if(unsavedChanged){ // si hay cambios sin guardar
                saveButton.setIcon(R.drawable.file_unsaved);

            } else {
                saveButton.setIcon(R.drawable.file_save);
            }
        }
    }



    private void updateNoteOnList(){
        RecyclerViewNotesManager.insertOrUpdateItem(note);

        // update note position to first in mainActivity list
        RecyclerViewNotesManager.moveItem(0, note);
    }



    private int saveFile() throws IOException {

        if (note.getRealPath() != null) {
            text = etEditor.getText().toString();
            if(FileUtil.overwriteFileStream(this, Uri.parse(note.getPath()), text)){
                unsavedChanged = false;
                updateUnsavedChangesState();

                return 1;

            } else {
                return -1;
            }
        } else {
            saveFileAs();
            return 0;
        }
    }



    private void saveFileManageResult(){
        try{
            int save = saveFile();

            if (save == 1)
                Toast.makeText(this, resources.getString(R.string.file_saved), Toast.LENGTH_SHORT).show();
            else if (save == -1)
                Toast.makeText(this, resources.getString(R.string.file_not_saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, resources.getString(R.string.file_not_saved), Toast.LENGTH_SHORT).show();
        }

    }




    private String copyToInternalStorage(Uri uri){
        return FileUtil.copyFileToInternalStorage(this, uri, TEMP_DIR);
    }



    private void updateToolbarVisibility(boolean enabled){
        if(contentToolbar != null){
            ViewGroup.LayoutParams params = contentToolbar.getLayoutParams();;
            if(enabled){
                contentToolbar.setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                contentToolbar.setVisibility(View.INVISIBLE);
                params.height = 0;
            }
            contentToolbar.setLayoutParams(params);
        }

    }



    private void showKeyboard(){
        //show keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }



    private void updateSettingsValues(){
        isAutosaveEnabled = settingsService.isAutosavingActive(this);
        isToolbarEnabled = settingsService.isToolbarActive(this);
        fontSize = settingsService.getFontSize(this);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Detecting a long press of the back button via onLongPress is broken in Android N.
        // To work around this, use a postDelayed, which is supported in all versions.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(unsavedChanged) {
                createDialog.yesNo(resources.getString(R.string.dialog_close_file), ActionValues.CLOSE_EDITOR.getID());

            } else {
                finish();
            }
            //return super.onKeyDown(keyCode, event);
            return true;
        }
        return super.onKeyDown(keyCode, event);


    }



    // - STORAGE PERMISSIONS -

    // This code checks if Storage Permissions have been granted and returns a boolean:
    public boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            return true;
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
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {

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

                    saveFileManageResult();
                    saveFileAs = false;
                }else{
                    createDialog.information(resources.getString(R.string.permission_storage_denied), ActionValues.NOACTION.getID());
                }
            }
        }
    }


}