package com.novaagritech.agriclinic.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.novaagritech.agriclinic.BuildConfig;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;

import com.novaagritech.agriclinic.databinding.FragmentHelpCropBinding;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.FileCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpCropFragment extends Fragment {


    public HelpCropFragment() {
        // Required empty public constructor
    }



    FragmentHelpCropBinding binding;

    private List<Info> dataList;

    private String TAG="HELPCROP";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_GALLERY_PHOTO = 2;
    private File mPhotoFile;
    private FileCompressor mCompressor;

    private String cropRemarks;
    private String cropName;
    private String imageEncoded="";

    ProgressDialog pDialog;
    private String user_id;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_help_crop, container, false);
        View view = binding.getRoot();

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        mCompressor = new FileCompressor(getActivity());

        user_id = myAppPrefsManager.getUserId();



        binding.imageEmGallery.setOnClickListener(v -> {



            requestStoragePermission(false);


        });

        binding.imageEmpCamera.setOnClickListener(v -> {

            requestStoragePermission(true);




        });


        binding.submitVisit.setOnClickListener(v -> {
            cropName = Objects.requireNonNull(binding.cropName.getText()).toString().trim();
            cropRemarks = Objects.requireNonNull(binding.cropRemarks.getText()).toString().trim();
            if (TextUtils.isEmpty(cropName) ){
                Toast.makeText(getActivity(), "Enter Crop Name", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(cropRemarks) ){
                Toast.makeText(getActivity(), "Enter Remarks", Toast.LENGTH_SHORT).show();
            }else if (imageEncoded.equals("")){
                Toast.makeText(getActivity(), "Upload Photo", Toast.LENGTH_SHORT).show();
            }else {
                cropUpload1();
            }

        });


        return  view;

    }




    private void cropUpload1(){

        pDialog= new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Please Wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("crop_name", cropName);
        jsonObject.addProperty("remarks", cropRemarks);
        jsonObject.addProperty("crop_image", imageEncoded);


        //Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processTakeCropProblem(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull Response<Home> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){


                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    dataList = response.body ( ).getResult ( );

                    if (articlesData.isStatus()) {
                        if (dataList != null && dataList.size ( ) > 0) {
                            Toast.makeText(getActivity(), "" + articlesData.getMessage(), Toast.LENGTH_SHORT).show();

                            binding.cropName.setText("");
                            binding.cropRemarks.setText("");
                            binding.imageViewProfilePic.setImageDrawable(getActivity().getDrawable(R.drawable.ic_account_circle_black_24dp));

                            imageEncoded = "";
                            pDialog.hide();
                        }

                    }else {
                        Toast.makeText(getActivity(), ""+articlesData.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<Home> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });








}

    private void encodeFileToBase64Binary(String fileName)
            throws IOException {

        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        imageEncoded = Base64.encodeToString(bytes,Base64.DEFAULT);
        //Log.d(TAG,""+imageEncoded);


    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        // File is too large
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }


    private void encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);


        // Log.e(TAG, ""+imageEncoded);
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    //Log.d(TAG,""+mPhotoFile);
                    encodeFileToBase64Binary(String.valueOf((mPhotoFile)));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(getActivity()).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.image_not_available)).into(binding.imageViewProfilePic);

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    //Log.d(TAG,""+mPhotoFile);
                    encodeFileToBase64Binary(String.valueOf((mPhotoFile)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(getActivity()).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.image_not_available)).into(binding.imageViewProfilePic);

            }
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "PNG" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(mFileName, ".png", storageDir);
    }

    /**
     * Get real file path from URI
     *
     * @param contentUri
     * @return
     */
    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }










}

