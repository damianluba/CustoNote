package com.damian.custonote.data.model;

import android.text.Spanned;

import androidx.core.text.HtmlCompat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Note implements Serializable {
    int ID, Photo;
    String title, content;
    byte[] image;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Boolean isBasicMode, isSynchronised, isFavourite, isSelectedToRemove;
    int backgroundColorValue, backgroundTextColorValue, textColorValue;

    public Note(String title, String content, Boolean isBasicMode, Boolean isFavourite, Boolean isSynchronised,
                LocalDateTime timestampNoteCreated, LocalDateTime timestampNoteModified, int backgroundColorValue, byte[] image) {
        this.title = title;
        this.content = content;
        this.isBasicMode = isBasicMode;
        this.timestampNoteCreated = timestampNoteCreated;
        this.timestampNoteModified = timestampNoteModified;
        this.isFavourite = isFavourite;
        this.isSynchronised = isSynchronised;
        this.backgroundColorValue = backgroundColorValue;
        this.image = image;
    }

    /*public Note(int ID, String title, String content*//*, LocalDateTime timestampNoteCreated*//*, Boolean isBasicMode, Boolean isFavourite) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        //        this.timestampNoteCreated = timestampNoteCreated;
        this.isBasicMode = isBasicMode;
        this.isFavourite = isFavourite;
    }
    public Note(String title, String content, Boolean isBasicMode, LocalDateTime timestampNoteCreated, LocalDateTime timestampNoteModified, int backgroundColorValue) {
        this.title = title;
        this.content = content;
        this.isBasicMode = isBasicMode;
        this.timestampNoteModified = timestampNoteModified;
        this.backgroundColorValue = backgroundColorValue;
    }*/

    public Note(int ID) {
        this.ID = ID;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Note() {

    }

    @Override
    public String toString() {
        return "Note{" + "ID=" + ID + ", Photo=" + Photo + ", title='" + title + '\'' + ", content='" + content +
                //                '\'' + ", timestampNoteCreated=" + timestampNoteCreated +
                '}';
    }

    public int getId() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { //note content is kept as HTML scripted in String
        this.content = content;
    }

    public Spanned displayReadableContent(String scriptedContent) {
        return HtmlCompat.fromHtml(scriptedContent, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    public Boolean getIsBasicMode() {
        return isBasicMode;
    }

    public void setIsBasicMode(Boolean isBasicMode) {
        this.isBasicMode = isBasicMode;
    }

    public LocalDateTime getTimestampNoteCreated() {
        return timestampNoteCreated;
    }

    public void setTimestampNoteCreated(LocalDateTime timestampNoteCreated) {
        this.timestampNoteCreated = timestampNoteCreated;
    }

    public LocalDateTime getTimestampNoteModified() {
        return timestampNoteModified;
    }

    public void setTimestampNoteModified(LocalDateTime timestampNoteModified) {
        this.timestampNoteModified = timestampNoteModified;
    }

    public int getPhoto() {
        return Photo;
    }

    public void setPhoto(int photo) {
        Photo = photo;
    }

    public Boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public Boolean getIsSynchronised() {
        return isSynchronised;
    }

    public void setIsSynchronised(Boolean isSynchronised) {
        this.isSynchronised = isSynchronised;
    }

    public int getBackgroundColorValue() {
        return backgroundColorValue;
    }

    public void setBackgroundColorValue(int backgroundColorValue) {
        this.backgroundColorValue = backgroundColorValue;
    }

    public int getTextBackgroundColorValue() {
        return backgroundTextColorValue;
    }

    public void setTextBackgroundColorValue(int backgroundTextColorValue) {
        this.backgroundTextColorValue = backgroundTextColorValue;
    }

    public int getTextColorValue() {
        return textColorValue;
    }

    public void setTextColorValue(int textColorValue) {
        this.textColorValue = textColorValue;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
/*
    //colors for note parameterising
    public Interface chooseColor() {
        */
/*final BackgroundColorSpan backgroundColorSpan;
        final ForegroundColorSpan foregroundColorSpan;*//*

        final Color textColor = Color.valueOf(Color.BLACK);
        return new ColorInterface(textColor);
    }
}

public class ColorInterface implements Interface {
    */
/*final BackgroundColorSpan backgroundColorSpan;
    final ForegroundColorSpan foregroundColorSpan;*//*

    private final Color textColor;

    public ColorInterface(Color color) {
        this.textColor = color;
    }

    public Color getTextColor() {
        return textColor;
    }
}*/