package com.damian.custonote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.damian.custonote.ui.NoteActivity;
import com.damian.custonote.ui.SearchableActivity;
import com.damian.custonote.ui.all.AllFragment;
import com.damian.custonote.ui.favourites.FavouritesFragment;
import com.damian.custonote.ui.labels.LabelsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbarMain;
    private TextView textViewUsername, textViewEmail;
    private Button buttonLogIn;
    private MenuItem menuItemImageUser, menuItemSearch;
    private Dialog dialogBasicInfoAboutUser;
    private Context context;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CustoNote); //the starting theme is @param splashScreenTheme, but after finished loading it's changed to Theme_CustoNote
        setContentView(R.layout.activity_main);

        context = this;
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(bottomNavigationViewItemListener);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_all, R.id.navigation_favourites, R.id.navigation_labels).build();
        NavController navController = Navigation.findNavController(MainActivity.this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavView, navController); //fragments switch's support
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        /*NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
        navController.setGraph(navGraph);*/

        /*firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.currentUser;*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main_activity, menu);
        menuItemImageUser = menu.findItem(R.id.itemImageUser);
        menuItemSearch = menu.findItem(R.id.itemSearch);
//        Glide.with(this).load(currentUser.photoUrl).into(menuItemImageUser);
//        menuItemImageUser.setIcon()
//        onresourceReady
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (true) {
            Glide.with(this).asBitmap().load("https").into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    menuItemImageUser.setIcon(new BitmapDrawable(getResources(), resource));

                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemImageUser:
                showDialog();
                return false;
            case R.id.itemSearch:
                startActivity(new Intent(context, SearchableActivity.class));
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationViewItemListener = item -> {
        Fragment selectedFragment = null;
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        switch(item.getItemId()) { //select among buttons placed on the bottom of the navigation bar
            case R.id.navigation_all:
                selectedFragment = new AllFragment();
                fabAdd.setImageResource(R.drawable.ic_add_note);
                fabAdd.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, NoteActivity.class)));
                break;

            case R.id.navigation_favourites:
                selectedFragment = new FavouritesFragment();
                fabAdd.setImageResource(R.drawable.ic_add_favourite);
                fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Favourite notes", Toast.LENGTH_LONG).show();
                    }
                });

                break;

            case R.id.navigation_labels:
                selectedFragment = new LabelsFragment();
                fabAdd.setImageResource(R.drawable.ic_add_label);
                fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Labels", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    public void showDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog_basic_info_about_user);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        dialog.show();
        // creates an instance of the dialog fragment and shows it
    }
}