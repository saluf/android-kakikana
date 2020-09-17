package com.salab.project.kakikana.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.salab.project.kakikana.R;
import com.salab.project.kakikana.databinding.ActivityMainBinding;

import java.util.HashSet;
import java.util.Set;

/**
 * This application applies single-activity architecture. The Activity are responsible to
 * host NavHost (container of destinations) and global components like appbar and bottom navigation.
 */
public class MainActivity extends AppCompatActivity {

    // constants
    private static final String TAG = MainActivity.class.getSimpleName();

    // global variables
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;

    private Set<Integer> mTopLevelDestSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // disable landscape for small device
        // ref: https://stackoverflow.com/questions/9627774/android-allow-portrait-and-landscape-for-tablets-but-force-portrait-on-phone
        if (!getResources().getBoolean(R.bool.isTablet)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // init viewBinding class
        ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Define top level destinations in which "UP" navigation will be hide and display bottom nav
        mTopLevelDestSet.add(R.id.quiz_list_dest);
        mTopLevelDestSet.add(R.id.kana_list_dest);
        mTopLevelDestSet.add(R.id.scoreboard_dest);
        mTopLevelDestSet.add(R.id.profile_dest);

        // Get NavController
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Setup Appbar
        setSupportActionBar(mBinding.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(mTopLevelDestSet).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        // Setup bottom navigation
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        // Adjust appbar, bottom nav components based on destination
        registerOnDestinationChangedListener();

    }

        @Override
    public boolean onSupportNavigateUp() {
        // setup "UP" navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // setup options menu navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    private void registerOnDestinationChangedListener() {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // only show bottom navigation for top level destination
            if (mTopLevelDestSet.contains(destination.getId())) {
                mBottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                mBottomNavigationView.setVisibility(View.GONE);
            }
            // hide "UP" navigation icon at quiz result dest to prevent it back to quiz dest
            if (destination.getId() == R.id.quiz_result_dest) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
            // hide support action bar
            if (destination.getId() == R.id.loginFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
            }
        });
    }
}