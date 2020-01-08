package com.novaagritech.agriclinic.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.SingleSchemesActivity;
import com.novaagritech.agriclinic.adapters.NewsAdapter;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.FragmentSchemesBinding;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchemesFragment extends Fragment {



    private NewsAdapter mAdapter;


    private List<Info> infoSchemes;



    private String TAG = "News";

    private LinearLayoutManager mLayoutManager;
    private Handler handler;
    private ProgressDialog pDialog;

    public SchemesFragment() {
        // Required empty public constructor
    }


    private FragmentSchemesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_schemes, container, false);
        View view = binding.getRoot();
        binding.shimmerViewContainer.startShimmer();
        handler = new Handler();
        pDialog = new ProgressDialog(getActivity());

        getSchemes();


        binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            infoSchemes = new ArrayList<Info>();

            binding.mSwipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        binding.shimmerViewContainer.startShimmer();
                        getSchemes();
                    }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);

        return view;
    }











    private void getSchemes() {


        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("limit", "");
        jsonObject.addProperty("crop_id", "5");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processGovtSchemesList(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull retrofit2.Response<Home> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    infoSchemes = response.body().getResponse();

                    if (infoSchemes.isEmpty()) {
                        binding.articlesSchemes.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);

                    } else {
                        binding.emptyView.setVisibility(View.GONE);
                        binding.articlesSchemes.setVisibility(View.VISIBLE);
                    }
                    if (articlesData.isStatus()){
                        if (infoSchemes != null&& infoSchemes.size()>0  ) {
                            for (int i = 0; i < infoSchemes.size(); i++) {
                                Log.d(TAG, "" + infoSchemes.size());


                                binding.articlesSchemes.setHasFixedSize(true);

                                mLayoutManager = new LinearLayoutManager(getActivity());

                                // use a linear layout manager
                                binding.articlesSchemes.setLayoutManager(mLayoutManager);

                                // create an Object for Adapter
                                mAdapter = new NewsAdapter(getActivity(), infoSchemes, binding.articlesSchemes);

                                // set the adapter object to the Recyclerview
                                binding.articlesSchemes.setAdapter(mAdapter);



                                binding.mSwipeRefreshLayout.setRefreshing(false);

                                //set click event
                                binding.articlesSchemes.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesSchemes, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent setIntent = new Intent(getActivity(), SingleSchemesActivity.class);
                                        setIntent.putExtra("scheme_id", infoSchemes.get(position).getId());
                                        setIntent.putExtra("title",getResources().getString(R.string.schemes));
                                        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(setIntent);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {

                                    }
                                }));


                            }
                            //get values


                        }

                    } else {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), ""+articlesData.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Home> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        binding.shimmerViewContainer.startShimmer();
    }

    @Override
    public void onPause() {
        binding.shimmerViewContainer.stopShimmer();
        super.onPause();
    }
}
