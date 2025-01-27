package com.marzane.notes_app.Utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notes_app.models.NoteModel;

import java.util.ArrayList;

public class RecyclerViewNotesManager {

    private static RecyclerView rvNoteList;
    private static ArrayList<NoteModel> arrayRecentNotes = new ArrayList<>();


    private RecyclerViewNotesManager(){}


    public static void deleteItem(NoteModel noteModel){
        if(rvNoteList != null && rvNoteList.getAdapter() != null){
            int position = arrayRecentNotes.indexOf(noteModel);
            if (position > -1){
                arrayRecentNotes.remove(position);
                rvNoteList.getAdapter().notifyItemRemoved(position);
            }
        }

    }


    public static void deleteAllItems(){
        if(rvNoteList != null && rvNoteList.getAdapter() != null){
            int size = arrayRecentNotes.size();
            arrayRecentNotes.clear();
            rvNoteList.getAdapter().notifyItemRangeRemoved(0, size);
        }

    }


    public static void insertOrUpdateItem(NoteModel noteModel){
        if(rvNoteList != null && rvNoteList.getAdapter() != null) {
            int position = arrayRecentNotes.indexOf(noteModel);
            if (position > -1) { // update
                arrayRecentNotes.set(position, noteModel);
                rvNoteList.getAdapter().notifyItemChanged(position);

            } else { // insert
                arrayRecentNotes.add(noteModel);
                rvNoteList.getAdapter().notifyItemInserted(arrayRecentNotes.size());
            }
        }
    }


    public static void moveItem(int to, NoteModel noteModel){
        if(rvNoteList != null && rvNoteList.getAdapter() != null){
            int from = arrayRecentNotes.indexOf(noteModel);

            if(from > -1){
                arrayRecentNotes.remove(from);
                arrayRecentNotes.add(to, noteModel);

                rvNoteList.getAdapter().notifyItemMoved(from, to);
            }
        }

    }


    public static void replaceItem(NoteModel oldNote, NoteModel newNote){
        if(rvNoteList != null && rvNoteList.getAdapter() != null){
            int position = arrayRecentNotes.indexOf(oldNote);
            if (position > -1){
                arrayRecentNotes.set(position, newNote);
                rvNoteList.getAdapter().notifyItemChanged(position);

            }
        }

    }


    public static void updateRecyclerViewVisibility(){
        if(RecyclerViewNotesManager.getDataList().isEmpty()){
            rvNoteList.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = rvNoteList.getLayoutParams();
            params.height = 0;
            rvNoteList.setLayoutParams(params);
        } else {
            rvNoteList.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = rvNoteList.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rvNoteList.setLayoutParams(params);
        }
    }


    public static ArrayList<NoteModel> getDataList(){
        return arrayRecentNotes;
    }


    public static RecyclerView getRecyclerView(){
        return rvNoteList;
    }


    public static void setDataList(ArrayList<NoteModel> arrayNotes){
        arrayRecentNotes = arrayNotes;
    }


    public static void setRecyclerView(RecyclerView rv){
        rvNoteList = rv;
    }

}
