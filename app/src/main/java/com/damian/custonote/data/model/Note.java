package com.damian.custonote.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Note implements Serializable {
    int ID, Photo;
    String title, content, st;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Boolean isBasicMode, isSynchronised, isFavourite;

    public Note(int ID, String title, String content/*, LocalDateTime timestampNoteCreated*/, Boolean isBasicMode, Boolean isFavourite) {
        this.ID = ID;
        this.title = title;
        this.content = content;
//        this.timestampNoteCreated = timestampNoteCreated;
        this.isBasicMode = isBasicMode;
        this.isFavourite = isFavourite;
    }

    public Note(String title, String content, Boolean isBasicMode, Boolean isFavourite, Boolean isSynchronised, LocalDateTime timestampNoteCreated, LocalDateTime timestampNoteModified) {
        this.title = title;
        this.content = content;
        this.isBasicMode = isBasicMode;
        this.isFavourite = isFavourite;
        this.isSynchronised = isSynchronised;
        this.timestampNoteCreated = timestampNoteCreated;
        this.timestampNoteModified = timestampNoteModified;
    }

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
        return "Note{" +
                "ID=" + ID +
                ", Photo=" + Photo +
                ", title='" + title +
                '\'' + ", content='" + content +
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

    public void setContent(String content) {
        this.content = content;
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

    public enum Color {
        Red, Blue, Yellow
    }
}