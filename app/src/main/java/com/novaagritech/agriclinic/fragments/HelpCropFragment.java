package com.novaagritech.agriclinic.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.CropReportSingleAdapter;
import com.novaagritech.agriclinic.app.AppController;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.FragmentHelpCropBinding;
import com.novaagritech.agriclinic.modals.ReportDetails;
import com.novaagritech.agriclinic.utilities.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpCropFragment extends Fragment {


    public HelpCropFragment() {
        // Required empty public constructor
    }



    private String TAG="HELPCROP";

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String imageEncoded="";
    private String  visitRemarks1;
    private int PICK_IMAGE_REQUEST = 1;


    private CropReportSingleAdapter visitorAdapter;
    List<ReportDetails> list;

    ProgressDialog pDialog;
    private String user_id;

    FragmentHelpCropBinding binding;
    private ReportDetails visitorDetails;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_help_crop, container, false);
        View view = binding.getRoot();

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());

        list = new ArrayList<ReportDetails>();
        visitorDetails =new ReportDetails();



        user_id = myAppPrefsManager.getUserId ( );





        binding.imageEmpCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {

                }
            }*/
        });

        binding.imageEmGallery.setOnClickListener(v -> {
            chooseImage();
        });

        binding.submitVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 visitRemarks1 = Objects.requireNonNull(binding.visitRemarks.getText()).toString().trim();
                if (TextUtils.isEmpty(visitRemarks1) ){
                    Toast.makeText(getActivity(), "Enter Remarks", Toast.LENGTH_SHORT).show();
                }else if (imageEncoded.equals("")){
                    Toast.makeText(getActivity(), "Upload Photo", Toast.LENGTH_SHORT).show();
                }else {
                    cropUpload();
                }

            }
        });



        return  view;

    }


    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void cropUpload(){

        pDialog= new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

            JSONObject jsonBody = new JSONObject();
            try {

                jsonBody.put("user_id", user_id);

                jsonBody.put("remarks", visitRemarks1);
                jsonBody.put("crop_image", imageEncoded);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String mRequestBody = jsonBody.toString();
            Log.d(TAG, ""+mRequestBody);
            // Tag used to cancel the request
            String tag_json_obj = "pqlist";



            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Urls.CROP_URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(final JSONObject response) {

                            Log.d(TAG, response.toString());

                            try{
                                if (response.getString("status").equalsIgnoreCase("true"))
                                {

                                    Toast.makeText(getActivity(), ""+response.getString("message"), Toast.LENGTH_SHORT).show();


                                    binding.visitRemarks.setText("");
                                    binding.imageEmpPhoto.setImageDrawable(getActivity().getDrawable(R.drawable.ic_account_circle_black_24dp));

                                    imageEncoded="";
                                    pDialog.hide();

                                }
                                else {

                                    Toast.makeText(getActivity(), ""+response.getString("message"), Toast.LENGTH_SHORT).show();
                                    pDialog.hide();
                                }

                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }





                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Try Again...", Toast.LENGTH_SHORT).show();
                    // hide the progress dialog
                    pDialog.dismiss();
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");

                    } catch (UnsupportedEncodingException uee) {


                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }

                }

            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);




    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            binding.imageEmpPhoto.setImageBitmap(photo);
            assert photo != null;
            encodeTobase64(photo);

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                binding.imageEmpPhoto.setImageBitmap(bitmap);
                assert bitmap != null;
                encodeTobase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }








    private void encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);


        // Log.e(TAG, ""+imageEncoded);
    }



}

