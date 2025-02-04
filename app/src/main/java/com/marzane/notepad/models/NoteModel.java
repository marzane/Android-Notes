package com.marzane.notepad.models;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.time.LocalDateTime;


public class NoteModel implements Serializable, Cloneable {

    private int id;
    private String title;
    private LocalDateTime lastOpened;
    private String path;
    private String realPath;

    public NoteModel(){};

    // constructor only title and path
    public NoteModel(String titulo, String path) {
        this.title = titulo;
        this.path = path;
    }

    // constructor only title, lastEdit and path
    public NoteModel(String titulo, LocalDateTime lastOpened, String path) {
        this.lastOpened = lastOpened;
        this.title = titulo;
        this.path = path;
    }

    // complete constructor
    public NoteModel(int id, String titulo, LocalDateTime lastEdit, String path, String realPath) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
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

}
