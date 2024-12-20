package com.marzane.notes_app.Threads.task;

import android.content.Context;

import com.marzane.notes_app.models.NoteModel;
import com.marzane.notes_app.repository.RecentNotesRepository;

import java.util.concurrent.Callable;

public class ListByPathTask implements Callable<NoteModel> {

    private RecentNotesRepository notesRepository;
    private NoteModel note;

    public ListByPathTask(Context context, NoteModel note){
        this.note = note;
        notesRepository = new RecentNotesRepository(context);
    }


    @Override
    public NoteModel call() throws Exception {

        // compruebo si la nota ya existe en la bd
        NoteModel noteResult = notesRepository.listByPath(note.getPath().toString());

        return noteResult;
    }
}
