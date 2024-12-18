package com.marzane.bloc_de_notas.models;

import android.net.Uri;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteModel {

    private int id;
    private String title;
    private LocalDateTime lastEdit;
    private Uri path;
    private String realPath;

    public NoteModel(){};

    // constructor only title and path
    public NoteModel(String titulo, Uri path) {
        this.title = titulo;
        this.path = path;
    }

    // constructor only title, lastEdit and path
    public NoteModel(String titulo, LocalDateTime lastEdit, Uri path) {
        this.lastEdit = lastEdit;
        this.title = titulo;
        this.path = path;
    }

    // complete constructor
    public NoteModel(int id, String titulo, LocalDateTime lastEdit, Uri path, String realPath) {
        this.lastEdit = lastEdit;
        this.id = id;
        this.title = titulo;
        this.path = path;
        this.realPath = realPath;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getLastEdit() {
        return lastEdit;
    }

    public void setlastEdit(LocalDateTime lastEdit) {
        this.lastEdit = lastEdit;
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
}
