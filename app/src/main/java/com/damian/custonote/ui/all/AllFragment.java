package com.damian.custonote.ui.all;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.R;
import com.damian.custonote.data.adapter.NotesAdapter;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.FragmentAllBinding;
import com.damian.custonote.ui.NoteActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AllFragment extends Fragment {
    private RecyclerView recyclerViewAllNotes;
    private List<Note> listNotes;
    private AllViewModel allViewModel;
    private FragmentAllBinding binding;
    private Boolean notesCheckingInProgress;
    private CheckBox checkBoxRemoveNote;
    //    private ActionMode actionMode;
    private static final int RECYCLER_VIEW_CODE = 1;
    MenuItem menuItemSynchroniseNotes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        allViewModel = new ViewModelProvider(this).get(AllViewModel.class);
        binding = FragmentAllBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewAllNotes = root.findViewById(R.id.recyclerViewAllNotes); //R chosen from com.damian.custonote.R
        recyclerViewAllNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAllNotes.setHasFixedSize(true); //?
//        recyclerViewAllNotes.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL)); //it serves the ability to set space between items in recyclerview
        showNotesInUi();


        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        showNotesInUi();
        FloatingActionButton fabAddNote = getActivity().findViewById(R.id.fabAdd);
        fabAddNote.setImageResource(R.drawable.ic_add_note);
        fabAddNote.setOnClickListener(v -> startActivity(new Intent(getContext(), NoteActivity.class)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showNotesInUi() {
        View root = binding.getRoot();
        ViewGroup viewGroupDelete /*= root.findViewById(R.id.)*/;
        recyclerViewAllNotes = root.findViewById(R.id.recyclerViewAllNotes); //R chosen from com.damian.custonote.R
        recyclerViewAllNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAllNotes.setHasFixedSize(true); //?
//        recyclerViewAllNotes.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        listNotes = databaseHelper.getNotes();
        Log.i("database records quantity - after launching", String.valueOf(listNotes.size()));

        //this adapter creates 2 listeners for common click and long click
        final NotesAdapter notesAdapter = new NotesAdapter(getContext().getApplicationContext(), listNotes, new NotesAdapter.OnSelectedNoteListener() {
            @Override
            public void onNoteClickListener(int position, View view) {
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                Note note = listNotes.get(position);
                intent.putExtra("bundleNote", note);
                startActivity(intent);
            }

            @Override
            public void onLongNoteClickListener(int position, View view) {
                Toast.makeText(getContext().getApplicationContext(), "longer press", Toast.LENGTH_LONG).show();
                /*if(actionMode == null) {
                    notesCheckingInProgress = true;
                    actionMode =  ((AppCompatActivity) getActivity()).startActionMode(actionModeCallback);
                    actionMode.setTitle("Delete selected notes");
                }*/
            }
        });
        //the methods for clicks in a note content are defined in NotesAdapter
        recyclerViewAllNotes.setAdapter(notesAdapter);
    }


    //METHODS FOR SPECIAL MODE OF NOTES DELETING
    /*private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        // override methods for implementing methods in case of will of toolbar change
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            mode.setTitle("Delete selected notes");
            mode.getMenuInflater().inflate(R.menu.action_bar_delete_main_activity, menu);
            if(mode == null)
                Toast.makeText(getContext(), "there's no action mode", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.itemMenuDelete_MainActivity: //delete notes now
//                    AlertDialog alertDialogDeleteNotes = new AlertDialog(th);
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
            notesCheckingInProgress = false;
            showNotesInUi();
        }
    };*/
}