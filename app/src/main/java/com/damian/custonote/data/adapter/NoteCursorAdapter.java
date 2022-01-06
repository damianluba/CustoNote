package com.damian.custonote.data.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;

public class NoteCursorAdapter extends CursorAdapter {

    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public NoteCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        /*EditText editTextTitle = view.findViewById(R.id.);
        EditText editTextContent = view.findViewById(R.id.);

        String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
        String content = cursor.getString(cursor.getColumnIndexOrThrow("CONTENT"));

        editTextTitle.setText(title);
        editTextContent.setText(content);*/

    }
}