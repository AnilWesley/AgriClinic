package com.novaagritech.agriclinic.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.app.AppSignatureHelper;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivityRegistrationBinding;
import com.novaagritech.agriclinic.interfaces.OtpReceivedInterface;
import com.novaagritech.agriclinic.modals.UserData;
import com.novaagritech.agriclinic.receiver.SmsBroadcastReceiver;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.LocationTrack;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class RegistrationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {

    ActivityRegistrationBinding binding;
    String TAG = "Registration_Activity1";
    ProgressDialog pDialog;
    String otp;

    LocationTrack locationTrack;
    Address address = null;
    String addr = "";
    String zipcode = "";
    String city = "";
    String state = "";
    String number;
    String otp_Num;
    String name,password;
    double longitude;
    double latitude;

    GoogleApiClient mGoogleApiClient;
    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private int RESOLVE_HINT = 2;

    MyAppPrefsManager myAppPrefsManager;

    String user_id;

    private List<UserData.UserDeatils> userDeatilsList;
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
        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

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



    }

    public void sendOtp(View view) {

        number = binding.editTextNumber.getText().toString().trim();
        name = binding.editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(number)) {
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
        Call<UserData> call = service.processRegisterOtp(jsonObject);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull retrofit2.Response<UserData> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){


                    UserData userData = response.body();
                    if (userData.isStatus()){
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
                        Toast.makeText(RegistrationActivity.this, ""+userData.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
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

        locationTrack = new LocationTrack(RegistrationActivity.this);
        if (locationTrack.canGetLocation()) {


            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();

            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();

            Log.d("LOCATION", "" + "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));

            Geocoder geocoder = new Geocoder(RegistrationActivity.this, Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);


                if (addresses != null && addresses.size() > 0) {

                    addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();

                    for (int i = 0; i < addresses.size(); i++) {
                        address = addresses.get(i);
                        if (address.getPostalCode() != null) {
                            zipcode = address.getPostalCode();
                            city = address.getLocality();
                            Log.d("LOCATION", "" + zipcode + "" + city);
                            break;
                        }


                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            locationTrack.showSettingsAlert();
        }




        if (otp_Num.equals(otp)) {


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile", number);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("pincode", zipcode);
            jsonObject.addProperty("otp", otp_Num);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("check_tc", "1");
            jsonObject.addProperty("check_sms_calls", "1");
            jsonObject.addProperty("otp_confirmed", "Yes");

            Log.d(TAG,""+jsonObject);
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<UserData> call = service.processRegisterOtp(jsonObject);
            call.enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(@NonNull Call<UserData> call, @NonNull retrofit2.Response<UserData> response) {

                    // Check if the Response is successful
                    if (response.isSuccessful()){


                        UserData userData = response.body();
                        if (userData.isStatus()){
                            assert response.body() != null;

                            pDialog.dismiss();

                            userDeatilsList=response.body().getResponse();
                            user_id=userDeatilsList.get(0).getId();
                            name=userDeatilsList.get(0).getName();
                            number=userDeatilsList.get(0).getMobile();
                            zipcode=userDeatilsList.get(0).getPincode();
                            // Set UserLoggedIn in MyAppPrefsManager

                            myAppPrefsManager.setUserLoggedIn(true);
                            myAppPrefsManager.setUserId(user_id);
                            myAppPrefsManager.setUserMobile(number);
                            myAppPrefsManager.setUserName(name);
                            myAppPrefsManager.setUserPincode(zipcode);

                            // Set isLogged_in of ConstantValues
                            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
                            Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(RegistrationActivity.this,HomeActivity1.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            pDialog.dismiss();
                        }else {
                            pDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, ""+userData.getMessage(), Toast.LENGTH_SHORT).show();
                        }




                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {

                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string

                binding.editTextNumber.setText(credential.getId());
            }
        }

    }
}