package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.adapters.CropsListAdapater;
import com.novaagritech.agriclinic.modals.Crops;
import com.novaagritech.agriclinic.databinding.ActivityCropsCategoriesBinding;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CropsCategoriesActivity extends AppCompatActivity {

    ActivityCropsCategoriesBinding binding;
    ProgressDialog pDialog;
    String TAG="CROPS_LIST";
    CropsListAdapater cropsListAdapater;
    List<Crops.CropDetails> cropsModalList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this, R.layout.activity_crops_categories);
        cropsModalList=new ArrayList<>();
        getAllCrops();



    }

    public void getAllCrops() {

        pDialog = new ProgressDialog(CropsCategoriesActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Crops> call = service.processCropCategories(jsonObject);
        call.enqueue(new Callback<Crops>() {
            @Override
            public void onResponse(@NonNull Call<Crops> call, @NonNull Response<Crops> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Crops crops = response.body();
                    cropsModalList = response.body().getResponse();


                    if (crops.isStatus()){
                        if (cropsModalList != null&&cropsModalList.size()>0  ) {
                            for (int i = 0; i <cropsModalList.size(); i++) {
                                Log.d(TAG, "" + cropsModalList.size());

                                cropsListAdapater = new CropsListAdapater(CropsCategoriesActivity.this, cropsModalList);
                                /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.cropsRecycle.getContext(),DividerItemDecoration.VERTICAL);
                                binding.cropsRecycle.addItemDecoration(dividerItemDecoration);*/
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CropsCategoriesActivity.this);
                                binding.cropsRecycle.setLayoutManager(linearLayoutManager);
                                binding.cropsRecycle.setAdapter(cropsListAdapater);
                                cropsListAdapater.notifyDataSetChanged();

                                pDialog.dismiss();
                            }
                            //get values

                        } else{
                            pDialog.dismiss();
                            binding.cropsRecycle.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        pDialog.dismiss();
                        binding.cropsRecycle.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<Crops> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });

        binding.cropsRecycle.addOnItemTouchListener(new RecyclerItemClickListener(CropsCategoriesActivity.this,binding.cropsRecycle, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent=new Intent(CropsCategoriesActivity.this, CropsSubCategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();

                bundle.putString("cat_id",cropsModalList.get(position).getId());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);

                //finish();


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


    }
}
