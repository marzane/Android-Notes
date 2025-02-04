package com.marzane.notepad.Threads.task;

import android.content.Context;

import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.repository.RecentNotesRepository;

import java.util.ArrayList;
import java.util.concurrent.Callable;

// To pass input parameters
public class ListAllNotesTask implements Callable<ArrayList<NoteModel>> {
    private RecentNotesRepository notesRepository;

    public ListAllNotesTask(Context context) {
        notesRepository = new RecentNotesRepository(context);
    }

    @Override
    public ArrayList<NoteModel> call() {
        // Some long running task
        return notesRepository.listAll();
    }
}