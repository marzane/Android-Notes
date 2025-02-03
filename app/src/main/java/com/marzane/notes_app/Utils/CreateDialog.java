package com.marzane.notes_app.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.marzane.notes_app.customDialogs.CustomDialogFileInfo;
import com.marzane.notes_app.customDialogs.CustomDialogFileOptions;
import com.marzane.notes_app.customDialogs.CustomDialogInformation;
import com.marzane.notes_app.customDialogs.CustomDialogYesNo;
import com.marzane.notes_app.customDialogs.CustomDialogYesNoEdit;
import com.marzane.notes_app.models.NoteModel;

public class CreateDialog {

    Activity activity;
    ColorDrawable bg = new ColorDrawable(Color.TRANSPARENT);

    public CreateDialog(Activity activity){
        this.activity = activity;
    }

    public void yesNo(String message, int action){
        CustomDialogYesNo cd = new CustomDialogYesNo(activity, message, action);
        if(cd.getWindow() != null) cd.getWindow().setBackgroundDrawable(bg);
        cd.create();
    }

    public void yesNoEdit(String message, int action, NoteModel note){
        CustomDialogYesNoEdit cdd = new CustomDialogYesNoEdit(activity, message, action, note);
        if(cdd.getWindow() != null) cdd.getWindow().setBackgroundDrawable(bg);
        cdd.create();
    }

    public void information(String message, int action){
        CustomDialogInformation cd = new CustomDialogInformation(activity, message, action);
        if(cd.getWindow() != null) cd.getWindow().setBackgroundDrawable(bg);
        cd.create();
    }

    public void fileInfo(NoteModel note){
        CustomDialogFileInfo cdd = new CustomDialogFileInfo(activity, note);
        if(cdd.getWindow() != null) cdd.getWindow().setBackgroundDrawable(bg);
        cdd.create();
    }

    public void fileOptions(NoteModel note){
        CustomDialogFileOptions cdd = new CustomDialogFileOptions(activity, note);
        if(cdd.getWindow() != null) cdd.getWindow().setBackgroundDrawable(bg);
        cdd.create();
    }

}
