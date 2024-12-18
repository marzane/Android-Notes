package com.marzane.bloc_de_notas.Threads.task;

import android.content.Context;

import com.marzane.bloc_de_notas.repository.RecentNotesRepository;

import java.util.concurrent.Callable;

public class deleteByPathTask implements Callable<Integer> {

    private RecentNotesRepository notesRepository;
    private String uri;

    public deleteByPathTask(Context context, String uri){
        this.uri = uri;
        notesRepository = new RecentNotesRepository(context);
    }


    @Override
    public Integer call() throws Exception {

        int result = notesRepository.deleteByPath(uri);

        return result;
    }
}
