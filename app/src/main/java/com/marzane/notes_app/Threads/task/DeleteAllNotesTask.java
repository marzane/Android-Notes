package com.marzane.notes_app.Threads.task;

import android.content.Context;

import com.marzane.notes_app.repository.RecentNotesRepository;

import java.util.concurrent.Callable;

// To pass input parameters
public class DeleteAllNotesTask implements Callable<Integer> {
    private RecentNotesRepository notesRepository;

    public DeleteAllNotesTask(Context context) {
        notesRepository = new RecentNotesRepository(context);
    }

    @Override
    public Integer call() {
        // Some long running task
        return notesRepository.deleteAll();
    }
}