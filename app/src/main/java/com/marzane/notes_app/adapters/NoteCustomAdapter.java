package com.marzane.notes_app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marzane.notes_app.R;
import com.marzane.notes_app.Utils.DateUtil;
import com.marzane.notes_app.activities.EditorActivity;
import com.marzane.notes_app.customDialogs.CustomDialogFileOptions;
import com.marzane.notes_app.models.NoteModel;

import java.util.ArrayList;

public class NoteCustomAdapter extends RecyclerView.Adapter<NoteCustomAdapter.ViewHolder> {

    private ArrayList<NoteModel> noteList;
    private Activity activity;
    private Resources resources;

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


    public NoteCustomAdapter(ArrayList<NoteModel> noteList, Activity activity, Resources resources) {
        this.noteList = noteList;
        this.activity = activity;
        this.resources = resources;
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
        viewHolder.getDate().setText(DateUtil.customDateFormatToString(note.getLastOpened(), resources.getString(R.string.date_format)));
        viewHolder.getTime().setText(DateUtil.customDateFormatToString(note.getLastOpened(), resources.getString(R.string.time_format)));


        // when you click an item
        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(viewHolder.itemView.getContext(), EditorActivity.class);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(resources.getString(R.string.extra_intent_uri_file), note.getPath());
            viewHolder.itemView.getContext().startActivity(intent);
        });

        viewHolder.itemView.setOnLongClickListener(view -> {
            CustomDialogFileOptions cdd = new CustomDialogFileOptions(activity, note, resources);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
            return true;
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return noteList.size();
    }

}
