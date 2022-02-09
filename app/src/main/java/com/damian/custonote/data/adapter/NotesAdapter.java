package com.damian.custonote.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.R;
import com.damian.custonote.data.model.Note;

import java.util.List;

//https://riptutorial.com/android/example/2992/easy-onlongclick-and-onclick-example
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesPositionViewHolder> {
    Context context;
    List<Note> listNotes;
    private OnSelectedNoteListener onSelectedNoteListener;
    public boolean checkingNotesInProgress;

    public int selectedCount = 1;


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
        checkingNotesInProgress = false;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_single_note_in_browsing, parent, false);
        NotesPositionViewHolder viewHolder = new NotesPositionViewHolder(view, this);

        /*viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void onBindViewHolder(@NonNull NotesPositionViewHolder holder, int position) { //lists all notes with all their parameters
        final Note note = listNotes.get(position);
        holder.textViewTitle.setText(note.getTitle());
        holder.textViewContent.setText(note.displayReadableContent(note.getContent()));
        holder.noteBackground.setBackgroundColor(note.getBackgroundColorValue());


        holder.imageViewIsFavourite.setImageResource(note.getIsFavourite() == true ? R.drawable.ic_star :
                R.drawable.ic_empty_star);

        holder.imageViewIsFavourite.setOnClickListener(v -> {
        });

        if(checkingNotesInProgress) {
            holder.checkBoxNoteChecked.setVisibility(View.VISIBLE);
            holder.checkBoxNoteChecked.setOnCheckedChangeListener((buttonView, isChecked) -> System.out.println(
            "Checkbox clicked"));
            checkingNotesInProgress = false;

        }
        else {
            holder.checkBoxNoteChecked.setVisibility(View.GONE);
            checkingNotesInProgress = true;
        }
    }

    /*public void switchMode(boolean removingNotes) {
        this.removingNotes = removingNotes;
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NotesPositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
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

            if(checkingNotesInProgress) {
                checkBoxNoteChecked.setVisibility(View.VISIBLE);
                checkBoxNoteChecked.setOnCheckedChangeListener((buttonView, isChecked) -> System.out.println(
                        "Checkbox clicked"));
            } else {
                checkBoxNoteChecked.setVisibility(View.GONE);
            }


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onSelectedNoteListener.onNoteClickListener(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            onSelectedNoteListener.onLongNoteClickListener(getAdapterPosition(), view);
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
        void onNoteClickListener(int position, View view); //the order of activities is in AllFragment
        void onLongNoteClickListener(int position, View view);
    }

    public void setOnNoteClickListener(OnSelectedNoteListener onSelectedNoteListener) {
        this.onSelectedNoteListener = onSelectedNoteListener;
    }

    public interface NotesCallback {
        void switchRecyclerViewToSelectMode();
        void jumpToNoteActivity();
        void onAllSelected();
        void onPartSelected();
    }

    public void switchMode(boolean removingNotes) {
        if(removingNotes) {

        }
    }

    /*private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        // override methods for implementing methods in case of will of toolbar change
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            mode.getMenuInflater().inflate(R.menu.action_bar_delete_main_activity, menu);
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.itemMenuDelete_MainActivity: //delete notes now
                   //deleteSelectedNotes()
                    mode.finish();
                    return true;
                case R.id.menuItemSelectAll:
                    //select all notes
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
            mode = null;
        }

        private void deleteSelectedNotes() {
            *//*while() {


            }*//*
        }
    };*/

    public void updateRecyclerView(List<Note> listSelectedNotes) {
        /*for( i :listSelectedNotes) {
            .delete(i);
        }*/
        notifyDataSetChanged();
    }

}