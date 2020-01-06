package com.novaagritech.agriclinic.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.databinding.FragmentAboutBinding;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.SchemesData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    String TAG="ABOUTFRAGMENT";


    private List<InfoData> infoDataList;
    public AboutFragment() {
        // Required empty public constructor
    }


    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_about, container, false);
        View view = binding.getRoot();



        getNews();


        return view;
    }






    private void getNews(){


        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");


        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<SchemesData> call = service.processAboutUS(jsonObject);
        call.enqueue(new Callback<SchemesData>() {
            @Override
            public void onResponse(@NonNull Call<SchemesData> call, @NonNull retrofit2.Response<SchemesData> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    SchemesData articlesData = response.body();
                    infoDataList = response.body().getResult();

                    if (articlesData.isStatus()) {
                        if (infoDataList != null && infoDataList.size() > 0) {

                                Log.d(TAG, "" + infoDataList.size());

                                binding.textDesc.loadDataWithBaseURL(null,infoDataList.get(0).getDescription(), "text/html; charset=utf-8", "UTF-8",null);



                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<SchemesData> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });

    }








}
