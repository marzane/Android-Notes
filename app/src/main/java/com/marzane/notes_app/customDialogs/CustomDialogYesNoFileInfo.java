package com.marzane.notes_app.customDialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Threads.task.deleteByPathTask;
import com.marzane.notes_app.models.NoteModel;

import java.io.File;

public class CustomDialogYesNoFileInfo extends Dialog implements View.OnClickListener {

    private TaskRunner taskRunner;

    public Activity activity;
    public ImageButton yes, no;
    public TextView tvMessage;
    public EditText etPath;
    public String message;
    public int action;
    public NoteModel note;

    public CustomDialogYesNoFileInfo(Activity a, String message, int action, NoteModel note) {
        super(a);
        this.activity = a;
        this.message = message;
        this.action = action;
        this.note = note;
        taskRunner = new TaskRunner();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_yes_no_file_info);

        yes = findViewById(R.id.button_accept);
        no = findViewById(R.id.button_cancel);
        tvMessage = findViewById(R.id.tv_dialog);
        etPath = findViewById(R.id.et_filePath);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        tvMessage.setText(message);

        if(action == ActionValues.REMOVE_FROM_LIST.getID()){
            etPath.setText(note.getTitle());
        } else if (action == ActionValues.DELETE_FILE.getID()){
            etPath.setText(note.getRealPath());
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_accept){
            // do an action
            if(action == ActionValues.REMOVE_FROM_LIST.getID() || action == ActionValues.DELETE_FILE.getID()){
                taskRunner.executeAsync(new deleteByPathTask(activity, note.getPath().toString()), countResult -> {
                    activity.recreate();
                });
            }

            if(action == ActionValues.DELETE_FILE.getID()){
                try{
                    File file = new File(note.getRealPath());
                    if(file.exists()) {
                        file.delete();
                        Toast.makeText(activity, R.string.deleted_file_ok, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e){
                    CustomDialogInformation cdd = new CustomDialogInformation(activity, e.getMessage(), ActionValues.NOACTION.getID());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }

            }

        } else if(id == R.id.button_cancel){
            dismiss();
        }
        dismiss();
    }
}
