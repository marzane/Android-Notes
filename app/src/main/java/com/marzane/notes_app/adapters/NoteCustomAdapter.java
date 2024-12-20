package com.marzane.notes_app.adapters;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notes_app.R;
import com.marzane.notes_app.Utils.DateUtil;
import com.marzane.notes_app.activities.EditorActivity;
import com.marzane.notes_app.models.NoteModel;

import java.util.ArrayList;

public class NoteCustomAdapter extends RecyclerView.Adapter<NoteCustomAdapter.ViewHolder> {

    private ArrayList<NoteModel> noteList;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvLastEdit;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvTitle = (TextView) view.findViewById(R.id.rv_note_title);
            tvLastEdit = (TextView) view.findViewById(R.id.rv_note_date);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvLastEdit(){
            return tvLastEdit;
        }
    }


    public NoteCustomAdapter(ArrayList<NoteModel> noteList) {
        this.noteList = noteList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_note_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        NoteModel note = noteList.get(position);

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTvTitle().setText(note.getTitle());
        viewHolder.getTvLastEdit().setText(DateUtil.LocalDateTimeToString(note.getLastOpened()));

        // when you click an item
        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(viewHolder.itemView.getContext(), EditorActivity.class);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra("uriFile", note.getPath());
            viewHolder.itemView.getContext().startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return noteList.size();
    }

}
