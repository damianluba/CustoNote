package com.damian.custonote.ui;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
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
    private EditText editTextTitle,  editTextContent;
    private LocalDateTime timestampNoteCreated, timestampNoteModified;
    private Context context;
    private Intent intent;
    private Toolbar toolbarTextTools, toolbarTextAlignment, toolbarFontSize;
    private NestedScrollView noteBackground;
    private int valueOfSelectedColor;

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

        binding.fabEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit the note", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        toolbarTextTools = findViewById(R.id.toolbarTextTools);
        toolbarTextAlignment = findViewById(R.id.toolbarTextAlignment);
        toolbarFontSize = findViewById(R.id.toolbarFontSize);
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
        setUpNoteMode();

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
//            switchMode(note);
            if(note.getIsBasicMode())
                configureButtonsForAdvancedMode();
            else {  //switch to basic mode
                AlertDialog alertDialogClearFormatting;
                clearFormatting(note.getContent());

            }
            note.setIsBasicMode(!note.getIsBasicMode());
            setUpNoteMode();
            return false;
        });

        menuItemBackgroundColor.setOnMenuItemClickListener(menuItem -> {
            configureAndShowDialogColorPaletteForBackground();
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
        String scriptedNoteContent = getScriptedContent(editTextContent.getText());
        databaseHelper.updateNote(note.getId(), editTextTitle.getText().toString(), scriptedNoteContent, note.getIsFavourite(), note.getBackgroundColorValue());

        textViewTimestamp.setText(
                        "Modified: " + timestampNoteModified.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) +
                        "\nCreated: " + timestampNoteCreated.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
    }

    private void saveNoteNotModifiedBefore() {
        textViewTimestamp.setText("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

        //here attributes isFavourite and isBasicMode aren't saved -  as only a user clicks marks a note as favourite, makes changes in note data
        note.setTitle(editTextTitle.getText().toString());
        setScriptedContent(editTextContent.getText());

        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        databaseHelper.addNote(note);
    }

    public void showSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextContent.requestFocus();
        imm.showSoftInput(editTextContent, InputMethodManager.SHOW_IMPLICIT);
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
            hideAdvancedButtonsForAdvancedMode();
        else configureButtonsForAdvancedMode();

        editTextTitle.setText(note.getTitle());
        editTextContent.setText(HtmlCompat.fromHtml(note.getContent() , HtmlCompat.FROM_HTML_MODE_LEGACY));
        note.displayReadableContent(note.getContent());
        noteBackground.setBackgroundColor(note.getBackgroundColorValue()); //make a background

        if(note.getTimestampNoteModified() != null) //if a note was modified before
            textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME))
                    + "\nModified: " + note.getTimestampNoteModified().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
        else textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
    }

    public void configureButtonsForAdvancedMode() {
        ImageView imageViewBold = findViewById(R.id.imageViewBold);
        StyleSpan selectedStyle;
        imageViewBold.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchBoldStyle(imageViewBold, spannableString);
            editTextContent.setText(spannableString);
        });

        ImageView imageViewItalic = findViewById(R.id.imageViewItalic);
        imageViewItalic.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchItalicStyle(imageViewItalic, spannableString);
            editTextContent.setText(spannableString);
        });

        ImageView imageViewUnderline = findViewById(R.id.imageViewUnderline);
        imageViewUnderline.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchUnderlineStyle(imageViewUnderline, spannableString);
            editTextContent.setText(spannableString);
        });

        Button buttonFontSize = findViewById(R.id.buttonFontSize);
        buttonFontSize.setOnClickListener(v -> {
            configureAndShowFontSizeToolbar();
        });

        Button buttonTextColor = findViewById(R.id.buttonTextColor);
        buttonTextColor.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            configureAndShowDialogColorPaletteForTextColor();
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            editTextContent.setText(spannableString);
        });

        TextView textViewBackgroundColor = findViewById(R.id.textViewBackgroundColor);
        textViewBackgroundColor.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            spannableString.setSpan(new BackgroundColorSpan(Color.BLUE), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            configureAndShowDialogColorPaletteForTextBackground();
            textViewBackgroundColor.setHighlightColor(Color.BLUE);
            editTextContent.setText(spannableString);
        });

        ImageView imageViewAlign = findViewById(R.id.imageViewAlign);
        imageViewAlign.setOnClickListener(v -> {
            configureAndShowTextAlignmentToolbar();
            /*Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editTextContent.setText(spannableString);*/
        });
    }

    private void switchBoldStyle(ImageView imageViewBold, Spannable spannableString) {
        if(editTextContent.getSelectionStart() != BOLD) { //make the text bold
            spannableString.setSpan(new StyleSpan(BOLD), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            imageViewBold.setBackgroundColor(Color.GRAY);
        } else { //make the text non-bold
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.removeSpan(editTextContent);
            imageViewBold.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void switchItalicStyle(ImageView imageViewItalic, Spannable spannableString) {
        if(editTextContent.getSelectionStart() != ITALIC) { //make the text italic
            spannableString.setSpan(new StyleSpan(ITALIC), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            imageViewItalic.setBackgroundColor(Color.GRAY);
        } else { //make the text non-italic
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            imageViewItalic.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void switchUnderlineStyle(ImageView imageViewUnderline, Spannable spannableString) {
        if(editTextContent.getSelectionStart() != Paint.UNDERLINE_TEXT_FLAG) { //make the text underlined
            spannableString.setSpan(new UnderlineSpan(), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            imageViewUnderline.setBackgroundColor(Color.GRAY);
        } else { //make the text non-underlined
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            imageViewUnderline.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void coloriseStar() {
        if(note.getIsFavourite())
            menuItemStar.setIconTintList(ColorStateList.valueOf(Color.YELLOW)); //setting the color of the item
        else menuItemStar.setIconTintList(ColorStateList.valueOf(Color.WHITE)); //setting the color of the item
    }

    private void setUpNoteMode() {
        if(note.getIsBasicMode()) {
            //TURN OFF ADVANCED MODE
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.WHITE)); //setting the color of the item
            toolbarTextAlignment.setVisibility(View.GONE);
            toolbarTextTools.setVisibility(View.GONE);
        } else {
            //TURN ON ADVANCED MODE
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.BLUE)); //setting the color of the item

            toolbarTextTools.setVisibility(View.VISIBLE);
            toolbarTextAlignment.setVisibility(View.VISIBLE);
            configureButtonsForAdvancedMode();
        }
    }

    public void setScriptedContent(Spanned spanned) {
        String scriptedContent = HtmlCompat.toHtml(spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        note.setContent(scriptedContent);
    }

    public String getScriptedContent(Spanned spanned) {
        return HtmlCompat.toHtml(spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
    }

    public void clearFormatting(String scriptedContent) {//clear spans
        Spanned spannedContent = HtmlCompat.fromHtml(scriptedContent, HtmlCompat.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannedContent);
        spannableStringBuilder.clearSpans();
        setScriptedContent(spannableStringBuilder);
    }

    public void configureAndShowDialogColorPaletteForBackground() {
        Dialog dialogColorPalette = new Dialog(NoteActivity.this);
        dialogColorPalette.setContentView(R.layout.layout_dialog_color_palette);
        TextView textViewChooseColorForBackground = dialogColorPalette.findViewById(R.id.textView);
        textViewChooseColorForBackground.setText("choose a color of background");

        ImageView imageViewBackgroundGray = dialogColorPalette.findViewById((R.id.imageViewGray));
        ImageView imageViewBackgroundBrown = dialogColorPalette.findViewById(R.id.imageViewBrown);
        ImageView imageViewBackgroundBlue = dialogColorPalette.findViewById(R.id.imageViewBlue);
        ImageView imageViewBackgroundCyan = dialogColorPalette.findViewById(R.id.imageViewCyan);
        ImageView imageViewBackgroundGreen = dialogColorPalette.findViewById(R.id.imageViewGreen);
        ImageView imageViewBackgroundRed = dialogColorPalette.findViewById(R.id.imageViewRed);
        ImageView imageViewBackgroundMagenta = dialogColorPalette.findViewById(R.id.imageViewMagenta);
        ImageView imageViewBackgroundYellow = dialogColorPalette.findViewById(R.id.imageViewYellow);
        ImageView imageViewBackgroundOrange = dialogColorPalette.findViewById(R.id.imageViewOrange);
        ImageView imageViewBackgroundWhite = dialogColorPalette.findViewById(R.id.imageViewWhite);
        ImageView imageViewBackgroundDarkGreen = dialogColorPalette.findViewById(R.id.imageViewDarkGreen);
        ImageView imageViewBackgroundPurple = dialogColorPalette.findViewById(R.id.imageViewPurple);

        imageViewBackgroundGray.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.gray));
        });
        imageViewBackgroundBrown.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.brown));
        });
        imageViewBackgroundBlue.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.blue));
        });
        imageViewBackgroundCyan.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.cyan));
        });
        imageViewBackgroundGreen.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.green));
        });
        imageViewBackgroundRed.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.red));
        });
        imageViewBackgroundMagenta.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.magenta));
        });
        imageViewBackgroundYellow.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.yellow));
        });
        imageViewBackgroundOrange.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.orange));
        });
        imageViewBackgroundWhite.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.white));
        });
        imageViewBackgroundPurple.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.purple));
        });
        imageViewBackgroundDarkGreen.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getResources().getColor(R.color.dark_green));
        });

        dialogColorPalette.show();
    }

    private void applyBackgroundColorToNote(Dialog dialogColorPalette, int valueOfSelectedColor) {
        note.setBackgroundColorValue(valueOfSelectedColor);
        noteBackground.setBackgroundColor(valueOfSelectedColor);
        dialogColorPalette.hide();
    }

    public void configureAndShowDialogColorPaletteForTextColor() {
        Dialog dialogColorPalette = new Dialog(NoteActivity.this);
        dialogColorPalette.setContentView(R.layout.layout_dialog_color_palette);
        TextView textViewChooseColorForText = dialogColorPalette.findViewById(R.id.textView);
        textViewChooseColorForText.setText("choose a color of text");

        ImageView imageViewTextColorGray = dialogColorPalette.findViewById((R.id.imageViewGray));
        ImageView imageViewTextColorBrown = dialogColorPalette.findViewById(R.id.imageViewBrown);
        ImageView imageViewTextColorBlue = dialogColorPalette.findViewById(R.id.imageViewBlue);
        ImageView imageViewTextColorCyan = dialogColorPalette.findViewById(R.id.imageViewCyan);
        ImageView imageViewTextColorGreen = dialogColorPalette.findViewById(R.id.imageViewGreen);
        ImageView imageViewTextColorRed = dialogColorPalette.findViewById(R.id.imageViewRed);
        ImageView imageViewTextColorMagenta = dialogColorPalette.findViewById(R.id.imageViewMagenta);
        ImageView imageViewTextColorYellow = dialogColorPalette.findViewById(R.id.imageViewYellow);
        ImageView imageViewTextColorOrange = dialogColorPalette.findViewById(R.id.imageViewOrange);
        ImageView imageViewTextColorWhite = dialogColorPalette.findViewById(R.id.imageViewWhite);
        ImageView imageViewTextColorDarkGreen = dialogColorPalette.findViewById(R.id.imageViewDarkGreen);
        ImageView imageViewTextColorPurple = dialogColorPalette.findViewById(R.id.imageViewPurple);

        imageViewTextColorGray.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.GRAY);
        });
        imageViewTextColorBrown.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.brown));
        });
        imageViewTextColorBlue.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.BLUE);
        });
        imageViewTextColorCyan.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.CYAN);
        });
        imageViewTextColorGreen.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.GREEN);
        });
        imageViewTextColorRed.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.RED);
        });
        imageViewTextColorMagenta.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.MAGENTA);
        });
        imageViewTextColorYellow.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.YELLOW);
        });
        imageViewTextColorOrange.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.orange));
        });
        imageViewTextColorWhite.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, Color.WHITE);
        });
        imageViewTextColorPurple.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.purple));
        });
        imageViewTextColorDarkGreen.setOnClickListener(v -> {
            applyTextColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.dark_green));
        });

        dialogColorPalette.show();
    }

    private void applyTextColorToNoteContent(Dialog dialogColorPalette, int valueOfSelectedColor) {
        note.setTextColorValue(valueOfSelectedColor);
        editTextContent.setTextColor(note.getTextColorValue());
        dialogColorPalette.hide();
    }

    public void configureAndShowDialogColorPaletteForTextBackground() {
        Dialog dialogColorPalette = new Dialog(NoteActivity.this);
        dialogColorPalette.setContentView(R.layout.layout_dialog_color_palette);
        TextView textViewChooseColor = dialogColorPalette.findViewById(R.id.textView);
        TextView textViewChooseColorForTextBackground = dialogColorPalette.findViewById(R.id.textView);
        textViewChooseColorForTextBackground.setText("choose a color of text background");

        ImageView imageViewTextBackgroundGray = dialogColorPalette.findViewById((R.id.imageViewGray));
        ImageView imageViewTextBackgroundBrown = dialogColorPalette.findViewById(R.id.imageViewBrown);
        ImageView imageViewTextBackgroundBlue = dialogColorPalette.findViewById(R.id.imageViewBlue);
        ImageView imageViewTextBackgroundCyan = dialogColorPalette.findViewById(R.id.imageViewCyan);
        ImageView imageViewTextBackgroundGreen = dialogColorPalette.findViewById(R.id.imageViewGreen);
        ImageView imageViewTextBackgroundRed = dialogColorPalette.findViewById(R.id.imageViewRed);
        ImageView imageViewTextBackgroundMagenta = dialogColorPalette.findViewById(R.id.imageViewMagenta);
        ImageView imageViewTextBackgroundYellow = dialogColorPalette.findViewById(R.id.imageViewYellow);
        ImageView imageViewTextBackgroundOrange = dialogColorPalette.findViewById(R.id.imageViewOrange);
        ImageView imageViewTextBackgroundWhite = dialogColorPalette.findViewById(R.id.imageViewWhite);
        ImageView imageViewTextBackgroundDarkGreen = dialogColorPalette.findViewById(R.id.imageViewDarkGreen);
        ImageView imageViewTextBackgroundPurple = dialogColorPalette.findViewById(R.id.imageViewPurple);

        imageViewTextBackgroundGray.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.gray));
        });
        imageViewTextBackgroundBrown.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.brown));
        });
        imageViewTextBackgroundBlue.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.blue));
        });
        imageViewTextBackgroundCyan.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.cyan));
        });
        imageViewTextBackgroundGreen.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.green));
        });
        imageViewTextBackgroundRed.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.red));
        });
        imageViewTextBackgroundMagenta.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.magenta));
        });
        imageViewTextBackgroundYellow.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.yellow));
        });
        imageViewTextBackgroundOrange.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.orange));
        });
        imageViewTextBackgroundWhite.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.white));
        });
        imageViewTextBackgroundPurple.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.purple));
        });
        imageViewTextBackgroundDarkGreen.setOnClickListener(v -> {
            applyTextBackgroundColorToNoteContent(dialogColorPalette, getResources().getColor(R.color.green));
        });

        dialogColorPalette.show();
    }

    private void applyTextBackgroundColorToNoteContent(Dialog dialogColorPalette, int valueOfSelectedColor) {
        note.setTextBackgroundColorValue(valueOfSelectedColor);
        editTextContent.setHighlightColor(note.getTextBackgroundColorValue());
        dialogColorPalette.hide();
    }

    private void configureAndShowFontSizeToolbar() {
        if(toolbarFontSize.getVisibility() == View.GONE) {
            toolbarFontSize.setVisibility(View.VISIBLE);
            Button buttonFontSizeMinus = findViewById(R.id.buttonFontSizeMinus);
            Button buttonFontSizePlus = findViewById(R.id.buttonFontSizePlus);
            TextView editTextFontSize = findViewById(R.id.textViewFontSize);

            buttonFontSizeMinus.setOnClickListener(v -> {
                int fontSize = Integer.parseInt(editTextFontSize.getText().toString())-1;
                editTextFontSize.setText(String.valueOf(fontSize));
            });
            buttonFontSizePlus.setOnClickListener(v -> {
                int fontSize = Integer.parseInt(editTextFontSize.getText().toString())+1;
                editTextFontSize.setText(String.valueOf(fontSize));
            });
        } else toolbarFontSize.setVisibility(View.GONE);
    }

    private void configureAndShowTextAlignmentToolbar() {
        if(toolbarTextAlignment.getVisibility() == View.GONE) {
            toolbarTextAlignment.setVisibility(View.VISIBLE);
            ImageView imageViewAlignLeft = findViewById(R.id.buttonAlignLeft);
            ImageView imageViewAlignCenter = findViewById(R.id.buttonAlignCenter);
            ImageView imageViewAlignRight = findViewById(R.id.buttonAlignRight);
            ImageView imageViewAlignJustify = findViewById(R.id.buttonAlignJustify);

            imageViewAlignLeft.setOnClickListener(v -> {

            });
            imageViewAlignCenter.setOnClickListener(v -> {

            });

            imageViewAlignRight.setOnClickListener(v -> {

            });
            imageViewAlignJustify.setOnClickListener(v -> {

            });
        } else toolbarTextAlignment.setVisibility(View.GONE);
    }

    public void hideAdvancedButtonsForAdvancedMode() {
            toolbarTextTools.setVisibility(View.GONE);
            toolbarTextAlignment.setVisibility(View.GONE);
            toolbarFontSize.setVisibility(View.GONE);
    }

    public static SpannableStringBuilder setSelectedTextSectionForSize(SpannableString selectedText, int fontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        return spannableStringBuilder;
    }
}