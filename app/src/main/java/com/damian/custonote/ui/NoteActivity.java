package com.damian.custonote.ui;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.damian.custonote.R;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.ActivityNoteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityNoteBinding binding;
    private Note note;
    private TextView textViewTimestamp;
    private MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode, menuItemBackgroundColor, menuItemAddImage, menuItemSynchronisation;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private EditText editTextTitle,  editTextContent;
    private Intent intent;
    private Toolbar toolbarTextTools, toolbarTextAlignment, toolbarFontSize;
    private NestedScrollView noteBackground;
    private ImageView imageViewImageOfNote;
    private Uri uriPickedImage = null;
    private Bitmap bitmapImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private final static int REQUEST_CODE_ACCESS_MEMORY_PERMITTED = 2;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout.LayoutParams params;
    private static final String TAG = "NoteActivity";
    private boolean isNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //shows the arrow allowing to go back by pressing its
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        appBarLayout = findViewById(R.id.appBarLayout);
        params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        textViewTimestamp = findViewById(R.id.textViewTimestamp);
        toolbarTextTools = findViewById(R.id.toolbarTextTools);
        toolbarTextAlignment = findViewById(R.id.toolbarTextAlignment);
        toolbarFontSize = findViewById(R.id.toolbarFontSize);
        noteBackground = findViewById(R.id.nestedScrollView);
        imageViewImageOfNote = findViewById(R.id.imageViewImageOfNote);
        appBarLayout = findViewById(R.id.appBarLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayoutInNoteActivity);
        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayout.setLayoutParams(params);

        //------------------------------------------------RECEIVING DATA FROM PREVIOUS ACTIVITY and  NOTE CONFIGURATION  ----------------------------------------------------------
        intent = getIntent();
        if(intent.getExtras() != null)  //if there are delivered some data from previous activity
            retrieveNoteDataFromBundle();
         else  configureNewNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_note_activity, menu);
        menuItemSynchronisation = menu.findItem(R.id.itemSynchronisation);
        menuItemSave = menu.findItem(R.id.itemSave);
        menuItemDelete = menu.findItem(R.id.itemDelete_NoteActivity);
        menuItemStar = menu.findItem(R.id.itemStar);
        menuItemSwitchMode = menu.findItem(R.id.itemSwitchMode);
        menuItemBackgroundColor = menu.findItem(R.id.itemBackgroundColor);
        menuItemAddImage = menu.findItem(R.id.itemAddImage);

        //started buttons configuration
        coloriseStar();
        setUpNoteMode();

        menuItemSynchronisation.setVisible(!isNewNote);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemSynchronisation:
                note.setIsSynchronised(true);
                addNoteToFirebase(note);
                return true;

            case R.id.itemSave:
                textViewTimestamp.setVisibility(View.VISIBLE);

                if(note.getId() == 0)
                    insertNote();
                else updateNote();

                note.setIsSynchronised(false); //a note was changed
                menuItemSynchronisation.setIcon(getDrawable(R.drawable.ic_not_synchronised)); //so it already isn't synchronised

                Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG).show();
                database.close();
                return true;

            case R.id.itemDelete_NoteActivity:
                databaseHelper.deleteNote(note);
                return true;

            case R.id.itemStar:
                databaseHelper = new DatabaseHelper(NoteActivity.this);
                note.setIsFavourite(!note.getIsFavourite());
                coloriseStar();
                database = databaseHelper.getWritableDatabase();
                databaseHelper.markOrUnmarkNoteAsFavourite(note.getId(), note.getIsFavourite());
                database.close();
                return true;

            case R.id.itemSwitchMode:
                goThroughChangingNoteMode();
                return true;

            case R.id.itemBackgroundColor:
                configureAndShowDialogColorPaletteForBackground();
                return true;

            case R.id.itemAddImage:
                checkAndRequestForPermission();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void insertNote() {
        note.setTimestampNoteCreated(LocalDateTime.now());
        textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

        //here attributes isFavourite and isBasicMode aren't saved -  as only a user clicks marks a note as favourite, makes changes in note data
        note.setTitle(editTextTitle.getText().toString());
        setScriptedContent(editTextContent.getText());

        if(bitmapImage != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            note.setImage(outputStream.toByteArray());
        } else {
            note.setImage(null);
        }
        databaseHelper = new DatabaseHelper(NoteActivity.this);
        database = databaseHelper.getWritableDatabase();
        databaseHelper.addNote(note);
    }

    private void updateNote() {
        databaseHelper = new DatabaseHelper(NoteActivity.this);
        database = databaseHelper.getWritableDatabase();
        String scriptedNoteContent = getScriptedContent(editTextContent.getText());
        note.setTimestampNoteModified(LocalDateTime.now());
        if(bitmapImage != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            note.setImage(outputStream.toByteArray());
        } else {
            note.setImage(null);
        }
        databaseHelper.updateNote(note.getId(), editTextTitle.getText().toString(), scriptedNoteContent, note.getIsFavourite(), note.getBackgroundColorValue(), note.getImage());

        textViewTimestamp.setText(
                "Modified: " + note.getTimestampNoteModified().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) +
                        "\nCreated: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
    }

    public void showSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextContent.requestFocus();
        imm.showSoftInput(editTextContent, InputMethodManager.SHOW_IMPLICIT);
    }

    private void configureNewNote() {
        textViewTimestamp.setVisibility(View.GONE);
        appBarLayout.setExpanded(false);
        note = new Note(null, null, true, false, false, null, null, Color.WHITE, null);
        isNewNote = true;
        showSystemKeyboard();
    }

    private void retrieveNoteDataFromBundle() {
        isNewNote = false;
        note = (Note) intent.getSerializableExtra("bundleNote");
        textViewTimestamp.setVisibility(View.VISIBLE);

        editTextTitle.setText(note.getTitle());
        editTextContent.setText(HtmlCompat.fromHtml(note.getContent() , HtmlCompat.FROM_HTML_MODE_LEGACY));
        note.displayReadableContent(note.getContent());
        noteBackground.setBackgroundColor(note.getBackgroundColorValue()); //make a background

        if(note.getIsBasicMode())
            hideAdvancedElementsForAdvancedMode();
        else configureAdvancedElementsForAdvancedMode();

        if(note.getTimestampNoteModified() == null) //if a note was modified before
            textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
         else textViewTimestamp.setText("Created: " + note.getTimestampNoteCreated().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME))
                    + "\nModified: " + note.getTimestampNoteModified().format(DateTimeFormatter.ofPattern(DatabaseHelper.FORMAT_DATE_TIME)));
    }

    private void configureAdvancedElementsForAdvancedMode() {
        if(note.getImage() != null) {
            Glide.with(NoteActivity.this).load(note.getImage()).into(imageViewImageOfNote);
        }
        TextView textViewBold = findViewById(R.id.textViewBold);
        textViewBold.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchBoldStyle(textViewBold, spannableString);
            editTextContent.setText(spannableString);
        });

        TextView textViewItalic = findViewById(R.id.textViewItalic);
        textViewItalic.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchItalicStyle(textViewItalic, spannableString);
            editTextContent.setText(spannableString);
        });

        TextView textViewUnderline = findViewById(R.id.textViewUnderline);
        textViewUnderline.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            switchUnderlineStyle(textViewUnderline, spannableString);
            editTextContent.setText(spannableString);
        });

        TextView textViewFontSize = findViewById(R.id.textViewFontSize);
        textViewFontSize.setOnClickListener(v -> {
            configureAndShowFontSizeToolbar();
        });

        TextView textViewTextColor = findViewById(R.id.textViewTextColor);
        textViewTextColor.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            configureAndShowDialogColorPaletteForTextColor(textViewTextColor, spannableString);
        });

        TextView textViewTextHighlightColor = findViewById(R.id.textViewTextHighlightColor);
        textViewTextHighlightColor.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            configureAndShowDialogColorPaletteForTextHighlight(textViewTextHighlightColor, spannableString);

        });

        ImageView imageViewAlign = findViewById(R.id.imageViewAlign);
        imageViewAlign.setOnClickListener(v -> {
            configureAndShowTextAlignmentToolbar();
            /*Spannable spannableString = new SpannableStringBuilder(editTextContent.getText());
            spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editTextContent.setText(spannableString);*/
        });
    }

    private void switchBoldStyle(TextView textViewBold, Spannable spannableString) {
        if(editTextContent.getSelectionStart() != BOLD) { //make the text bold
            spannableString.setSpan(new StyleSpan(BOLD), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textViewBold.setBackgroundColor(Color.GRAY);
        } else { //make the text non-bold
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.removeSpan(editTextContent);
            textViewBold.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void switchItalicStyle(TextView textViewItalic, Spannable spannableString) { //style of text of textView is changed so this parameter takes place here
        if(editTextContent.getSelectionStart() != ITALIC) { //make the text italic
            spannableString.setSpan(new StyleSpan(ITALIC), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textViewItalic.setBackgroundColor(Color.GRAY);
        } else { //make the text non-italic
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textViewItalic.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void switchUnderlineStyle(TextView textViewUnderline, Spannable spannableString) {
        if(editTextContent.getSelectionStart() != Paint.UNDERLINE_TEXT_FLAG) { //make the text underlined
            spannableString.setSpan(new UnderlineSpan(), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textViewUnderline.setBackgroundColor(Color.GRAY);
        } else { //make the text non-underlined
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textViewUnderline.setBackgroundColor(Color.TRANSPARENT);
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
            menuItemAddImage.setVisible(false);
        } else {
            //TURN ON ADVANCED MODE
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.BLUE)); //setting the color of the item
            toolbarTextTools.setVisibility(View.VISIBLE);
            menuItemAddImage.setVisible(true);
            configureAdvancedElementsForAdvancedMode();
        }
    }

    private void goThroughChangingNoteMode() {
        if(note.getIsBasicMode()) {
            //TURN ON ADVANCED MODE
            note.setIsBasicMode(false);
            menuItemSwitchMode.setIconTintList(ColorStateList.valueOf(Color.BLUE)); //setting the color of the item
            toolbarTextTools.setVisibility(View.VISIBLE);
            menuItemAddImage.setVisible(true);
            configureAdvancedElementsForAdvancedMode();
        } else {
            //TURN OFF ADVANCED MODE
            new AlertDialog.Builder(NoteActivity.this)
                    .setTitle("You're about to remove any text formatting and photo. Are you sure?")
                    .setIcon(getDrawable(R.drawable.ic_warning))
                    .setPositiveButton("Yes, I need only basic features now", (dialog, which) -> {
                        clearFormatting(note.getContent());
                        note.setImage(null);
                        imageViewImageOfNote.setImageDrawable(null);
                        note.setIsBasicMode(true);
                        setUpNoteMode();
                    })
                    .setNegativeButton("No, I don't want to change mode", null)
                    .show();
        }
    }

    public void setScriptedContent(Spanned spanned) {
        String scriptedContent = HtmlCompat.toHtml(spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        note.setContent(scriptedContent);
    }

    public String getScriptedContent(Spanned spanned) {
        return HtmlCompat.toHtml(spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
    }

    private void clearFormatting(String scriptedContent) {//clear spans
        Spanned spannedContent = HtmlCompat.fromHtml(scriptedContent, HtmlCompat.FROM_HTML_MODE_LEGACY);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannedContent);
        spannableStringBuilder.clearSpans();
        setScriptedContent(spannableStringBuilder);
    }

    private void configureAndShowDialogColorPaletteForBackground() {
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
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.gray));
        });
        imageViewBackgroundBrown.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.brown));
        });
        imageViewBackgroundBlue.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.blue));
        });
        imageViewBackgroundCyan.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.cyan));
        });
        imageViewBackgroundGreen.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.green));
        });
        imageViewBackgroundRed.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.red));
        });
        imageViewBackgroundMagenta.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.magenta));
        });
        imageViewBackgroundYellow.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.yellow));
        });
        imageViewBackgroundOrange.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.orange));
        });
        imageViewBackgroundWhite.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.white));
        });
        imageViewBackgroundPurple.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.purple));
        });
        imageViewBackgroundDarkGreen.setOnClickListener(v -> {
            applyBackgroundColorToNote(dialogColorPalette, getColor(R.color.dark_green));
        });

        dialogColorPalette.show();
    }

    private void applyBackgroundColorToNote(Dialog dialogColorPalette, int valueOfSelectedColor) {
        note.setBackgroundColorValue(valueOfSelectedColor);
        noteBackground.setBackgroundColor(valueOfSelectedColor);
        dialogColorPalette.hide();
    }

    private void configureAndShowDialogColorPaletteForTextColor(TextView textViewTextColor, Spannable spannableString) {
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
            applyTextColor(dialogColorPalette, Color.GRAY, textViewTextColor, spannableString);
        });
        imageViewTextColorBrown.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, getColor(R.color.brown), textViewTextColor, spannableString);
        });
        imageViewTextColorBlue.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.BLUE, textViewTextColor, spannableString);
        });
        imageViewTextColorCyan.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.CYAN, textViewTextColor, spannableString);
        });
        imageViewTextColorGreen.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.GREEN, textViewTextColor, spannableString);
        });
        imageViewTextColorRed.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.RED, textViewTextColor, spannableString);
        });
        imageViewTextColorMagenta.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.MAGENTA, textViewTextColor, spannableString);
        });
        imageViewTextColorYellow.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.YELLOW, textViewTextColor, spannableString);
        });
        imageViewTextColorOrange.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, getColor(R.color.orange), textViewTextColor, spannableString);
        });
        imageViewTextColorWhite.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, Color.WHITE, textViewTextColor, spannableString);
        });
        imageViewTextColorPurple.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, getColor(R.color.purple), textViewTextColor, spannableString);
        });
        imageViewTextColorDarkGreen.setOnClickListener(v -> {
            applyTextColor(dialogColorPalette, getColor(R.color.dark_green), textViewTextColor, spannableString);
        });

        dialogColorPalette.show();
    }

    private void applyTextColor(Dialog dialogColorPalette, int valueOfSelectedColor, TextView textViewTextColor, Spannable spannableString) {
        spannableString.setSpan(new ForegroundColorSpan(valueOfSelectedColor), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textViewTextColor.setTextColor(valueOfSelectedColor);
        editTextContent.setText(spannableString);
        dialogColorPalette.hide();
    }

    private void configureAndShowDialogColorPaletteForTextHighlight(TextView textViewHighlightColor, Spannable spannableString) {
        Dialog dialogColorPalette = new Dialog(NoteActivity.this);
        dialogColorPalette.setContentView(R.layout.layout_dialog_color_palette);
        TextView textViewChooseColorForTextBackground = dialogColorPalette.findViewById(R.id.textView);
        textViewChooseColorForTextBackground.setText("choose a color of text highlight");

        ImageView imageViewTextHighlightGray = dialogColorPalette.findViewById((R.id.imageViewGray));
        ImageView imageViewTextHighlightBrown = dialogColorPalette.findViewById(R.id.imageViewBrown);
        ImageView imageViewTextHighlightBlue = dialogColorPalette.findViewById(R.id.imageViewBlue);
        ImageView imageViewTextHighlightCyan = dialogColorPalette.findViewById(R.id.imageViewCyan);
        ImageView imageViewTextHighlightGreen = dialogColorPalette.findViewById(R.id.imageViewGreen);
        ImageView imageViewTextHighlightRed = dialogColorPalette.findViewById(R.id.imageViewRed);
        ImageView imageViewTextHighlightMagenta = dialogColorPalette.findViewById(R.id.imageViewMagenta);
        ImageView imageViewTextHighlightYellow = dialogColorPalette.findViewById(R.id.imageViewYellow);
        ImageView imageViewTextHighlightOrange = dialogColorPalette.findViewById(R.id.imageViewOrange);
        ImageView imageViewTextHighlightWhite = dialogColorPalette.findViewById(R.id.imageViewWhite);
        ImageView imageViewTextHighlightDarkGreen = dialogColorPalette.findViewById(R.id.imageViewDarkGreen);
        ImageView imageViewTextHighlightPurple = dialogColorPalette.findViewById(R.id.imageViewPurple);

        imageViewTextHighlightGray.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.gray), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightBrown.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.brown), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightBlue.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.blue), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightCyan.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.cyan), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightGreen.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.green), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightRed.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.red), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightMagenta.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.magenta), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightYellow.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.yellow), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightOrange.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.orange), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightWhite.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.white), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightPurple.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.purple), textViewHighlightColor, spannableString);
        });
        imageViewTextHighlightDarkGreen.setOnClickListener(v -> {
            applyTextHighlightColor(dialogColorPalette, getColor(R.color.dark_green), textViewHighlightColor, spannableString);
        });

        dialogColorPalette.show();
    }

    private void applyTextHighlightColor(Dialog dialogColorPalette, int valueOfSelectedColor, TextView textViewTextHighlightColor, Spannable spannableString) {
        spannableString.setSpan(new BackgroundColorSpan(valueOfSelectedColor), editTextContent.getSelectionStart(), editTextContent.getSelectionEnd(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textViewTextHighlightColor.setBackgroundTintList(ColorStateList.valueOf(valueOfSelectedColor)); //the textView has the background grey frame so it's enough to set tint
        editTextContent.setText(spannableString);
        dialogColorPalette.hide();
    }

    private void configureAndShowFontSizeToolbar() {
        if(toolbarFontSize.getVisibility() == View.GONE) {
            toolbarFontSize.setVisibility(View.VISIBLE);
            Button buttonFontSizeMinus = findViewById(R.id.buttonFontSizeMinus);
            Button buttonFontSizePlus = findViewById(R.id.buttonFontSizePlus);
            TextView editTextFontSize = findViewById(R.id.textViewFontSizeTRUE);

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

    private void setCollapsingToolbarLayout() {
        if(imageViewImageOfNote.getDrawable() != null) {
            appBarLayout.setExpanded(true);
            appBarLayout.setAlpha(0.6F);
            params.height = 800;
        } else {
            appBarLayout.setExpanded(false);
        }
    }

    private void hideAdvancedElementsForAdvancedMode() {
            toolbarTextTools.setVisibility(View.GONE);
            toolbarTextAlignment.setVisibility(View.GONE);
            toolbarFontSize.setVisibility(View.GONE);
    }

    private static SpannableStringBuilder setSelectedTextSectionForSize(SpannableString selectedText, int fontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        return spannableStringBuilder;
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(NoteActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //if reading storage permission not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(NoteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(NoteActivity.this, "Reading storage permission is required", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(NoteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ACCESS_MEMORY_PERMITTED);
            }
        } else openGallery();
    }

    //choosing an image
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                uriPickedImage = result.getData().getData();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uriPickedImage);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmapImage = BitmapFactory.decodeStream(inputStream);
                imageViewImageOfNote.setImageBitmap(bitmapImage);

                Toast.makeText(NoteActivity.this, "Chosen image", Toast.LENGTH_SHORT).show();
                setCollapsingToolbarLayout();
            }
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openGallery();
        else Toast.makeText(NoteActivity.this, "Reading storage permission is required", Toast.LENGTH_SHORT).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openGallery() {
        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*"); //setting type of intent
        resultLauncher.launch(intentGallery);
    }

    private void addNoteToFirebase(Note note) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("title", note.getTitle());
        noteData.put("content", note.getContent());
        noteData.put("isBasicMode", note.getIsBasicMode());
        noteData.put("timestampNoteCreated", note.getTimestampNoteCreated());
        noteData.put("timestampNoteModified", note.getTimestampNoteModified());
        //note.isSynchronised isn't put here because as only note is uploaded to cloud is synchronised, the same situation in opposite direction
        noteData.put("isFavourite", note.getIsFavourite());
        noteData.put("backgroundColorValue", note.getBackgroundColorValue());

        FirebaseFirestore database = FirebaseFirestore.getInstance(); //? firebaseStore
        StorageReference folder = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference firebaseImagePath = folder.child(uriPickedImage.getLastPathSegment());

        firebaseImagePath.putFile(uriPickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                noteData.put("imageUri", firebaseImagePath.getDownloadUrl());
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NoteActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("IMAGE_UPLOADING", e.getMessage());
                    }
        });

        database.collection("notes")
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    menuItemSynchronisation.setIcon(getDrawable(R.drawable.ic_synchronised));
//                    Toast.makeText(this, "Note synchronised", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                });
    }
}