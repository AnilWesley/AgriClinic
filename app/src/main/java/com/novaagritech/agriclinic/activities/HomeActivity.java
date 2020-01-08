package com.novaagritech.agriclinic.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.BuildConfig;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.firebase.ForceUpdateChecker;
import com.novaagritech.agriclinic.fragments.AboutFragment;
import com.novaagritech.agriclinic.fragments.EventsFragment;
import com.novaagritech.agriclinic.fragments.HelpCropFragment;
import com.novaagritech.agriclinic.fragments.HelpFragment;
import com.novaagritech.agriclinic.fragments.HomeFragment1;
import com.novaagritech.agriclinic.fragments.NewsFragment;
import com.novaagritech.agriclinic.fragments.SchemesFragment;
import com.novaagritech.agriclinic.fragments.SearchMArticlesFragment;
import com.novaagritech.agriclinic.fragments.TermsFragment;
import com.novaagritech.agriclinic.location.Constants;
import com.novaagritech.agriclinic.location.FetchAddressIntentService;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , ForceUpdateChecker.OnUpdateNeededListener  {

    Toolbar toolbar;
    private static final String TAG = "HOME!";
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    boolean doubleBackToExitPressedOnce = false;

    final Fragment homeFragment1 = new HomeFragment1();
    //final Fragment homeFragment1 = new HomeFragment_test();
    final Fragment newsFragment = new NewsFragment();
    final Fragment schemesFragment = new SchemesFragment();
    final Fragment eventsFragment = new EventsFragment();
    final Fragment searchMArticlesFragment = new SearchMArticlesFragment();
    final Fragment aboutFragment = new AboutFragment();
    final Fragment helpFragment = new HelpFragment();
    final Fragment helpCropFragment = new HelpCropFragment();
    final Fragment termsFragment = new TermsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment1;
    MyAppPrefsManager myAppPrefsManager;
    FragmentTransaction transaction;
    String token;

    private String user_id;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested=false;

    /**
     * The formatted location address.
     */
    private String mAddressOutput="";
    private String mAddressLatitude="";
    private String mAddressLongitude="";
    private String mAddressPostalCode="";

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;


    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private LocationSettingsRequest mLocationSettingsRequest;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getResources().getString(R.string.home));



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        myAppPrefsManager = new MyAppPrefsManager(HomeActivity.this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        TextView name = (TextView )v. findViewById(R.id.nav_name);
        TextView number = (TextView )v. findViewById(R.id.nav_number);

        name.setText(""+myAppPrefsManager.getUserName());
        number.setText(""+myAppPrefsManager.getUserMobile());

        TextView appversion = (TextView )v. findViewById(R.id.appversion);

        appversion.setText("V : "+ ConstantValues.getAppVersion(HomeActivity.this));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_view);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
        user_id= myAppPrefsManager.getUserId();

       /* CurvedBottomNavigationView curvedBottomNavigationView = findViewById(R.id.bottom_view);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        curvedBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);*/

        transaction=getSupportFragmentManager().beginTransaction();
        fm.beginTransaction().replace(R.id.main_container, termsFragment, "8").hide(termsFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, helpFragment, "7").hide(helpFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, aboutFragment, "6").hide(aboutFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, searchMArticlesFragment, "5").hide(searchMArticlesFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, helpCropFragment, "9").hide(helpCropFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, eventsFragment, "4").hide(eventsFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, schemesFragment, "3").hide(schemesFragment).commit();
        fm.beginTransaction().replace(R.id.main_container, newsFragment, "2").hide(newsFragment).commit();
        fm.beginTransaction().replace(R.id.main_container,homeFragment1, "1").commit();


        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();







        updateToken();



        mResultReceiver = new AddressResultReceiver(new Handler());


        // Set defaults, then update using values stored in the Bundle.


        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /**
         * Provides access to the Location Settings API.
         */
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");



                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();

                        }


                    }
                });

        if (mLastLocation != null) {
            startIntentService();
            return;
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();




    }


    private void updateToken() {



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //To do//
                        String msg = getString(R.string.fcm_token, "");
                        Log.d(TAG, msg);
                        return;
                    }

                    // Get the Instance ID token//
                    token = task.getResult().getToken();
                    String msg = getString(R.string.fcm_token, token);
                    Log.d(TAG, msg);

                });

        token =FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, token);
        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("token", token);

        //Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Banners> call = service.processToken(jsonObject);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(@NonNull Call<Banners> call, @NonNull Response<Banners> response) {

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        Banners articlesData1 = response.body();

                        if (articlesData1.isStatus()) {

                            Log.d("TOKEN1",""+articlesData1.getMessage());

                        }


                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Banners> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });



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
                        fm.beginTransaction().replace(R.id.main_container,homeFragment1, "1").commit();
                        transaction.addToBackStack(null);
                        active = homeFragment1;
                        return true;

                    case R.id.navigation_news:
                        toolbar.setTitle(getResources().getString(R.string.news));
                        fm.beginTransaction().replace(R.id.main_container,newsFragment, "2").commit();
                        //fm.beginTransaction().hide(active).show(newsFragment).commit();
                        transaction.addToBackStack(null);
                        active = newsFragment;
                        return true;

                    case R.id.navigation_schemes:
                        toolbar.setTitle(getResources().getString(R.string.schemes));
                        fm.beginTransaction().replace(R.id.main_container,schemesFragment, "3").commit();
                        transaction.addToBackStack(null);
                        active = schemesFragment;
                        return true;

                    case R.id.navigation_events:
                        toolbar.setTitle(getResources().getString(R.string.events));
                        fm.beginTransaction().replace(R.id.main_container,eventsFragment, "4").commit();

                        transaction.addToBackStack(null);
                        active = eventsFragment;
                        return true;


                    case R.id.navigation_crop:
                        toolbar.setTitle(getResources().getString(R.string.channels));
                        fm.beginTransaction().replace(R.id.main_container,helpCropFragment, "9").commit();

                        transaction.addToBackStack(null);
                        active = helpCropFragment;

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
            fm.beginTransaction().replace(R.id.main_container,homeFragment1, "1").commit();
            transaction.addToBackStack(null);
            active = homeFragment1;
        } else if (id == R.id.navigation_news) {
            toolbar.setTitle(getResources().getString(R.string.news));
            fm.beginTransaction().replace(R.id.main_container,newsFragment, "2").commit();
            transaction.addToBackStack(null);
            active = newsFragment;

        }else if (id == R.id.navigation_schemes) {
            toolbar.setTitle(getResources().getString(R.string.schemes));
            fm.beginTransaction().replace(R.id.main_container,schemesFragment, "3").commit();
            transaction.addToBackStack(null);
            active = schemesFragment;

        } else if (id == R.id.navigation_events) {
            toolbar.setTitle(getResources().getString(R.string.events));
            fm.beginTransaction().replace(R.id.main_container,eventsFragment, "4").commit();
            transaction.addToBackStack(null);
            active = eventsFragment;

        } else if (id == R.id.navigation_articles) {
            toolbar.setTitle(getResources().getString(R.string.articles));
            fm.beginTransaction().replace(R.id.main_container,searchMArticlesFragment, "6").commit();
            transaction.addToBackStack(null);
            active = searchMArticlesFragment;


        }else if (id == R.id.navigation_reports) {
           Intent intent=new Intent(HomeActivity.this,CropReportsListActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);

        }else if (id == R.id.navigation_videos) {
            Intent intent =new Intent(HomeActivity.this, VideoMoreActivity.class);
            intent.putExtra("channelID","UCicsA32B7SO_RUJqzDKbKNA");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_share) {
            ConstantValues.shareMyApp(HomeActivity.this);

        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));


        }else if (id == R.id.nav_about) {
            toolbar.setTitle(getResources().getString(R.string.about_us));
            fm.beginTransaction().replace(R.id.main_container,aboutFragment, "7").commit();
            transaction.addToBackStack(null);
            active = aboutFragment;

        }else if (id == R.id.nav_help) {
            toolbar.setTitle(getResources().getString(R.string.help_support));
            fm.beginTransaction().replace(R.id.main_container,helpFragment, "8").commit();
            transaction.addToBackStack(null);
            active = helpFragment;

        }else if (id == R.id.nav_terms) {
            toolbar.setTitle(getResources().getString(R.string.about_us));
            fm.beginTransaction().replace(R.id.main_container,termsFragment, "9").commit();
            transaction.addToBackStack(null);
            active = termsFragment;

        }else if (id == R.id.nav_logout) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
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
                            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(HomeActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();

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




    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                mAddressLatitude = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                mAddressLongitude = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                mAddressPostalCode = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }



    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    /**
     * Updates the address in the UI.
     */
    private void displayAddressOutput() {

        Log.d(TAG,"Current Location  : "+mAddressOutput);
        Log.d(TAG,"Current Location  : "+mAddressLatitude);
        Log.d(TAG,"Current Location  : "+mAddressLongitude);
        Log.d(TAG,"Current Location  : "+mAddressPostalCode);


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("latitude", mAddressLatitude);
        jsonObject.addProperty("longitude", mAddressLongitude);
        jsonObject.addProperty("pincode", mAddressPostalCode);
        jsonObject.addProperty("location", mAddressOutput);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Banners> call = service.processLocation(jsonObject);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(@NonNull Call<Banners> call, @NonNull Response<Banners> response) {

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        Banners articlesData1 = response.body();

                        if (articlesData1.isStatus()) {

                            Log.d(TAG,""+articlesData1.getMessage());

                        }else {
                            Log.d(TAG,""+articlesData1.getMessage());
                        }


                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Banners> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });


    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {


            Log.d(TAG,"locationUpadted");
        } else {

            Log.d(TAG,"locationNotUpadted");
        }
    }

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG,""+text);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressLatitude);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressLongitude);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressPostalCode);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in HomeActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY_ADDRESS);
            mAddressLatitude = resultData.getString(Constants.RESULT_DATA_KEY_LATITUDE);
            mAddressLongitude = resultData.getString(Constants.RESULT_DATA_KEY_LONGITUDE);
            mAddressPostalCode = resultData.getString(Constants.RESULT_DATA_KEY_POSTAL_CODE);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}