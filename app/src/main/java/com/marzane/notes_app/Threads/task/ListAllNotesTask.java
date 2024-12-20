package com.marzane.notes_app.Threads.task;

import android.content.Context;

import com.marzane.notes_app.models.NoteModel;
import com.marzane.notes_app.repository.RecentNotesRepository;

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