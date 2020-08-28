package com.salab.project.kakikana.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

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

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init viewBinding class
        ActivityMainBinding mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Define top level destinations in which "UP" navigation will be hide and display bottom nav
        final Set<Integer> topLevelDestSet = new HashSet<>();
        topLevelDestSet.add(R.id.quiz_list_dest);
        topLevelDestSet.add(R.id.kana_list_dest);
        topLevelDestSet.add(R.id.scoreboard_dest);
        topLevelDestSet.add(R.id.profile_dest);

        // Get NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Setup Appbar
        setSupportActionBar(mBinding.toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelDestSet).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Setup bottom navigation
        final BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Hide bottom navigation in non-top-level destinations
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(topLevelDestSet.contains(destination.getId())) {
                // only show bottom navigation for top level destination
                bottomNav.setVisibility(View.VISIBLE);
            } else {
                bottomNav.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // setup "UP" navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // setup options menu navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }
}