package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.CropReportAdapter;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivityCropReportsListBinding;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CropReportsListActivity extends AppCompatActivity {

    ActivityCropReportsListBinding binding;

    CropReportAdapter mAdapter;
    List<Info> list;

    String TAG="VISTOR_LIST";
    ProgressDialog pDialog;
    String user_id;

    MyAppPrefsManager myAppPrefsManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView ( CropReportsListActivity.this, R.layout.activity_crop_reports_list );

        setSupportActionBar ( binding.toolbar );


        myAppPrefsManager=new MyAppPrefsManager(this);
        user_id=myAppPrefsManager.getUserId();



        binding.actionImage.setOnClickListener (v ->
                onBackPressed ( ));


        pDialog = new ProgressDialog(CropReportsListActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("user_id", user_id);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processCropProblemsList(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull retrofit2.Response<Home> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    list = response.body().getResult();

                    if (articlesData.isStatus()) {
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                Log.d(TAG, "" + list.size());

                                mAdapter = new CropReportAdapter(CropReportsListActivity.this, list);

                                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(CropReportsListActivity.this);
                                //gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                binding.recycleCrops.setLayoutManager(gridLayoutManager);
                                //homeRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                binding.recycleCrops.setHasFixedSize(true);
                                // Set the Adapter and LayoutManager to the RecyclerView
                                binding.recycleCrops.setAdapter(mAdapter);




                                mAdapter.notifyDataSetChanged();
                                pDialog.dismiss();
                            }
                            //get values


                        }else{
                            pDialog.dismiss();
                            binding.recycleCrops.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }

                    }else {
                        pDialog.dismiss();
                        binding.recycleCrops.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                        binding.emptyView.setText("No Records Found");
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










}
