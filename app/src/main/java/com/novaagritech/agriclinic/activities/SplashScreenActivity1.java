package com.novaagritech.agriclinic.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreenActivity1 extends AppCompatActivity {

    ProgressBar progressBar;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    MyAppPrefsManager myAppPrefsManager;
    private String TAG="SPLASHACTIVITY1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        progressBar = findViewById(R.id.progressBar);
        myAppPrefsManager = new MyAppPrefsManager(this);
        //progressBar.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                permissioncheck();

            } else {
                // Pre-Marshmallow
                LaunchApp();
            }
        }, 2500);




    }








//-************************ permission check ***********************************************************************

    private void permissioncheck() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ");
        if (addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE");
        if (addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");
        if (addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("CALL_PHONE");
       /* if (addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("READ_PHONE_STATE");*/

        if (addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("ACCESS_FINE_LOCATION");

        if (addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("ACCESS_COARSE_LOCATION");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                StringBuilder message = new StringBuilder("You need to grant access to " + permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message.append(", ").append(permissionsNeeded.get(i));
                showMessageOKCancel(message.toString(),
                        (dialog, which) -> {

                            if (Build.VERSION.SDK_INT >= 23) {
                                // Marshmallow+
                                requestPermissions(permissionsList.toArray(new String[0]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


                            }

                        });
                return;
            }

            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                requestPermissions(permissionsList.toArray(new String[0]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


            }


        }else
        {
            // Toast.makeText(this,"Permission",Toast.LENGTH_LONG).show();
            LaunchApp();
        }

        //insertDummyContact();
    }


    private boolean addPermission(List<String> permissionsList, String permission) {

        boolean cond;
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                shouldShowRequestPermissionRationale(permission);//  return false;
            }
            //  return true;

            cond = true;


        } else {
            // Pre-Marshmallow
            cond = true;
        }

        return cond;

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreenActivity1.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            //Displaying a toast
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initial
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
           /* perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);*/
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            //Toast.makeText(SplashScreenActivity.this, " Permissions are jddddd", Toast.LENGTH_SHORT).show();
            // Fill with results
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            // Check for ACCESS_FINE_LOCATION
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                   /* && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED*/
                   &&  perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                   && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                // insertDummyContact();

                //Toast.makeText(SplashScreenActivity.this, " Permissions are l", Toast.LENGTH_SHORT).show();
                LaunchApp();

            } else {
                // Permission Denied
                Toast.makeText(SplashScreenActivity1.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                        .show();

                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    //Do something after 100
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    finish();
                }, 2500);
            }

        }
    }

    public void LaunchApp()
    {
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 2 seconds
                    sleep(1000);


                    if (ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn()) {

                        FirebaseDynamicLinks.getInstance()
                                .getDynamicLink(getIntent())
                                .addOnSuccessListener(SplashScreenActivity1.this, pendingDynamicLinkData -> {
                                    // Get deep link from result (may be null if no link is found)
                                    Uri deepLink = null;
                                    if (pendingDynamicLinkData != null) {
                                        deepLink = pendingDynamicLinkData.getLink();

                                        String referlink = deepLink.toString ( ).replace ( "https://agriclinic.org/viewcontent.php?id=", "" );
                                        Log.e ( TAG, " substring3 " + referlink ); //id=174


                                        if (referlink.contains("articles")){

                                            String strNew = referlink.replace("articles", "");
                                            Intent intent1=new Intent(SplashScreenActivity1.this, SingleArticleActivity.class);
                                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent1.putExtra("article_id",strNew);
                                            startActivity(intent1);
                                            finish();


                                        }
                                        if (referlink.contains("news")){
                                            String strNew = referlink.replace("news", "");
                                            Intent intent1=new Intent(SplashScreenActivity1.this, SingleNewsActivity.class);
                                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent1.putExtra("news_id",strNew);
                                            startActivity(intent1);
                                            finish();

                                        }
                                        if (referlink.contains("schemes")){
                                            String strNew = referlink.replace("schemes", "");
                                            Intent intent1=new Intent(SplashScreenActivity1.this, SingleSchemesActivity.class);
                                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent1.putExtra("scheme_id",strNew);
                                            startActivity(intent1);
                                            finish();
                                        }
                                        if (referlink.contains("events")){
                                            String strNew = referlink.replace("events", "");
                                            Intent intent1=new Intent(SplashScreenActivity1.this, SingleEventActivity.class);
                                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent1.putExtra("event_id",strNew);
                                            startActivity(intent1);
                                            finish();
                                        }
                                    }


                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.
                                    // ...

                                    // ...
                                })
                                .addOnFailureListener(SplashScreenActivity1.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "getDynamicLink:onFailure", e);
                                    }
                                });


                    } else {

                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        // start thread
        background.start();


    }
}
