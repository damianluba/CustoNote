package com.damian.custonote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.damian.custonote.ui.SearchableActivity;
import com.damian.custonote.ui.all.AllFragment;
import com.damian.custonote.ui.favourites.FavouritesFragment;
import com.damian.custonote.ui.labels.LabelsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbarMain;
    TextView textViewUsername, textViewEmail;
    Button buttonLogIn;
    MenuItem menuItemImageUser, menuItemSearch;
    Dialog dialogBasicInfoAboutUser;
    FloatingActionButton fabAdd;
    Context context;
    BottomNavigationView bottomNavView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CustoNote); //the starting theme is @param splashScreenTheme, but after finished
        // loading it's changed to Theme_CustoNote
        setContentView(R.layout.activity_main);
        context = this;
        bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(bottomNavigationViewItemListener);
        fabAdd = findViewById(R.id.fabAdd);
        toolbarMain = findViewById(R.id.toolbar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_all, R.id.navigation_favourites, R.id.navigation_labels).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView, navController); //fragments switch's support
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
        navController.setGraph(navGraph);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main_activity, menu);
        menuItemImageUser = menu.findItem(R.id.itemImageUser);
        menuItemSearch = menu.findItem(R.id.menuItemSearch);

        menuItemImageUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showDialog();
                return false;
            }
        });

        menuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(context, SearchableActivity.class));
                return false;
            }
        });
        return true /*super.onCreateOptionsMenu(menu)*/;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationViewItemListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch(item.getItemId()) { //select among buttons placed on the bottom of the navigation bar
                case R.id.navigation_all:
                    selectedFragment = new AllFragment();
                    break;

                case R.id.navigation_favourites:
                    selectedFragment = new FavouritesFragment();
                    break;

                case R.id.navigation_labels:
                    selectedFragment = new LabelsFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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