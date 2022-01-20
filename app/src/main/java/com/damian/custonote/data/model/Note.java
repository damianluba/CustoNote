package com.damian.custonote.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Note implements Serializable {
    int ID, Photo;
    String title, content;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Boolean isBasicMode;


    public Note(int ID, String title, String content/*, LocalDateTime timestampNoteCreated*/, Boolean isBasicMode) {
        this.ID = ID;
        this.title = title;
        this.content = content;
//        this.timestampNoteCreated = timestampNoteCreated;
        this.isBasicMode = isBasicMode;
    }

    public Note(int ID) {
        this.ID = ID;
    }

    public Note(String title, String content/*, LocalDateTime timestampNoteCreated*/) {
        this.title = title;
        this.content = content;
        //        this.timestampNoteCreated = timestampNoteCreated;
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

    public Boolean getIsBasicMode() {
        return isBasicMode;
    }

    public void setIsBasicMode(Boolean isBasicMode) {
        this.isBasicMode = isBasicMode;
    }
}