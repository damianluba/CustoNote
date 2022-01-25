package com.damian.custonote.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.R;
import com.damian.custonote.data.model.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesPositionViewHolder> {
    Context context;
    List<Note> listNotes;
    private OnSelectedNoteListener onSelectedNoteListener;
    private boolean checkingNotesInProgress;

    //    adapting the note to full view: https://larntech.net/recyclerview-onclicklistener-open-new-activity-filter-recyclerview-using-search-view/

    public NotesAdapter(Context context, List<Note> listNotes, OnSelectedNoteListener onSelectedNoteListener) {
        this.context = context;
        this.listNotes = listNotes;
        this.onSelectedNoteListener = onSelectedNoteListener;
    }

    public NotesAdapter(Context context, List<Note> listNotes) {
        this.context = context;
        this.listNotes = listNotes;
    }

    @NonNull @Override
    public NotesPositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_single_note_in_browsing, parent, false);
        return new NotesPositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesPositionViewHolder holder, int position) { //lists all notes
        holder.textViewTitle.setText(listNotes.get(position).getTitle());
        holder.textViewContent.setText(listNotes.get(position).getContent());

        holder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NotesPositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitle, textViewContent;
        ImageView imageViewMainImageOfNote;
//        OnNoteListener onNoteListener;

        public NotesPositionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
//            imageViewMainImageOfNote = itemView.findViewById(R.id.imageViewMainImageOfNote);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!checkingNotesInProgress)
                        onSelectedNoteListener.onNoteClickListener(listNotes.get(getAdapterPosition()));
                    else checkItem();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //TODO delete the selected notes
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

    private void checkItem() {

    }

    /*private void deleteItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());
        holder.itemView.setVisibility(View.GONE);
    }*/

    public interface OnSelectedNoteListener {
        void onNoteClickListener(Note note);
//        void onLongNoteClickListener(Note note);
    }

}