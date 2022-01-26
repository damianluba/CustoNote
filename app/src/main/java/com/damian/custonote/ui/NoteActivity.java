package com.damian.custonote.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.damian.custonote.R;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.ActivityNoteBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityNoteBinding binding;
    Note note;
    TextView textViewTimestamp;
    MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    EditText editTextTitle_contentNote,  editTextContent_contentNote;
    Toolbar toolbarAlignText;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Context context;
    Intent intent;
    SpannableString spannableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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


        //------------------------------------------------RECEIVING DATA FROM PREVIOUS ACTIVITY and OPENING THE NOTE ----------------------------------------------------------
        intent = getIntent();
        if(intent.getExtras() != null) { //if there are delivered some data from previous activity
            //it means that THE NOTE ALREADY EXISTS and is opened in view mode
            note = (Note) intent.getSerializableExtra("bundleNote");
            textViewTimestamp.setVisibility(View.VISIBLE);

            spannableString = new SpannableString(note.getContent());
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, note.getContent().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), 0, note.getContent().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),0,note.getContent().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(Color.GREEN), 0, note.getContent().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new BackgroundColorSpan(Color.GREEN), 0, note.getContent().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            editTextTitle_contentNote.setText(note.getTitle());
            editTextContent_contentNote.setText(spannableString);

            textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
            if(note.getTimestampNoteModified() != null) //if a note wasn't modified
                textViewTimestamp.setText(textViewTimestamp.getText() + "\nModified: " + note.getTimestampNoteModified().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
        } else { //THE NOTE WASN'T MODIFIED BEFORE
            // there aren't delivered any information so the note can be created by a user
            textViewTimestamp.setVisibility(View.GONE);
            note = new Note(null, null, true, false, false, null, null);
            showSystemKeyboard();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_note_activity, menu);
        menuItemSave = menu.findItem(R.id.itemSave);
        menuItemDelete = menu.findItem(R.id.itemDelete);
        menuItemStar = menu.findItem(R.id.itemStar);
        menuItemSwitchMode = menu.findItem(R.id.itemSwitchMode);

        //started buttons configuration
        coloriseStar();
        organiseComponentsRelevantWithTextFormatting();

        menuItemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // SAVING A NOTE
            @Override        //save a note
            public boolean onMenuItemClick(MenuItem menuItem) {
                timestampNoteModified = LocalDateTime.now(); //it needs to be done objectively
                timestampNoteCreated = timestampNoteModified;
                textViewTimestamp.setVisibility(View.VISIBLE);

                if(note.getId() == 0) {//if a note was already opened as a new note
                    textViewTimestamp.setText("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

                    //here attributes isFavourite and isBasicMode aren't saved -  as only a user clicks marks a note as favourite, makes changes in note data
                    note = new Note(editTextTitle_contentNote.getText().toString(), editTextContent_contentNote.getText().toString()/*,timestampNoteCreated*/); //TODO
                    databaseHelper = new DatabaseHelper(context);
                    database = databaseHelper.getWritableDatabase();
                    databaseHelper.addNote(note);
                } else { //if not modified the first time
                    databaseHelper = new DatabaseHelper(context);
                    database = databaseHelper.getWritableDatabase();
                    databaseHelper.updateNote(note.getId(), editTextContent_contentNote.getText().toString(), editTextTitle_contentNote.getText().toString());

                    textViewTimestamp.setText(
                                    "Modified: " + timestampNoteModified.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) +
                                    "Created: " + timestampNoteCreated.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
                }

                Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
                database.close();
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
                note.setIsFavourite(!note.getIsFavourite()) ;
                coloriseStar();
                return false;
            }
        });

        menuItemSwitchMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toolbar toolbarTextTools = findViewById(R.id.toolbarTextTools);
                note.setIsBasicMode(!note.getIsBasicMode());
                organiseComponentsRelevantWithTextFormatting();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void coloriseStar() {
        if(note.getIsFavourite())
            menuItemStar.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.yellow))); //setting the color of the item
         else menuItemStar.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))); //setting the color of the item
    }

    private void organiseComponentsRelevantWithTextFormatting() {
        if(note.getIsBasicMode()) {
            menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))); //setting the color of the item
            toolbarAlignText.setVisibility(View.GONE);
        } else {
            menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.blue))); //setting the color of the item
            toolbarAlignText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() { //called when user press back arrow to go up in the view hierarchy
        onBackPressed();
        return true;
    }

    public void showSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextContent_contentNote.requestFocus();
        imm.showSoftInput(editTextContent_contentNote, InputMethodManager.SHOW_IMPLICIT);
    }
}