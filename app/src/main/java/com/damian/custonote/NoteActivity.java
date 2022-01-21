package com.damian.custonote;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.ActivityNoteBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityNoteBinding binding;
    Note note = new Note();
    TextView textViewTimestamp;
    MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    EditText editTextTitle_contentNote,  editTextContent_contentNote;
    Toolbar toolbarAlignText;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Context context;
    Intent intent;
    int receivedNoteId;
    String receivedTitle;
    String receivedContent;
    boolean receivedIsBasicMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //        getSupportActionBar().setTitle("New note");

        context= this;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note);
        /*appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

        binding.fabEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit the note", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        editTextTitle_contentNote = findViewById(R.id.editTextTitle_contentNote);
        editTextContent_contentNote = findViewById(R.id.editTextContent_contentNote);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        toolbarAlignText = findViewById(R.id.toolbarAlignText);

        //RECEIVING DATA FROM PREVIOUS ACTIVITY
        intent = getIntent();
        if(intent.getExtras() != null) { //if there are delivered some data from previous activity
            //it means that THE NOTE ALREADY EXISTS and is opened in view mode
            receivedNoteId = intent.getIntExtra("bundleId", 0);
            receivedTitle = intent.getStringExtra("bundleTitle");
            receivedContent = intent.getStringExtra("bundleContent");
            receivedIsBasicMode = intent.getBooleanExtra("bundleIsBasicMode", true);
            note.setID(receivedNoteId);
            editTextTitle_contentNote.setText(receivedTitle);
            if(receivedIsBasicMode) {//if user left the note keeping it in a basic mode
                editTextContent_contentNote.setText(receivedContent);
            }
            else {//show advanced editor for note editing
                initialiseAdvancedToolbar();
            }
        } else { //there aren't delivered any information so the note can be created by a user
            editTextContent_contentNote.requestFocus();
            receivedIsBasicMode = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_note_activity, menu);
        menuItemSave = menu.findItem(R.id.itemSave);
        menuItemDelete = menu.findItem(R.id.itemDelete);
        menuItemStar = menu.findItem(R.id.itemStar);
        menuItemSwitchMode = menu.findItem(R.id.itemSwitchMode);

        menuItemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // SAVING A NOTE
            @Override        //save a note
            public boolean onMenuItemClick(MenuItem menuItem) {
                timestampNoteModified = LocalDateTime.now(); //it needs to be done objectively
                System.err.println("\n\n\n\n\n\n\n A TU WEJDZIE CZY NIE WJEDZIE ");
                System.err.println("wartosc ifa mi podaj prosze: " + timestampNoteCreated == null);
                System.err.println(timestampNoteCreated);
                System.err.println("koniec debugowania");
                if(timestampNoteCreated == null) {//if a note was already opened as a new note
                    System.out.println("tworzy nowa notatke");
                    timestampNoteCreated = timestampNoteModified;
                    textViewTimestamp.setText("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

                    Note note = new Note(editTextTitle_contentNote.getText().toString(), editTextContent_contentNote.getText().toString()/*,timestampNoteCreated*/); //TODO

                    // TODO here you must transform all temporary note changes to data in the note structure
                    databaseHelper = new DatabaseHelper(context);
                    database = databaseHelper.getWritableDatabase();
                    databaseHelper.addNote(note);
                    Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("zapisuje edytowana");
                    databaseHelper.updateNote(note.getId(), editTextContent_contentNote.getText().toString(), editTextTitle_contentNote.getText().toString(), timestampNoteCreated);
                    databaseHelper.getDatabaseName();
                    textViewTimestamp.setText(
                            "Modified: " + timestampNoteModified.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) +
                                    "\nCreated: " + timestampNoteCreated.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
                }

                return false;
            }
        });

        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                databaseHelper.deleteNote(note);
                return false;
            }
        });

        menuItemStar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //TODO
                /*if(receivedIsFavourite) {
                    menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.yellow))); //setting the color of the item
                } else {
                    menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))); //setting the color of the item
                }*/
                return false;
            }
        });

        menuItemSwitchMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //             if(menuItemSwitchMode.getIconTintList().equals(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))))
                Toolbar toolbarTextTools = findViewById(R.id.toolbarTextTools);
                if(receivedIsBasicMode) {
                    hideAdvancedToolbar();
                    menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))); //setting the color of the item
                } else {
                    initialiseAdvancedToolbar();
                    menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.blue))); //setting the color of the item
                }
                //                note.setIsBasicMode(!note.getIsBasicMode());  //set the opposite mode
                receivedIsBasicMode = !receivedIsBasicMode;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initialiseAdvancedToolbar() {
        toolbarAlignText.setVisibility(View.VISIBLE);
    }

    private void hideAdvancedToolbar() {
        toolbarAlignText.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        //        this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() { //called when user press back arrow to go up in the view hierarchy
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}