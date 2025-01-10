package com.marzane.notes_app.customDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.activities.EditorActivity;
import com.marzane.notes_app.models.NoteModel;

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

    public CustomDialogFileOptions(Activity a, NoteModel note) {
        super(a);
        this.activity = a;
        this.note = note;
        resources = activity.getResources();
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
        delete = findViewById(R.id.button_delete_file);

        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        info.setOnClickListener(this);
        removeList.setOnClickListener(this);
        delete.setOnClickListener(this);
        tvMessage.setText(note.getTitle());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_back){
            dismiss();

        } else if(id == R.id.button_edit){
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra(resources.getString(R.string.extra_intent_uri_file), note.getPath());
            activity.startActivity(intent);

        } else if(id == R.id.button_file_info){
            CustomDialogFileInfo cdd = new CustomDialogFileInfo(activity, note);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

        } else if(id == R.id.button_delete_from_list){
            String message = resources.getString(R.string.dialog_remove_from_list);
            CustomDialogYesNoEdit cdd = new CustomDialogYesNoEdit(activity, message, ActionValues.REMOVE_FROM_LIST.getID(), note);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

        } else if (id == R.id.button_delete_file) {
            String message = resources.getString(R.string.dialog_delete_file);
            CustomDialogYesNoEdit cdd = new CustomDialogYesNoEdit(activity, message, ActionValues.DELETE_FILE.getID(), note);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

        } else if (id == R.id.button_rename){

            // TODO: rename

            Toast.makeText(activity, "rename", Toast.LENGTH_SHORT).show();
            String message = resources.getString(R.string.dialog_rename_file);
            CustomDialogYesNoEdit cdd = new CustomDialogYesNoEdit(activity, message, ActionValues.RENAME_FILE.getID(), note);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        }

        dismiss();
    }

}
