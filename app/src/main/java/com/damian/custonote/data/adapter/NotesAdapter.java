package com.damian.custonote.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    //    adapting the note to full view: https://larntech
    //    .net/recyclerview-onclicklistener-open-new-activity-filter-recyclerview-using-search-view/

    public NotesAdapter(Context context, List<Note> listNotes, OnSelectedNoteListener onSelectedNoteListener) {
        this.context = context;
        this.listNotes = listNotes;
        this.onSelectedNoteListener = onSelectedNoteListener;
    }

    public NotesAdapter(Context context, List<Note> listNotes) {
        this.context = context;
        this.listNotes = listNotes;
    }

    @Override
    public NotesPositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_single_note_in_browsing, parent, false);
        NotesPositionViewHolder viewHolder = new NotesPositionViewHolder(view, this);
        /*viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若在多选删除情况下，点击item选择/取消checkbox，不能播放
                if(checkingNotesInProgress) {
                    if(viewHolder.checkBoxNoteChecked.isChecked()) {
                        viewHolder.checkBoxNoteChecked.setChecked(false);
                    } else
                        viewHolder.checkBoxNoteChecked.setChecked(true);
                    return;
                }
                int position = viewHolder.getAdapterPosition();

            }
        });*/

        /*viewHolder.view.setOnLongClickListener((View.OnLongClickListener) v -> {
            if(!checkingNotesInProgress) {
                longClickListener(viewHolder);
            }
            return true;
        });*/
        viewHolder.checkBoxNoteChecked.setOnClickListener(v -> {
            int selectedPosition = viewHolder.getAdapterPosition();
            Note selectedNotes = listNotes.get(selectedPosition);
            if(viewHolder.checkBoxNoteChecked.isChecked()) {
                /*multipleFileList.add(selectedRecordingFiles);
            } else {
                multipleFileList.remove(selectedRecordingFiles);*/
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesPositionViewHolder holder, int position) { //lists all notes with all
        // their parameters
        final Note note = listNotes.get(position);
        holder.textViewTitle.setText(note.getTitle());
        holder.textViewContent.setText(note.displayReadableContent(note.getContent()));
        holder.noteBackground.setBackgroundColor(note.getBackgroundColorValue());

        if(note.getIsFavourite())
            holder.imageViewIsFavourite.setImageResource(R.drawable.ic_star);
        else
            holder.imageViewIsFavourite.setImageResource(R.drawable.ic_empty_star);

        holder.imageViewIsFavourite.setOnClickListener(v -> {
        });


        holder.checkBoxNoteChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("Checkbox clicked");

            }
        });

    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NotesPositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        SpannableTextView spannableTextView;
        TextView textViewTitle, textViewContent;
        ImageView imageViewMainImageOfNote, imageViewIsFavourite;
        NotesAdapter notesAdapter;
        CheckBox checkBoxNoteChecked;
        ConstraintLayout noteBackground;

        public NotesPositionViewHolder(@NonNull View itemView, @NonNull NotesAdapter notesAdapter) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            imageViewIsFavourite = itemView.findViewById(R.id.imageViewIsFavourite);
            noteBackground = itemView.findViewById(R.id.noteBackground);
            checkBoxNoteChecked = itemView.findViewById(R.id.checkBoxNoteChecked);
            //            imageViewMainImageOfNote = itemView.findViewById(R.id.imageViewMainImageOfNote);
            this.notesAdapter = notesAdapter;

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

    private void deleteSelectedNotes() {
    }

    public interface OnSelectedNoteListener {
        void onNoteClickListener(int position); //the order of activities is in AllFragment
        void onLongNoteClickListener(int position);
    }

    public void setOnNoteClickListener(OnSelectedNoteListener onSelectedNoteListener) {
        this.onSelectedNoteListener = onSelectedNoteListener;
    }
}