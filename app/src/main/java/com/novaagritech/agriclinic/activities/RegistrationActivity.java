package com.novaagritech.agriclinic.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.BuildConfig;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.app.AppSignatureHelper;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivityRegistrationBinding;
import com.novaagritech.agriclinic.interfaces.OtpReceivedInterface;
import com.novaagritech.agriclinic.location.Constants;
import com.novaagritech.agriclinic.location.FetchAddressIntentService;
import com.novaagritech.agriclinic.modals.Users;
import com.novaagritech.agriclinic.receiver.SmsBroadcastReceiver;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RegistrationActivity extends AppCompatActivity implements OtpReceivedInterface{

    ActivityRegistrationBinding binding;
    String TAG = "Registration_Activity1";
    ProgressDialog pDialog;
    String otp;

    
   
    String number;
    String otp_Num;
    String name,password;
   


    SmsBroadcastReceiver mSmsBroadcastReceiver;

    MyAppPrefsManager myAppPrefsManager;

    String user_id;

    private List<Users.UserDeatils> userDeatilsList;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        myAppPrefsManager=new MyAppPrefsManager(this);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(RegistrationActivity.this);
        appSignatureHelper.getAppSignatures();

        SpannableString SpanString = new SpannableString(
                "By Registering you agree to the Terms of Use and Privacy Policy");

        ClickableSpan teremsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {


                Intent mIntent = new Intent(RegistrationActivity.this, CommonWebView.class);
                mIntent.putExtra("isTermsAndCondition", true);
                mIntent.putExtra("value","1");
                startActivity(mIntent);

            }
        };

        // Character starting from 32 - 45 is Terms and condition.
        // Character starting from 49 - 63 is privacy policy.

        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {

                Intent mIntent = new Intent(RegistrationActivity.this, CommonWebView.class);
                mIntent.putExtra("isPrivacyPolicy", true);
                mIntent.putExtra("value","2");
                startActivity(mIntent);

            }
        };

        SpanString.setSpan(teremsAndCondition, 32, 45, 0);
        SpanString.setSpan(privacy, 49, 63, 0);
        SpanString.setSpan(new ForegroundColorSpan(Color.BLUE), 32, 45, 0);
        SpanString.setSpan(new ForegroundColorSpan(Color.BLUE), 49, 63, 0);
        SpanString.setSpan(new UnderlineSpan(), 32, 45, 0);
        SpanString.setSpan(new UnderlineSpan(), 49, 63, 0);

        binding.txtByRegistering.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtByRegistering.setText(SpanString, TextView.BufferType.SPANNABLE);
        binding.txtByRegistering.setSelected(true);

        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        //getHintPhoneNumber();
        binding.agreeTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    binding.buttonVerify.setEnabled(true);
                    binding.buttonVerify.setBackgroundColor(getResources().getColor(R.color.quantum_vanillagreen600));

                }else{
                    binding.buttonVerify.setEnabled(false);
                    binding.buttonVerify.setBackgroundColor(getResources().getColor(R.color.quantum_grey));
                }
            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());


        // Set defaults, then update using values stored in the Bundle.


        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
         *  Receives data sent from FetchAddressIntentService and updates the UI in RegistrationActivity.
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
                            ActivityCompat.requestPermissions(RegistrationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RegistrationActivity.this,
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
    public void sendOtp(View view) {

        number = binding.editTextNumber.getText().toString().trim();
        name = binding.editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(name) ) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
        } else if (ConstantValues.validateFirstName1(name)) {
            Toast.makeText(this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (ConstantValues.isValidMoblie(number)) {
            Toast.makeText(this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
        } else {
            send_otp();


        }


    }

    public void textSignIn(View view){
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void send_otp() {

        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        number = binding.editTextNumber.getText().toString().trim();


        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", number);
        jsonObject.addProperty("otp_confirmed", "No");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Users> call = service.processRegisterOtp(jsonObject);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull retrofit2.Response<Users> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){


                    Users users = response.body();
                    if (users.isStatus()){
                        assert response.body() != null;
                        pDialog.dismiss();
                        userDeatilsList=response.body().getResponse();

                        otp = userDeatilsList.get(0).getOtp();
                        Log.d(TAG,""+otp);
                        binding.linearLayout.setVisibility(View.VISIBLE);
                        binding.buttonSend.setVisibility(View.GONE);
                        binding.buttonVerify.setVisibility(View.VISIBLE);
                        startSMSListener();
                    }else {
                        pDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, ""+ users.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });




    }

    public void veriyOtp(View view) {
         otp_Num = binding.editTextOTP.getText().toString().trim();
         password = binding.editTextPasswrd.getText().toString().trim();
        if (TextUtils.isEmpty(otp_Num)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }else if (ConstantValues.isValidOTP(otp_Num)) {
            Toast.makeText(this, "OTP should be 6 digits", Toast.LENGTH_SHORT).show();
        }  else if (ConstantValues.isValidPassword1(password)) {
            Toast.makeText(this, "Password should greater than 5 digits", Toast.LENGTH_SHORT).show();
        }else {
            verify_otp();
        }
    }


    public void verify_otp() {
        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        

        if (otp_Num.equals(otp)) {


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile", number);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("latitude", mAddressLatitude);
            jsonObject.addProperty("longitude", mAddressLongitude);
            jsonObject.addProperty("pincode", mAddressPostalCode);
            jsonObject.addProperty("location", mAddressOutput);
            jsonObject.addProperty("otp", otp_Num);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("check_tc", "1");
            jsonObject.addProperty("check_sms_calls", "1");
            jsonObject.addProperty("otp_confirmed", "Yes");

            Log.d(TAG,""+jsonObject);
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<Users> call = service.processRegisterOtp(jsonObject);
            call.enqueue(new Callback<Users>() {
                @Override
                public void onResponse(@NonNull Call<Users> call, @NonNull retrofit2.Response<Users> response) {

                    // Check if the Response is successful
                    if (response.isSuccessful()){


                        Users users = response.body();
                        if (users.isStatus()){
                            assert response.body() != null;

                            pDialog.dismiss();

                            userDeatilsList=response.body().getResponse();
                            user_id=userDeatilsList.get(0).getId();
                            name=userDeatilsList.get(0).getName();
                            number=userDeatilsList.get(0).getMobile();
                            mAddressPostalCode=userDeatilsList.get(0).getPincode();
                            // Set UserLoggedIn in MyAppPrefsManager

                            myAppPrefsManager.setUserLoggedIn(true);
                            myAppPrefsManager.setUserId(user_id);
                            myAppPrefsManager.setUserMobile(number);
                            myAppPrefsManager.setUserName(name);
                            myAppPrefsManager.setUserPincode(mAddressPostalCode);

                            // Set isLogged_in of ConstantValues
                            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
                            Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(RegistrationActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            pDialog.dismiss();
                        }else {
                            pDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, ""+ users.getMessage(), Toast.LENGTH_SHORT).show();
                        }




                    }
                }

                @Override
                public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    Log.d("ResponseF",""+t);
                }
            });


        } else {
            Toast.makeText(RegistrationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }

    }





   

    @Override
    public void onOtpReceived(String otp) {
        //Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        Log.d(TAG,""+otp);
        binding.editTextOTP.setText(otp);

    }



    @Override
    public void onOtpTimeout() {
        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
    }

   

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               /* layoutInput.setVisibility(View.GONE);
                layoutVerify.setVisibility(View.VISIBLE);*/
                Toast.makeText(RegistrationActivity.this, "Otp Sent Successfully", Toast.LENGTH_LONG).show();


            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

   

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        int RESOLVE_HINT = 2;
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {

                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string

                binding.editTextNumber.setText(credential.getId());
            }
        }

    }
}