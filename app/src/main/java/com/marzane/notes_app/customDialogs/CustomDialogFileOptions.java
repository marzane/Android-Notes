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
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.activities.EditorActivity;
import com.marzane.notes_app.models.NoteModel;

public class CustomDialogFileOptions extends Dialog implements View.OnClickListener {

    private TaskRunner taskRunner;

    public Activity activity;
    public LinearLayout back, edit, openDir, removeList, delete;
    public TextView tvMessage;
    public String message;
    public NoteModel note;

    public CustomDialogFileOptions(Activity a, String message, NoteModel note) {
        super(a);
        this.activity = a;
        this.message = message;
        this.note = note;
        //taskRunner = new TaskRunner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_file_options);

        tvMessage = findViewById(R.id.tv_dialog);
        back = findViewById(R.id.button_back);
        edit = findViewById(R.id.button_edit);
        openDir = findViewById(R.id.button_open_dir);
        removeList = findViewById(R.id.button_delete_from_list);
        delete = findViewById(R.id.button_delete_file);

        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        openDir.setOnClickListener(this);
        removeList.setOnClickListener(this);
        delete.setOnClickListener(this);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_back){
            dismiss();
        } else if(id == R.id.button_edit){
            Intent intent = new Intent(activity, EditorActivity.class);
            intent.putExtra("uriFile", note.getPath());
            activity.startActivity(intent);

        } else if(id == R.id.button_open_dir){
            // Construct an intent for opening a folder
            try{
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(note.getPath(), "resource/folder");
                activity.startActivity(intent);
            }catch (Exception e){
                CustomDialogInformation cdd = new CustomDialogInformation(activity, "Unable to open directory: \n" + e.getMessage(), ActionValues.NOACTION.getID());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }

            /*
            // Check that there is an app activity handling that intent on our system
            if (intent.resolveActivityInfo(activity.getPackageManager(), 0) != null) {
                // Yes there is one start it then
                activity.startActivity(intent);
            } else {
                // Did not find any activity capable of handling that intent on our system
                // TODO: Display error message or something
                Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
            }*/
        } else if(id == R.id.button_delete_from_list){
            dismiss();
        } else if (id == R.id.button_delete_file) {
            dismiss();
        }

        dismiss();
    }

}
