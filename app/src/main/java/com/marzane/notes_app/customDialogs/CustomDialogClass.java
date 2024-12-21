package com.marzane.notes_app.customDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Threads.task.DeleteAllNotesTask;
import com.marzane.notes_app.Utils.FileUtil;
import com.marzane.notes_app.activities.EditorActivity;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    private TaskRunner taskRunner;

    public Activity activity;
    public ImageButton yes, no;
    public TextView tvMessage;
    public String message;
    public int action;

    public CustomDialogClass(Activity a, String message, int action) {
        super(a);
        this.activity = a;
        this.message = message;
        this.action = action;
        taskRunner = new TaskRunner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_yes_no);

        yes = findViewById(R.id.button_accept);
        no = findViewById(R.id.button_cancel);
        tvMessage = findViewById(R.id.tv_dialog);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_accept){
            // do an action
            if(action == ActionValues.CLOSE_APP.getID()){
                activity.finishAffinity();
            } else if(action == ActionValues.OPEN_FILE_PROVIDER.getID()){
                FileUtil.openFileIntent(activity);
            } else if(action == ActionValues.NEW_FILE.getID()){
                Intent intentNew = new Intent(activity, EditorActivity.class);
                activity.startActivity(intentNew);
                activity.finish();
            } else if(action == ActionValues.CLEAR_LIST.getID()){
                taskRunner.executeAsync(new DeleteAllNotesTask(activity), (data) -> {
                    activity.recreate();
                });
            } else if(action == ActionValues.CLOSE_FILE.getID()){
                activity.finish();
            }

        } else if(id == R.id.button_cancel){
            dismiss();
        }
        dismiss();
    }
}
