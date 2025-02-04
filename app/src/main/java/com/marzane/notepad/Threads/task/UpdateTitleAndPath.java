package com.marzane.notepad.Threads.task;

import android.content.Context;

import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.repository.RecentNotesRepository;

import java.util.concurrent.Callable;

public class UpdateTitleAndPath implements Callable<Integer> {

    private RecentNotesRepository notesRepository;
    private NoteModel newNote;  // la nota que quiero insertar o actualizar
    private NoteModel oldNote;

    public UpdateTitleAndPath(Context context, NoteModel oldNote, NoteModel newNote){
        this.newNote = newNote;
        this.oldNote = oldNote;
        notesRepository = new RecentNotesRepository(context);
    }


    @Override
    public Integer call() throws Exception {

        // localizo la nota en la base de datos
        NoteModel noteResult = notesRepository.listByPath(oldNote.getPath().toString());

        Integer countResult = 0;

        // si existe, lo actualizo
        if(noteResult != null){
            countResult = notesRepository.updateNoteNoId(oldNote, newNote);

        }

        return countResult;
    }
}
