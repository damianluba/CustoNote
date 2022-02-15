package com.damian.custonote;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.damian.custonote.ui.SearchableActivity;
import com.damian.custonote.ui.all.AllFragment;
import com.damian.custonote.ui.favourites.FavouritesFragment;
import com.damian.custonote.ui.labels.LabelsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private final static int RC_SIGN_IN = 123;
    private final static String TAG = "MainActivity";
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;

//    private Toolbar toolbarMain;
    private TextView textViewUsername, textViewEmail;
    private SignInButton buttonLogIn;
    private ImageView imageViewUser;
    private MenuItem menuItemImageUser, menuItemSearch;
    private String username, email;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CustoNote); //the starting theme is @param splashScreenTheme, but after finished loading it's changed to Theme_CustoNote
        setContentView(R.layout.activity_main);

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

        createGoogleRequest();

    }

    @Override
    protected void onStart() {
        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(account != null) {   //TODO
            updateUI();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main_activity, menu);
        menuItemImageUser = menu.findItem(R.id.itemImageUser);
        menuItemSearch = menu.findItem(R.id.itemSearch);
        if(account != null) {
            Glide.with(MainActivity.this)
                    .load(account.getPhotoUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            menuItemImageUser.setIcon(resource);
                        }
                    });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
            Glide.with(this).asBitmap().load("https").into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    menuItemImageUser.setIcon(new BitmapDrawable(getResources(), resource));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemImageUser:
                showAccountDialog();
                return false;
            case R.id.itemSearch:
                startActivity(new Intent(MainActivity.this, SearchableActivity.class));
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
    };

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    public void showAccountDialog() {
        final Dialog dialogAccount = new Dialog(MainActivity.this);
        dialogAccount.setContentView(R.layout.layout_dialog_basic_info_about_user);
        textViewUsername = dialogAccount.findViewById(R.id.textViewUsername);
        buttonLogIn = dialogAccount.findViewById(R.id.buttonLogIn);
        textViewEmail = dialogAccount.findViewById(R.id.textViewEmail);
        imageViewUser = dialogAccount.findViewById(R.id.imageViewUser_dialog);

        if(account == null) { //if user not logged in
            //buttonLogIn is visible from the beginning
            textViewEmail.setVisibility(View.GONE);
            imageViewUser.setVisibility(View.GONE);
            buttonLogIn.setOnClickListener(v -> {
                intentActivityResultLauncher.launch(googleSignInClient.getSignInIntent());
            });
        } else {
            buttonLogIn.setVisibility(View.GONE);
            textViewUsername.setText(account.getDisplayName());
            textViewEmail.setText(account.getEmail());
            imageViewUser.setVisibility(View.VISIBLE);
            Glide.with(MainActivity.this)
                    .load(account.getPhotoUrl())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_warning)
                    .into(imageViewUser);
        }
        dialogAccount.show();
    }

    private void updateUI() {
        createGoogleRequest();
    }

    private void createGoogleRequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) { //that also make changes in dialogAccount
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);  //TODO
            username = account.getDisplayName();
            email = account.getEmail();
            photoUri = account.getPhotoUrl();
            updateUI();
            /*startActivity(MainActivity.this, HomeActivity.class);
            finish();*/
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            updateUI();
        }
    }

    /*private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

}