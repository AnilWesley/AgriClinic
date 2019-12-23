package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivityLoginBinding;
import com.novaagritech.agriclinic.modals.UserData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {


    String TAG="LOGIN_ACTIVITY";


    boolean doublePressedBackToExit=false;
    MyAppPrefsManager myAppPrefsManager;

    ActivityLoginBinding binding;
    ProgressDialog pDialog;
    private List<UserData.UserDeatils> userDeatilsList;
    String user_id,name,number,zipcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login);
        myAppPrefsManager= new MyAppPrefsManager(LoginActivity.this);
        binding.forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


    }

    public void veriySignIn(View view){

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        String name1=binding.editTextNumber.getText().toString().trim();
        String pass=binding.editTextCnfPasswrd.getText().toString().trim();
        if (!name1.isEmpty() && !pass.isEmpty()){




            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile", name1);
            jsonObject.addProperty("password", pass);


            Log.d(TAG,""+jsonObject);
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<UserData> call = service.processUserLogin(jsonObject);
            call.enqueue(new Callback<UserData>() {
                @Override
                public void onResponse(@NonNull Call<UserData> call, @NonNull retrofit2.Response<UserData> response) {

                    // Check if the Response is successful
                    if (response.isSuccessful()){


                        UserData userData = response.body();
                        if (userData.isStatus()){
                            assert response.body() != null;



                            userDeatilsList=response.body().getResponse();

                            user_id=userDeatilsList.get(0).getId();
                            name=userDeatilsList.get(0).getName();
                            number=userDeatilsList.get(0).getMobile();
                            zipcode=userDeatilsList.get(0).getPincode();


                            myAppPrefsManager.setUserLoggedIn(true);
                            myAppPrefsManager.setUserId(user_id);
                            myAppPrefsManager.setUserMobile(number);
                            myAppPrefsManager.setUserName(name);
                            myAppPrefsManager.setUserPincode(zipcode);

                            // Set isLogged_in of ConstantValues
                            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity1.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            pDialog.dismiss();


                        }else {
                            pDialog.dismiss();
                            Toast.makeText(LoginActivity.this, ""+userData.getMessage(), Toast.LENGTH_SHORT).show();
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
        else{

            Toast.makeText(LoginActivity.this, "Enter Details", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

    public void textSignUp(View view){

        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (doublePressedBackToExit) {
            super.onBackPressed();
        }
        else {
            this.doublePressedBackToExit = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            // Delay of 2 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // Set doublePressedBackToExit false after 2 seconds
                    doublePressedBackToExit = false;
                }
            }, 2000);
        }

        // Check if doubleBackToExitPressed is true

    }
}
