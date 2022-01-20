package com.damian.custonote.ui.all;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.NoteActivity;
import com.damian.custonote.R;
import com.damian.custonote.data.adapter.NotesAdapter;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.FragmentAllBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AllFragment extends Fragment implements NotesAdapter.SelectedNote {
    RecyclerView recyclerViewAllNotes;
    FloatingActionButton fabAddNote;
    List<Note> listNotes;
    private AllViewModel allViewModel;
    private FragmentAllBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        allViewModel = new ViewModelProvider(this).get(AllViewModel.class);
        binding = FragmentAllBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerViewAllNotes = root.findViewById(R.id.recyclerViewAllNotes); //R chosen from com.damian.custonote.R
        recyclerViewAllNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAddNote = getActivity().findViewById(R.id.fabAdd);
        fabAddNote.setImageResource(R.drawable.ic_add_note);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NoteActivity.class));
            }
        });

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        listNotes = databaseHelper.getNotes();
        Log.i("database records quantity - after launching", String.valueOf(listNotes.size()));
        NotesAdapter notesAdapter = new NotesAdapter(getActivity().getApplicationContext(), listNotes, this);

        //press the desired row to show the note in details
        recyclerViewAllNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
//        recyclerViewAllNotes.setOnLongClickListener(/*mark  as a note to delete*/);
        recyclerViewAllNotes.setAdapter(notesAdapter);
        return root;
    }

    @Override
    public void selectedNote(Note note) {
        Bundle bundleNoteData = new Bundle();
        bundleNoteData.putInt("bundleNoteId", note.getId()); //it's enough to pass ID of the note, the NoteFragment gets all note data by its ID
//        Navigation.findNavController(getView()).navigate(R.id.action_navigation_all_to_navigation_note, bundleNoteData);
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        intent.putExtras(bundleNoteData);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}