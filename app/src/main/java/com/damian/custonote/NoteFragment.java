package com.damian.custonote;

import androidx.fragment.app.Fragment;

public class NoteFragment extends Fragment {
    /*private AppBarConfiguration appBarConfiguration;
//    private ActivityNoteBinding binding;
    Note note;
    TextView textViewTimestamp;
    EditText editTextTitle_contentNote,  editTextContent_contentNote;
    FloatingActionButton fabEditNote;
    MenuItem menuItemSave, menuItemStar, menuItemDelete, menuItemSwitchMode;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    LocalDateTime timestampNoteCreated, timestampNoteModified;
    Context context;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note, container, false);
//        AppBarLayout appBarLayout = root.findViewById(R.id.appBarLayout);
        Toolbar toolbar = root.findViewById(R.id.toolbar_note);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.inflateMenu(R.menu.action_bar_note_fragment);
        CollapsingToolbarLayout collapsingToolbarLayout = root.findViewById(R.id.collapsingToolbarLayout);
        *//*actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*//*
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        context= this.getContext();
        fabEditNote = root.findViewById(R.id.fabEditNote);
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        Navigation.findNavController(getView()).navigate(R.id.action_navigation_all_to_navigation_note);
        fabEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit the note", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        editTextTitle_contentNote = root.findViewById(R.id.editTextTitle_contentNote);
        editTextContent_contentNote = root.findViewById(R.id.editTextContent_contentNote);
        textViewTimestamp = root.findViewById(R.id.textViewTimestamp);
//        intent = getIntent();

        //GETTING ARGUMENTS
        Bundle noteId = getArguments();
        if(noteId != null) { //if there are delivered some data from previous fragment
            //it means that the note already exists and is opened in view mode
            int receivedNoteId = noteId.getInt("noteId");
//            editTextTitle_contentNote.setText(noteId.getString("noteTitle"));
//            editTextContent_contentNote.setText(noteId.getString("noteContent"));

            note = new Note(receivedNoteId);
            editTextTitle_contentNote.setText(String.valueOf(note.getId()));
            editTextContent_contentNote.setText(note.getContent());
        } else { //there aren't delivered any information so the note is empty
            editTextContent_contentNote.requestFocus();
        }
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_note_fragment, menu);
        menuItemStar = menu.findItem(R.id.itemStar);
        menuItemSave = menu.findItem(R.id.itemSave);
        menuItemDelete = menu.findItem(R.id.itemDelete);
        menuItemSwitchMode = menu.findItem(R.id.itemSwitchMode);

        menuItemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // SAVING A NOTE
            @Override        //save a note
            public boolean onMenuItemClick(MenuItem menuItem) {
                timestampNoteModified = LocalDateTime.now(); //it needs to be done objectively

                if(timestampNoteCreated == null) {//if a note was already opened as a new note
                    timestampNoteCreated = timestampNoteModified;
                    textViewTimestamp.setText("Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));

                    Note note = new Note(editTextTitle_contentNote.getText().toString(),
                            editTextContent_contentNote.getText().toString());
                    databaseHelper = new DatabaseHelper(context);
                    database = databaseHelper.getWritableDatabase();
                    databaseHelper.addNote(note);
                    Toast.makeText(context, "Note saved", Toast.LENGTH_LONG).show();
                }
                else {
                    databaseHelper.updateNote(note.getId(), editTextContent_contentNote.getText().toString(),
                            editTextTitle_contentNote.getText().toString(), timestampNoteCreated);
                    databaseHelper.getDatabaseName();
                    textViewTimestamp.setText("Modified: " + timestampNoteModified.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)) + "\nCreated: " + timestampNoteCreated.format(DateTimeFormatter.ofPattern(databaseHelper.FORMAT_DATE_TIME)));
                }
                    return false;
            }
        });

        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                databaseHelper.deleteNote(note);
                return false;
            }
        });

        menuItemStar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return false;
        }
    });

        menuItemSwitchMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //             if(menuItemSwitchMode.getIconTintList().equals(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))))
            Toolbar toolbarTextTools = (Toolbar) menu.findItem(R.id.toolbarTextTools);
            if(note.getIsBasicMode().equals(true)) {
                toolbarTextTools.setVisibility(View.GONE); //turn off advanced mode
                menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white))); //setting the color of the item
            } else {
                toolbarTextTools.setVisibility(View.VISIBLE); //turn on advanced mode
                menuItemSwitchMode.setIconTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.blue))); //setting the color of the item
            }
            note.setIsBasicMode(!note.getIsBasicMode());  //set the opposite mode
            return false;
        }
    });

        super.onCreateOptionsMenu(menu, inflater);
    }*/
}