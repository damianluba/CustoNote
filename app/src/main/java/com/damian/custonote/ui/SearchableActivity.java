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

import com.damian.custonote.R;
import com.damian.custonote.data.adapter.NotesAdapter;
import com.damian.custonote.data.database.DatabaseHelper;
import com.damian.custonote.data.model.Note;
import com.damian.custonote.databinding.ActivitySearchableBinding;

import java.util.List;

public class SearchableActivity extends AppCompatActivity {
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String phrase) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                List<Note> listFoundNotes = databaseHelper.findNoteWithPhrase(phrase);
                NotesAdapter notesAdapter = new NotesAdapter(getApplicationContext(), listFoundNotes, new NotesAdapter.OnSelectedNoteListener() {
                    @Override
                    public void onNoteClickListener(int position) {
                        Intent intent = new Intent(SearchableActivity.this, NoteActivity.class);
                        Note note = listFoundNotes.get(position);
                        intent.putExtra("bundleNote", note);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongNoteClickListener(int position) {
                        Toast.makeText(SearchableActivity.this, "longer press", Toast.LENGTH_LONG).show();
                    }
                });

                if(!phrase.isEmpty()) {
                    if(listFoundNotes != null) {
                        recyclerViewSearchedNotes.setVisibility(View.VISIBLE);
                        recyclerViewSearchedNotes.setAdapter(notesAdapter);
                    }
                    else { //there's no notes with the typed phrase
                        recyclerViewSearchedNotes.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "No notes with this phrase", Toast.LENGTH_LONG).show();
                    }
                } else recyclerViewSearchedNotes.setVisibility(View.GONE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

/*    @Override
    public void onNoteClickListener(Note note) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.putExtra("bundleNote", note);
        startActivity(intent);
    }*/

/*    @Override
    public void onLongNoteClickListener(Note note) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.putExtra("bundleNote", note);
        startActivity(intent);
    }*/


/*    @Override
    public void SelectedNoteToRemove(Note note) {
        Toast.makeText(this.getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
    }*/
}