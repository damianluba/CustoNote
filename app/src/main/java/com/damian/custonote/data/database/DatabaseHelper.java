package com.damian.custonote.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.damian.custonote.data.model.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "DATABASE_CUSTONOTE";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_TIMESTAMP_CREATED = "TIMESTAMP_CREATED";
    public static final String COL_TIMESTAMP_MODIFIED = "TIMESTAMP_MODIFIED";
    public static final String COL_IS_BASIC_MODE = "IS_BASIC_MODE";
    public static final String COL_IS_FAVOURITE = "IS_FAVOURITE";
    public static final String COL_IS_SYNCHRONISED = "IS_SYNCHRONISED";
    public static final String FORMAT_DATE_TIME = "d MMM YYYY, HH:mm";

    //methods for notes managing
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + "" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT," +
                COL_CONTENT + " TEXT," +
                COL_IS_BASIC_MODE + " TEXT," +
                COL_TIMESTAMP_CREATED + " TEXT," +
                COL_TIMESTAMP_MODIFIED + " TEXT," +
                COL_IS_FAVOURITE + " TEXT," +
                COL_IS_SYNCHRONISED + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { //oldVersion, newVersion
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addNote(Note note) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TITLE, note.getTitle());
        contentValues.put(COL_CONTENT, note.getContent());
        note.setIsBasicMode(true);//TODO
        note.setIsFavourite(false);//TODO
        note.setIsSynchronised(false);//TODO
        contentValues.put(COL_IS_BASIC_MODE, (note.getIsBasicMode()?1:0));
        contentValues.put(COL_TIMESTAMP_CREATED,  LocalDateTime.now().toString());
        contentValues.put(COL_IS_FAVOURITE, (note.getIsFavourite()?1:0));
        contentValues.put(COL_IS_SYNCHRONISED, (note.getIsSynchronised()?1:0));

        database.insert(DATABASE_TABLE, null, contentValues);
        database.close();
    }

    public void updateNote(int ID, String newContent, String newTitle) {
        SQLiteDatabase database = this.getWritableDatabase();
       ContentValues updatedValues = new ContentValues();
        updatedValues.put(COL_TITLE, newTitle);
        updatedValues.put(COL_CONTENT, newContent);
        updatedValues.put(COL_TIMESTAMP_MODIFIED,  LocalDateTime.now().toString());
//        updatedValues.put(COL_IS_FAVOURITE,  new);
        updatedValues.put(COL_TIMESTAMP_MODIFIED,  LocalDateTime.now().toString());
        database.update(DATABASE_TABLE, updatedValues,  COL_ID + " = " + ID, null);
        database.close();
    }

    public void deleteNote(@NonNull Note note) {
        SQLiteDatabase database = this.getWritableDatabase();  //this is just in case if there is no parameters in
        // this function
        Cursor cursor = database.rawQuery("DELETE FROM " + DATABASE_TABLE + " where " + COL_ID + " = " + note.getId(), null);
    }

    public Cursor getData() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT*FROM " + DATABASE_TABLE, null); //
        return cursor;
    }

    public Cursor allNotes() {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.query(DATABASE_TABLE, new String[]{COL_ID, COL_TITLE, COL_CONTENT/*, COL_TIMESTAMP*/}, null,
                null, null, null, COL_TIMESTAMP_MODIFIED);
    }

    {
/*    public Note getNoteByID(int id) { //Cursor getNoteById TODO
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor record = database.query(
                DATABASE_TABLE, null *//*new String[]{COL_TITLE, COL_CONTENT*//**//*, COL_TIMESTAMP *//**//*, COL_IS_BASIC_MODE}*//*, "ID = ?", new String[]{Integer.toString(id)}, *//*String.valueOf*//*
                null,
                null,
                null);
        if(record == null)
            throw new IllegalArgumentException("There's no a note with id=" + id);
        else if(!record.moveToFirst())
            throw new IllegalArgumentException("Record not moved to first");

        String title = record.getString(1);
        String content = record.getString(2);
        Boolean isBasicMode = Boolean.parseBoolean(record.getString(3));
        Boolean isFavourite = Boolean.parseBoolean(record.getString(4));
        //        LocalDateTime timestamp = LocalDateTime.parse(record.getString(4), DateTimeFormatter.ofPattern(FORMAT_DATE_TIME));
        database.close();
        return new Note(id, title, content, isBasicMode*//*, timestamp*//*, isFavourite);
    }*/}

    public List<Note> findNoteWithPhrase(String phrase) {
        List<Note> foundNotes = null;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT*FROM " + DATABASE_TABLE + " WHERE " + COL_TITLE + " LIKE ?",
                new String[]{"%" + phrase + "%"}, null);

        if(cursor.moveToFirst()) {
            foundNotes = new ArrayList<Note>();
            do {
                Note note = new Note();
                note.setID(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setIsBasicMode(cursor.getInt(3)==1);
                note.setTimestampNoteCreated(LocalDateTime.parse(cursor.getString(4)));
                if(cursor.getString(5) != null) {
                    note.setTimestampNoteModified(LocalDateTime.parse(cursor.getString(5)));
                }
                note.setIsFavourite(cursor.getInt(6)==1);
                note.setIsSynchronised(cursor.getInt(7)==1);

                foundNotes.add(note);
            } while(cursor.moveToNext());
        }
        database.close();
        return foundNotes;
    }

    public List<Note> getNotes() {
        SQLiteDatabase database = this.getReadableDatabase();
        List<Note> allNotes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT*FROM " + DATABASE_TABLE, null);

        if(cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setID(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setIsBasicMode(cursor.getInt(3)==1);
                note.setTimestampNoteCreated(LocalDateTime.parse(cursor.getString(4)));
                if(cursor.getString(5) != null)
                    note.setTimestampNoteModified(LocalDateTime.parse(cursor.getString(5)));
                note.setIsFavourite(cursor.getInt(6)==1);
                note.setIsSynchronised(cursor.getInt(7)==1);

                allNotes.add(note);
            } while(cursor.moveToNext());
        }
        //https://stackoverflow.com/questions/51035283/cursorindexoutofboundsexception-index-0-requested-with-a-size
        // -of-0-in-android
        return allNotes;
    }
}