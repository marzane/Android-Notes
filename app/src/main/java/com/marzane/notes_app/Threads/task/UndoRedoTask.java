package com.marzane.notes_app.Threads.task;

import android.content.Context;
import android.widget.EditText;

import com.marzane.notes_app.Utils.TextViewUndoRedo;

import java.util.concurrent.Callable;

// To pass input parameters
public class UndoRedoTask implements Callable<Integer> {
    private TextViewUndoRedo textViewUndoRedo;
    private EditText et;
    private Context context;

    public UndoRedoTask(Context context, EditText et) {
        this.et = et;
        this.context = context;
    }

    @Override
    public Integer call() {
        // Some long running task


        return 0;
    }
}