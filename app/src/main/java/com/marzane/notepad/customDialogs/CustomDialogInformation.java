package com.marzane.notepad.customDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marzane.notepad.ActionValues;
import com.marzane.notepad.R;

/**
 * Simple dialog only to show a message with
 * a button to close it
 */
public class CustomDialogInformation extends Dialog implements View.OnClickListener {

    public Activity c;
    public ImageButton okButton;
    public TextView tvMessage;
    public String message;
    public int action;

    public CustomDialogInformation(Activity a, String message, int action) {
        super(a);
        this.c = a;
        this.message = message;
        this.action = action;
        super.setCancelable(false);
        super.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_information);

        okButton = findViewById(R.id.button_accept);
        tvMessage = findViewById(R.id.tv_dialog);

        okButton.setOnClickListener(this);
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        if(action == ActionValues.CLOSE_ACTIVITY.getID()) {
            c.finish();
        } else {
            dismiss();
        }
    }
}
