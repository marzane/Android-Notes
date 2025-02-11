package com.marzane.notepad.customDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.marzane.notepad.ActionValues;
import com.marzane.notepad.R;
import com.marzane.notepad.Utils.CreateDialog;
import com.marzane.notepad.activities.EditorActivity;
import com.marzane.notepad.models.NoteModel;

/**
 * This dialog is used when user do a long
 * press on a note in the recent notes list
 * (recyclerView in MainActivity)
 */
public class CustomDialogFileOptions extends Dialog implements View.OnClickListener {

    private Activity activity;
    private LinearLayout back, edit, info, removeList, delete;
    private TextView tvMessage;
    private NoteModel note;
    private Resources resources;
    private CreateDialog createDialog;

    public CustomDialogFileOptions(Activity a, NoteModel note) {
        super(a);
        this.activity = a;
        this.note = note;
        resources = activity.getResources();
        createDialog = new CreateDialog(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_file_options);

        tvMessage = findViewById(R.id.tv_dialog);
        back = findViewById(R.id.button_back);
        edit = findViewById(R.id.button_edit);
        info = findViewById(R.id.button_file_info);
        removeList = findViewById(R.id.button_delete_from_list);
        //delete = findViewById(R.id.button_delete_file);

        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        info.setOnClickListener(this);
        removeList.setOnClickListener(this);
        //delete.setOnClickListener(this);
        tvMessage.setText(note.getTitle());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_back){
            dismiss();

        } else if(id == R.id.button_edit){
            Uri uri = Uri.parse(note.getPath());
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(resources.getString(R.string.extra_intent_uri_file), uri);
            activity.startActivity(intent);

        } else if(id == R.id.button_file_info){
            createDialog.fileInfo(note);

        } else if(id == R.id.button_delete_from_list){
            String message = resources.getString(R.string.dialog_remove_from_list);
            createDialog.yesNoEdit(message, ActionValues.REMOVE_FROM_LIST.getID(), note);

        } /*
          else if (id == R.id.button_delete_file) {
            String message = resources.getString(R.string.dialog_delete_file);
            createDialog.yesNoEdit(message, ActionValues.DELETE_FILE.getID(), note);

        }

        */

        dismiss();
    }

}
