package com.damian.custonote.ui.all;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Boolean> selection;

    public AllViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}