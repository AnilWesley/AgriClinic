package com.novaagritech.agriclinic.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.firebase.ForceUpdateChecker;
import com.novaagritech.agriclinic.fragments.AboutFragment;
import com.novaagritech.agriclinic.fragments.EventsFragment;
import com.novaagritech.agriclinic.fragments.HelpFragment;
import com.novaagritech.agriclinic.fragments.HomeFragment1;
import com.novaagritech.agriclinic.fragments.HomeFragment_test;
import com.novaagritech.agriclinic.fragments.NewsFragment;
import com.novaagritech.agriclinic.fragments.SchemesFragment;
import com.novaagritech.agriclinic.fragments.SearchMArticlesFragment;
import com.novaagritech.agriclinic.fragments.VideosFragment;

public class HomeActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , ForceUpdateChecker.OnUpdateNeededListener  {

    Toolbar toolbar;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    boolean doubleBackToExitPressedOnce = false;

    //final Fragment homeFragment1 = new HomeFragment1();
    final Fragment homeFragment1 = new HomeFragment_test ();
    final Fragment newsFragment = new NewsFragment();
    final Fragment schemesFragment = new SchemesFragment();
    final Fragment eventsFragment = new EventsFragment();
    final Fragment searchMArticlesFragment = new SearchMArticlesFragment();
    final Fragment aboutFragment = new AboutFragment();
    final Fragment helpFragment = new HelpFragment();
    final Fragment videosFragment = new VideosFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment1;
    MyAppPrefsManager myAppPrefsManager;
    FragmentTransaction transaction;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getResources().getString(R.string.home));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        myAppPrefsManager = new MyAppPrefsManager(HomeActivity1.this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        TextView name = (TextView )v. findViewById(R.id.nav_name);
        TextView number = (TextView )v. findViewById(R.id.nav_number);

        name.setText(""+myAppPrefsManager.getUserName());
        number.setText(""+myAppPrefsManager.getUserMobile());

        TextView appversion = (TextView )v. findViewById(R.id.appversion);

        appversion.setText("V : "+ ConstantValues.getAppVersion(HomeActivity1.this));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        transaction=getSupportFragmentManager().beginTransaction();
        fm.beginTransaction().add(R.id.main_container, helpFragment, "8").hide(helpFragment).commit();
        fm.beginTransaction().add(R.id.main_container, aboutFragment, "7").hide(aboutFragment).commit();
        fm.beginTransaction().add(R.id.main_container, searchMArticlesFragment, "6").hide(searchMArticlesFragment).commit();
        fm.beginTransaction().add(R.id.main_container, videosFragment, "5").hide(videosFragment).commit();
        fm.beginTransaction().add(R.id.main_container, eventsFragment, "4").hide(eventsFragment).commit();
        fm.beginTransaction().add(R.id.main_container, schemesFragment, "3").hide(schemesFragment).commit();
        fm.beginTransaction().add(R.id.main_container, newsFragment, "2").hide(newsFragment).commit();
        fm.beginTransaction().add(R.id.main_container,homeFragment1, "1").commit();


        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();


    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue.")
                .setPositiveButton("Update",
                        (dialog12, which) ->
                                redirectStore(updateUrl)).setNegativeButton("No, thanks",
                        (dialog1, which) ->
                                dialog1.dismiss()).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        toolbar.setTitle(getResources().getString(R.string.home));
                        fm.beginTransaction().hide(active).show(homeFragment1).commit();
                        transaction.addToBackStack(null);
                        active = homeFragment1;
                        return true;

                    case R.id.navigation_news:
                        toolbar.setTitle(getResources().getString(R.string.news));
                        fm.beginTransaction().hide(active).show(newsFragment).commit();
                        transaction.addToBackStack(null);
                        active = newsFragment;
                        return true;

                    case R.id.navigation_schemes:
                        toolbar.setTitle(getResources().getString(R.string.schemes));
                        fm.beginTransaction().hide(active).show(schemesFragment).commit();
                        transaction.addToBackStack(null);
                        active = schemesFragment;
                        return true;

                    case R.id.navigation_events:
                        toolbar.setTitle(getResources().getString(R.string.events));
                        fm.beginTransaction().hide(active).show(eventsFragment).commit();
                        transaction.addToBackStack(null);
                        active = eventsFragment;
                        return true;

                    case R.id.navigation_videos:
                        toolbar.setTitle(getResources().getString(R.string.channels));
                        fm.beginTransaction().hide(active).show(videosFragment).commit();
                        transaction.addToBackStack(null);
                        active = videosFragment;

                        return true;
                }
                return false;
            };


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce=false;


            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_home) {
            toolbar.setTitle(getResources().getString(R.string.home));
            fm.beginTransaction().hide(active).show(homeFragment1).commit();
            transaction.addToBackStack(null);
            active = homeFragment1;
        } else if (id == R.id.navigation_news) {
            toolbar.setTitle(getResources().getString(R.string.news));
            fm.beginTransaction().hide(active).show(newsFragment).commit();
            transaction.addToBackStack(null);
            active = newsFragment;

        }else if (id == R.id.navigation_schemes) {
            toolbar.setTitle(getResources().getString(R.string.schemes));
            fm.beginTransaction().hide(active).show(schemesFragment).commit();
            transaction.addToBackStack(null);
            active = schemesFragment;

        } else if (id == R.id.navigation_events) {
            toolbar.setTitle(getResources().getString(R.string.events));
            fm.beginTransaction().hide(active).show(eventsFragment).commit();
            transaction.addToBackStack(null);
            active = eventsFragment;

        } else if (id == R.id.navigation_articles) {
            toolbar.setTitle(getResources().getString(R.string.articles));
            fm.beginTransaction().hide(active).show(searchMArticlesFragment).commit();
            transaction.addToBackStack(null);
            active = searchMArticlesFragment;

        }else if (id == R.id.nav_share) {
            ConstantValues.shareMyApp(HomeActivity1.this);

        } else if (id == R.id.nav_about) {
            toolbar.setTitle(getResources().getString(R.string.about_us));
            fm.beginTransaction().hide(active).show(aboutFragment).commit();
            transaction.addToBackStack(null);
            active = aboutFragment;

        }else if (id == R.id.nav_help) {
            toolbar.setTitle(getResources().getString(R.string.help_support));
            fm.beginTransaction().hide(active).show(helpFragment).commit();
            transaction.addToBackStack(null);
            active = helpFragment;

        }else if (id == R.id.nav_logout) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity1.this);
            builder1.setMessage("Sure to Logout?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            // Set UserLoggedIn in MyAppPrefsManager

                            myAppPrefsManager.setUserLoggedIn(false);
                            myAppPrefsManager.setUserId(null);


                            // Set isLogged_in of ConstantValues
                            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


                            // Navigate to Login Activity
                            Intent intent=new Intent(HomeActivity1.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(HomeActivity1.this, "Logout Successfully", Toast.LENGTH_SHORT).show();

                            finish();
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "N0",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}