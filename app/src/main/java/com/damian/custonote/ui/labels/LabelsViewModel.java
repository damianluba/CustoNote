package com.damian.custonote.ui.labels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LabelsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LabelsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is labels fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}