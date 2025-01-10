package com.marzane.notes_app.customDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.marzane.notes_app.Utils.MyClipboardManager;
import com.marzane.notes_app.R;
import com.marzane.notes_app.models.NoteModel;

/**
 * This dialog is used to show file details such
 * as file name and where is located when user press
 * the button "info file"
 */
public class CustomDialogFileInfo extends Dialog implements View.OnClickListener {

    public Activity activity;
    public ImageButton yes;
    public EditText etTitle, etPath;
    public ImageButton copy1, copy2;
    public NoteModel note;
    private MyClipboardManager mcm = new MyClipboardManager();

    public CustomDialogFileInfo(Activity a, NoteModel note) {
        super(a);
        this.activity = a;
        this.note = note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_file_info);

        yes = findViewById(R.id.button_accept);
        etTitle = findViewById(R.id.et_title);
        etPath = findViewById(R.id.et_location);
        copy1 = findViewById(R.id.ib_copy_1);
        copy2 = findViewById(R.id.ib_copy_2);

        yes.setOnClickListener(this);
        etTitle.setText(note.getTitle());
        etPath.setText(note.getRealPath());

        copy1.setOnClickListener(view -> {
            Boolean b = mcm.copyToClipboard(activity, note.getTitle());
            if (b) Toast.makeText(activity, activity.getResources().getText(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

        copy2.setOnClickListener(view -> {
            Boolean b = mcm.copyToClipboard(activity, note.getRealPath());
            if (b) Toast.makeText(activity, activity.getResources().getText(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.button_accept){
            dismiss();
        }
        dismiss();
    }

}
