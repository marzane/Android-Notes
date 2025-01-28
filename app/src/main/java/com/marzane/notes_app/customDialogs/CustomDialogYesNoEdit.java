package com.marzane.notes_app.customDialogs;

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

import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notes_app.ActionValues;
import com.marzane.notes_app.R;
import com.marzane.notes_app.Threads.TaskRunner;
import com.marzane.notes_app.Threads.task.deleteByPathTask;
import com.marzane.notes_app.Utils.FileUtil;
import com.marzane.notes_app.Utils.RecyclerViewNotesManager;
import com.marzane.notes_app.models.NoteModel;

import java.io.File;
import java.util.ArrayList;

/**
 * This dialog constains:
 * 1) a simple text,
 * 2) an EditText,
 * 3) accept and cancel buttons (yes / no)
 */
public class CustomDialogYesNoEdit extends Dialog implements View.OnClickListener {

    private TaskRunner taskRunner;

    private Activity activity;
    private ImageButton yes, no;
    private TextView tvMessage;
    private EditText etPath;
    private String message;
    private int action;
    private NoteModel note;
    private ArrayList<NoteModel> arrayListNotes;
    private RecyclerView rvNotesMain;

    public CustomDialogYesNoEdit(Activity a, String message, int action, NoteModel note ) {
        super(a);
        this.activity = a;
        this.message = message;
        this.action = action;
        this.note = note;
        arrayListNotes = RecyclerViewNotesManager.getDataList();
        rvNotesMain = RecyclerViewNotesManager.getRecyclerView();
        taskRunner = new TaskRunner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_yes_no_edit);

        yes = findViewById(R.id.button_accept);
        no = findViewById(R.id.button_cancel);
        tvMessage = findViewById(R.id.tv_dialog);
        etPath = findViewById(R.id.et_filePath);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        tvMessage.setText(message);

        etPath.setFocusable(false);

        if(action == ActionValues.REMOVE_FROM_LIST.getID()){
            etPath.setText(note.getTitle());

        } else if (action == ActionValues.DELETE_FILE.getID()){
            etPath.setText(note.getRealPath());
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        boolean deleted = false;

        if(id == R.id.button_accept){
            // do an action
            if(action == ActionValues.DELETE_FILE.getID()){
                deleted = FileUtil.deleteFile(note.getRealPath());

                if(!deleted){
                    Toast.makeText(activity, R.string.deleted_file_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, R.string.deleted_file_ok, Toast.LENGTH_SHORT).show();
                }

            }


            if(action == ActionValues.REMOVE_FROM_LIST.getID() || (action == ActionValues.DELETE_FILE.getID() && deleted)){
                taskRunner.executeAsync(new deleteByPathTask(activity, note.getPath()), countResult -> {
                    if(countResult > 0){
                        // update data
                        RecyclerViewNotesManager.deleteItem(note);

                        // save edited recyclerView and dataset
                        RecyclerViewNotesManager.setDataList(arrayListNotes);
                        RecyclerViewNotesManager.setRecyclerView(rvNotesMain);

                        RecyclerViewNotesManager.updateRecyclerViewVisibility();
                    }

                });
            }

        } else if(id == R.id.button_cancel){
            dismiss();
        }
        dismiss();
    }
}
