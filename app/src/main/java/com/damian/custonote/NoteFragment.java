package com.damian.custonote;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;

public class NoteFragment extends Fragment {
    private AppBarConfiguration appBarConfiguration;
//    private ActivityNoteBinding binding;
    Note note;
    TextView textViewTimestamp;
    EditText editTextTitle_contentNote,  editTextContent_contentNote;
    FloatingActionButton fabEditNote;
    MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Context context;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note, container, false);
        AppBarLayout appBarLayout = root.findViewById(R.id.appBarLayout);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("New note");

//        context= this.getContext();
        fabEditNote = root.findViewById(R.id.fabEditNote);
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        Navigation.findNavController(getView()).navigate(R.id.action_navigation_all_to_navigation_note);
        fabEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit the note", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        editTextTitle_contentNote = root.findViewById(R.id.editTextTitle_contentNote);
        editTextContent_contentNote = root.findViewById(R.id.editTextContent_contentNote);
        textViewTimestamp = root.findViewById(R.id.textViewTimestamp);
//        intent = getIntent();

        //GETTING ARGUMENTS
        Bundle noteId = getArguments();
        if(noteId != null) { //if there are delivered some data from previous fragment
            //it means that the note already exists and is opened in view mode
            int receivedNoteId = noteId.getInt("noteId");
//            editTextTitle_contentNote.setText(noteId.getString("noteTitle"));
//            editTextContent_contentNote.setText(noteId.getString("noteContent"));

            note = new Note(receivedNoteId);
            editTextTitle_contentNote.setText(note.getTitle());
            editTextContent_contentNote.setText(note.getContent());
        } else { //there aren't delivered any information so the note is empty
            editTextContent_contentNote.requestFocus();
        }
        return root;
    }
}