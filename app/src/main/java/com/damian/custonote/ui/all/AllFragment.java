package com.damian.custonote.ui.all;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class AllFragment extends Fragment{
    RecyclerView recyclerViewAllNotes;
    FloatingActionButton fabAddNote;
    List<Note> listNotes;
    private AllViewModel allViewModel;
    private FragmentAllBinding binding;
    CheckBox checkBoxRemoveNote;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        allViewModel = new ViewModelProvider(this).get(AllViewModel.class);
        binding = FragmentAllBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fabAddNote = getActivity().findViewById(R.id.fabAdd);
        fabAddNote.setImageResource(R.drawable.ic_add_note);

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NoteActivity.class));
            }
        });

        showNotesInUi();
        return root;
    }


    @Override
    public void onResume() {
        showNotesInUi();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showNotesInUi() {
        View root = binding.getRoot();
        recyclerViewAllNotes = root.findViewById(R.id.recyclerViewAllNotes); //R chosen from com.damian.custonote.R
        recyclerViewAllNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        listNotes = databaseHelper.getNotes();
        Log.i("database records quantity - after launching", String.valueOf(listNotes.size()));

        //this adapter creates 2 listeners for common click and long click
        NotesAdapter notesAdapter = new NotesAdapter(getContext().getApplicationContext(), listNotes, new NotesAdapter.OnSelectedNoteListener() {
            @Override
            public void onNoteClickListener(int position) {
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                Note note = listNotes.get(position);
                intent.putExtra("bundleNote", note);
                startActivity(intent);
            }

            @Override
            public void onLongNoteClickListener(int position) {
                Toast.makeText(getContext().getApplicationContext(), "longer press", Toast.LENGTH_LONG).show();
            }
        });
        //the methods for clicks in a note content are defined in NotesAdapter
        recyclerViewAllNotes.setAdapter(notesAdapter);
    }
}