package com.damian.custonote.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
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
    private Note note;
    private TextView textViewTimestamp;
    private MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode, menuItemBackgroundColor;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private EditText editTextTitle_contentNote,  editTextContent_contentNote;
    private Toolbar toolbarAlignText;
    private LocalDateTime timestampNoteCreated, timestampNoteModified;
    private Context context;
    private Intent intent;
    private Toolbar toolbarTextTools;
    private NestedScrollView noteBackground;

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
        toolbarTextTools = findViewById(R.id.toolbarTextTools);
        toolbarAlignText = findViewById(R.id.toolbarAlignText);
        noteBackground = findViewById(R.id.nestedScrollView);



        //------------------------------------------------RECEIVING DATA FROM PREVIOUS ACTIVITY and  NOTE CONFIGURATION  ----------------------------------------------------------
        intent = getIntent();
        if(intent.getExtras() != null)  //if there are delivered some data from previous activity
            retrieveNoteDataFromBundle();
         else  configureNewNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_note_activity, menu);
        menuItemSave = menu.findItem(R.id.itemSave);
        menuItemDelete = menu.findItem(R.id.itemDelete);
        menuItemStar = menu.findItem(R.id.itemStar);
        menuItemSwitchMode = menu.findItem(R.id.itemSwitchMode);
        menuItemBackgroundColor = menu.findItem(R.id.itemBackgroundColor);

        //started buttons configuration
        coloriseStar();
        setNoteMode();

        //----------------------------------------------------------SAVING-------------------------------------------------------------------------------------
        menuItemSave.setOnMenuItemClickListener(menuItem -> {
            timestampNoteModified = LocalDateTime.now(); //it needs to be done objectively
            timestampNoteCreated = timestampNoteModified;
            textViewTimestamp.setVisibility(View.VISIBLE);

            if(note.getId() == 0)
                saveNoteNotModifiedBefore();
             else updateNote();


            Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
            database.close();
            return false;
        });

        menuItemDelete.setOnMenuItemClickListener(menuItem -> {
            databaseHelper.deleteNote(note);
            return false;
        });

        menuItemStar.setOnMenuItemClickListener(menuItem -> {
            databaseHelper = new DatabaseHelper(context);
            note.setIsFavourite(!note.getIsFavourite());
            coloriseStar();
            database = databaseHelper.getWritableDatabase();
            databaseHelper.markOrUnmarkNoteAsFavourite(note.getId(), note.getIsFavourite());
            database.close();
            return false;
        });

        menuItemSwitchMode.setOnMenuItemClickListener(menuItem -> {
            note.setIsBasicMode(!note.getIsBasicMode());
            setNoteMode();
            return false;
        });

        menuItemBackgroundColor.setOnMenuItemClickListener(menuItem -> {
            configureAndShowDialogColorPalette();
            return false;
        });

        return super.onCreateOptionsMenu(menu);
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

    private void updateNote() {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        databaseHelper.updateNote(note.getId(), editTextContent_contentNote.getText().toString(), editTextTitle_contentNote.getText().toString(), note.getIsFavourite(), note.getColorBackgroundValue());

        textViewTimestamp.setText(
                        "Modified: " + timestampNoteModified.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) +
                        "\nCreated: " + timestampNoteCreated.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
    }

    private void saveNoteNotModifiedBefore() {
        textViewTimestamp.setText("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

        //here attributes isFavourite and isBasicMode aren't saved -  as only a user clicks marks a note as favourite, makes changes in note data
        note.setTitle(editTextTitle_contentNote.getText().toString());
        note.setContent(editTextContent_contentNote.getText().toString());

        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        databaseHelper.addNote(note);
    }

    public void showSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextContent_contentNote.requestFocus();
        imm.showSoftInput(editTextContent_contentNote, InputMethodManager.SHOW_IMPLICIT);
    }

    private void configureNewNote() {
        textViewTimestamp.setVisibility(View.GONE);
        note = new Note(null, null, true, false, false, null, null, Color.WHITE);
        showSystemKeyboard();
    }

    private void retrieveNoteDataFromBundle() {
        note = (Note) intent.getSerializableExtra("bundleNote");
        textViewTimestamp.setVisibility(View.VISIBLE);

        if(note.getIsBasicMode())
            HideAdvancedButtonsForAdvancedMode();
        else configureButtonsForAdvancedMode();

        editTextTitle_contentNote.setText(note.getTitle());
        editTextContent_contentNote.setText(note.getContent());
        noteBackground.setBackgroundColor(note.getColorBackgroundValue()); //make a background

        textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
        if(note.getTimestampNoteModified() != null) //if a note wasn't modified
            textViewTimestamp.setText(textViewTimestamp.getEditableText() + "\nModified: " + note.getTimestampNoteModified().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
    }

    public void configureButtonsForAdvancedMode() {
        toolbarAlignText.setVisibility(View.VISIBLE);

        ImageView imageViewBold = findViewById(R.id.imageViewBold);
        imageViewBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpannableStringBuilder spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                spannableString.setSpan(new BackgroundColorSpan(Color.GREEN), 0, note.getContent().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });

        ImageView imageViewItalic = findViewById(R.id.imageViewItalic);
        imageViewItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC) , editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });

        ImageView imageViewUnderline = findViewById(R.id.imageViewUnderline);
        imageViewUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new UnderlineSpan(), editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });

        ImageView imageViewAlign = findViewById(R.id.imageViewAlign);
        imageViewAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });

        Button buttonFont = findViewById(R.id.buttonFontSize);
        buttonFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new RelativeSizeSpan(2f), editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });

        Button buttonTextColor = findViewById(R.id.buttonTextColor);
        buttonTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable spannableString = new SpannableStringBuilder(editTextContent_contentNote.getText());
                spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), editTextContent_contentNote.getSelectionStart(), editTextContent_contentNote.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                editTextContent_contentNote.setText(spannableString);
            }
        });
    }

    private void coloriseStar() {
        if(note.getIsFavourite())
            menuItemStar.setIconTintList(ColorStateList.valueOf(Color.YELLOW)); //setting the color of the item
        else menuItemStar.setIconTintList(ColorStateList.valueOf(Color.TRANSPARENT)); //setting the color of the item
    }

    private void applyBackgroundColor() {
        int colorValue = Color.WHITE;
        {/*        switch (selectedColor) {
            case "gray":
                colorValue = Color.GRAY;
                break;
            case "blue":
                colorValue = Color.BLUE;
                break;
            case "cyan":
                colorValue = Color.CYAN;
                break;
            case "green":
                colorValue = Color.GREEN;
                break;
            case "red":
                colorValue = Color.RED;
                break;
            case "magenta":
                colorValue = Color.MAGENTA;
                break;
            case "yellow":
                colorValue = Color.YELLOW;
                break;
            default:    //a default color of a note is white
                colorValue = Color.WHITE;
                break;
        }*/}

        note.setColorBackgroundValue(colorValue);
        noteBackground.setBackgroundColor(colorValue);
    }

    private void setNoteMode() {
        if(note.getIsBasicMode()) {
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.WHITE)); //setting the color of the item
            toolbarAlignText.setVisibility(View.GONE);
            toolbarTextTools.setVisibility(View.GONE);
        } else {
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.BLUE)); //setting the color of the item
            toolbarTextTools.setVisibility(View.VISIBLE);
            toolbarAlignText.setVisibility(View.VISIBLE);
            configureButtonsForAdvancedMode();
        }
    }

    public void configureAndShowDialogColorPalette() {
        Dialog dialogColorPalette = new Dialog(NoteActivity.this);
        dialogColorPalette.setContentView(R.layout.layout_dialog_color_palette);

        ImageView imageViewBackgroundGray = dialogColorPalette.findViewById((R.id.imageViewBackgroundGray));
        ImageView imageViewBackgroundBrown = dialogColorPalette.findViewById(R.id.imageViewBackgroundBrown);
        ImageView imageViewBackgroundBlue = dialogColorPalette.findViewById(R.id.imageViewBackgroundBlue);
        ImageView imageViewBackgroundCyan = dialogColorPalette.findViewById(R.id.imageViewBackgroundCyan);
        ImageView imageViewBackgroundGreen = dialogColorPalette.findViewById(R.id.imageViewBackgroundGreen);
        ImageView imageViewBackgroundRed = dialogColorPalette.findViewById(R.id.imageViewBackgroundRed);
        ImageView imageViewBackgroundMagenta = dialogColorPalette.findViewById(R.id.imageViewBackgroundMagenta);
        ImageView imageViewBackgroundYellow = dialogColorPalette.findViewById(R.id.imageViewBackgroundYellow);
        ImageView imageViewBackgroundOrange = dialogColorPalette.findViewById(R.id.imageViewBackgroundOrange);
        ImageView imageViewBackgroundWhite = dialogColorPalette.findViewById(R.id.imageViewBackgroundWhite);
        ImageView imageViewBackgroundDarkGreen = dialogColorPalette.findViewById(R.id.imageViewBackgroundDarkGreen);
        ImageView imageViewBackgroundPurple = dialogColorPalette.findViewById(R.id.imageViewBackgroundPurple);

        imageViewBackgroundGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setColorBackgroundValue(Color.GRAY);
                dialogColorPalette.hide();
                noteBackground.setBackgroundColor(note.getColorBackgroundValue());
            }
        });
        imageViewBackgroundBrown.setOnClickListener(v -> {
            note.setColorBackgroundValue(getResources().getColor(R.color.brown));
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundBlue.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.BLUE);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundCyan.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.CYAN);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundGreen.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.GREEN);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundRed.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.RED);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundMagenta.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.MAGENTA);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundYellow.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.YELLOW);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundOrange.setOnClickListener(v -> {
            note.setColorBackgroundValue(getResources().getColor(R.color.orange));
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundWhite.setOnClickListener(v -> {
            note.setColorBackgroundValue(Color.WHITE);
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundPurple.setOnClickListener(v -> {
            note.setColorBackgroundValue(getResources().getColor(R.color.purple));
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });
        imageViewBackgroundDarkGreen.setOnClickListener(v -> {
            note.setColorBackgroundValue(getResources().getColor(R.color.dark_green));
            dialogColorPalette.hide();
            noteBackground.setBackgroundColor(note.getColorBackgroundValue());
        });

        dialogColorPalette.show();
    }

    public void HideAdvancedButtonsForAdvancedMode() {
            toolbarTextTools.setVisibility(View.GONE);
            toolbarAlignText.setVisibility(View.GONE);
    }
}