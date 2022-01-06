package com.damian.custonote.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.custonote.NoteActivity;
import com.damian.custonote.R;
import com.damian.custonote.data.adapter.NotesAdapter;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.ActivitySearchableBinding;

import java.util.List;

public class SearchableActivity extends AppCompatActivity implements NotesAdapter.SelectedNote {
    private ActivitySearchableBinding binding;
    SearchView searchView;
    MenuItem menuItemSearchView;
    RecyclerView recyclerViewSearchedNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleIntent(getIntent());
        recyclerViewSearchedNotes = findViewById(R.id.recyclerViewSearchedNotes);
        recyclerViewSearchedNotes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_searchable_activity, menu);
        menuItemSearchView = menu.findItem(R.id.itemSearchView);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuItemSearchView.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.onActionViewExpanded(); //searchView is expanded and shows software keyboard automatically when this activity is opened
//        searchView.setSubmitButtonEnabled(true); //additional button for submitting
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                          return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext());
                List<Note> listFoundNotes = databaseHelper.findNoteWithPhrase(s);
                NotesAdapter notesAdapter = new NotesAdapter(getBaseContext(), listFoundNotes, SearchableActivity.this::selectedNote);
                recyclerViewSearchedNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getBaseContext(), NoteActivity.class));
                    }
                });
                if(listFoundNotes != null)
                    recyclerViewSearchedNotes.setAdapter(notesAdapter);
                else Toast.makeText(getApplicationContext(), "No notes with this phrase", Toast.LENGTH_LONG).show();

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

/*    @Override
    protected void onNewIntent(Intent intent) {
             handleIntent(intent);
    }*/

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public void selectedNote(Note note) {
        startActivity(new Intent(getApplicationContext(), NoteActivity.class).putExtra("data", note));
    }
}