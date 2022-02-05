package com.damian.custonote.ui.all;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void selectNote() {

    }

    public void checkAllNotes() {

    }

    public void uncheckNote() {

    }

    public void uncheckAllNotes() {

    }

    public void markAsFavourite() {

    }
}