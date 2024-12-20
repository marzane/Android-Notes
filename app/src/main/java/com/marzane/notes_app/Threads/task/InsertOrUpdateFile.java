package com.marzane.notes_app.Threads.task;

import android.content.Context;

import com.marzane.notes_app.models.NoteModel;
import com.marzane.notes_app.repository.RecentNotesRepository;

import java.util.concurrent.Callable;

public class InsertOrUpdateFile implements Callable<Integer> {

    private RecentNotesRepository notesRepository;
    private NoteModel note;  // la nota que quiero insertar o actualizar

    public InsertOrUpdateFile(Context context, NoteModel note){
        this.note = note;
        notesRepository = new RecentNotesRepository(context);
    }


    @Override
    public Integer call() throws Exception {

        // compruebo si la nota ya existe en la bd
        NoteModel noteResult = notesRepository.listByPath(note.getPath().toString());

        Integer countResult = 0;

        if(noteResult != null){
            // si existe, actualizo
            countResult = notesRepository.updateByPath(note);

        } else {
            // si no existe, inserto la nueva nota en la bd
            countResult = (int) notesRepository.insert(note);
        }

        return countResult;
    }
}
