package com.marzane.notes_app.models;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.time.LocalDateTime;


public class NoteModel implements Serializable, Cloneable {

    private int id;
    private String title;
    private LocalDateTime lastOpened;
    private Uri path;
    private String realPath;

    public NoteModel(){};

    // constructor only title and path
    public NoteModel(String titulo, Uri path) {
        this.title = titulo;
        this.path = path;
    }

    // constructor only title, lastEdit and path
    public NoteModel(String titulo, LocalDateTime lastOpened, Uri path) {
        this.lastOpened = lastOpened;
        this.title = titulo;
        this.path = path;
    }

    // complete constructor
    public NoteModel(int id, String titulo, LocalDateTime lastEdit, Uri path, String realPath) {
        this.lastOpened = lastEdit;
        this.id = id;
        this.title = titulo;
        this.path = path;
        this.realPath = realPath;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getLastOpened() {
        return lastOpened;
    }

    public void setlastOpened(LocalDateTime lastOpened) {
        this.lastOpened = lastOpened;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }


    // compare by uri path
    @Override
    public boolean equals(Object object) {
        return (object instanceof NoteModel) && (path != null)
                ? path.equals(((NoteModel) object).path) //change ".equals" to "=" if you use int instead of Integer
                : (object == this);
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*
    // TODO: arreglar problema uri provider
    public void updateTitleAndRealPath(Activity activity, String newTitle){
        String oldTitle = getTitle();
        setTitle(newTitle);

        // update realPath
        String realPath = getRealPath();
        String newRealPath = realPath.replace(oldTitle, newTitle);
        setRealPath(newRealPath);

        // ...
    }
    */
}
