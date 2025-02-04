package com.marzane.notepad.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notepad.R;

import com.marzane.notepad.Utils.CreateDialog;
import com.marzane.notepad.activities.EditorActivity;
import com.marzane.notepad.models.NoteModel;
import com.marzane.notepad.Utils.DateUtil;

import java.util.ArrayList;

public class NoteCustomAdapter extends RecyclerView.Adapter<NoteCustomAdapter.ViewHolder> {

    private ArrayList<NoteModel> noteList;
    private Activity activity;
    private Resources resources;
    private CreateDialog createDialog;


    public NoteCustomAdapter(ArrayList<NoteModel> noteList, Activity activity) {
        this.noteList = noteList;
        this.activity = activity;
        this.resources = activity.getResources();
        createDialog = new CreateDialog(activity);
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_note_item, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        NoteModel note = noteList.get(position);

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTvTitle().setText(note.getTitle());
        viewHolder.getDate().setText(DateUtil.customDateFormatToString(note.getLastOpened(), resources.getString(R.string.date_format)));
        viewHolder.getTime().setText(DateUtil.customDateFormatToString(note.getLastOpened(), resources.getString(R.string.time_format)));


        // when you click an item
        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(viewHolder.itemView.getContext(), EditorActivity.class);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            Uri uri = Uri.parse(note.getPath());
            intent.putExtra(resources.getString(R.string.extra_intent_uri_file), uri);
            viewHolder.itemView.getContext().startActivity(intent);
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            createDialog.fileOptions(note);
            return true;
        });
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     * @return int
     */
    @Override
    public int getItemCount() {
        return noteList.size();
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvDate, tvTime;

        public ViewHolder(View view) {
            super(view);

            // Define click listener for the ViewHolder's View
            tvTitle = view.findViewById(R.id.rv_note_title);
            tvDate = view.findViewById(R.id.rv_note_date);
            tvTime = view.findViewById(R.id.rv_note_time);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }
        public TextView getDate(){
            return tvDate;
        }
        public TextView getTime(){
            return tvTime;
        }
    }

}
