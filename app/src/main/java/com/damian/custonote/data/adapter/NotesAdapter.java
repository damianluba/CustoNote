package com.damian.custonote.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.R;
import com.damian.custonote.data.model.Note;
import com.mpt.android.stv.SpannableTextView;

import java.util.List;

//https://riptutorial.com/android/example/2992/easy-onlongclick-and-onclick-example
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
        return new NotesPositionViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesPositionViewHolder holder, int position) { //lists all notes with all their parameters
        holder.textViewTitle.setText(listNotes.get(position).getTitle());
        holder.textViewContent.setText(listNotes.get(position).getContent());
        holder.noteBackground.setBackgroundColor(listNotes.get(position).getColorBackgroundValue());

        if(listNotes.get(position).getIsFavourite())
            holder.imageViewIsFavourite.setImageResource(R.drawable.ic_star);
        else holder.imageViewIsFavourite.setImageResource(R.drawable.ic_empty_star);

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

    public class NotesPositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        SpannableTextView spannableTextView;
        TextView textViewTitle, textViewContent;
        ImageView imageViewMainImageOfNote, imageViewIsFavourite;
        NotesAdapter notesAdapter;
        ConstraintLayout noteBackground;

        public NotesPositionViewHolder(@NonNull View itemView, @NonNull NotesAdapter notesAdapter) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            imageViewIsFavourite = itemView.findViewById(R.id.imageViewIsFavourite);
            noteBackground = itemView.findViewById(R.id.noteBackground);
            this.notesAdapter = notesAdapter;

//            imageViewMainImageOfNote = itemView.findViewById(R.id.imageViewMainImageOfNote);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onSelectedNoteListener.onNoteClickListener(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onSelectedNoteListener.onLongNoteClickListener(getAdapterPosition());
            return false;
        }
    }

    /*private void deleteItem(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());
        holder.itemView.setVisibility(View.GONE);
    }*/

    public interface OnSelectedNoteListener {
        void onNoteClickListener(int position);
        void onLongNoteClickListener(int position);
    }

    public void setOnNoteClickListener(OnSelectedNoteListener onSelectedNoteListener) {
        this.onSelectedNoteListener = onSelectedNoteListener;
    }
}