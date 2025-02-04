package com.marzane.notepad.Threads.task;

import android.content.Context;

import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.repository.RecentNotesRepository;

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
        NoteModel noteResult = notesRepository.listByPath(note.getPath());

        return noteResult;
    }
}
