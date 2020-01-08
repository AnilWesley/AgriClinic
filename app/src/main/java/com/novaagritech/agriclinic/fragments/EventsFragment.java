package com.novaagritech.agriclinic.fragments;


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
import com.novaagritech.agriclinic.activities.SingleEventActivity;
import com.novaagritech.agriclinic.adapters.EventsAdapter;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.FragmentEventsBinding;
import com.novaagritech.agriclinic.interfaces.OnLoadMoreListener;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {



    private EventsAdapter mAdapter;

    private List<Info> infoEvents;


    private String TAG = "News";

    private LinearLayoutManager mLayoutManager;
    private Handler handler;


    public EventsFragment() {
        // Required empty public constructor
    }


    private FragmentEventsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_events, container, false);
        View view = binding.getRoot();
        binding.shimmerViewContainer.startShimmer();
        handler = new Handler();

        getEvents();

        binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            infoEvents = new ArrayList<Info>();

            binding.mSwipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        binding.shimmerViewContainer.startShimmer();
                        getEvents();
                    }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);

        return view;
    }


    private void getEvents() {




        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "1");
        jsonObject.addProperty("limit", "");
        jsonObject.addProperty("crop_id", "5");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processEventsList(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull retrofit2.Response<Home> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    infoEvents = response.body().getResponse();

                    if (infoEvents.isEmpty()) {
                        binding.articlesEvents.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);


                    } else {
                        binding.emptyView.setVisibility(View.GONE);
                        binding.articlesEvents.setVisibility(View.VISIBLE);

                    }
                    Collections.sort(infoEvents, (lhs, rhs) ->
                            lhs.getStart_date().compareTo(rhs.getStart_date()));

                    if (articlesData.isStatus()){
                        if (infoEvents != null&& infoEvents.size()>0  ) {
                            for (int i = 0; i < infoEvents.size(); i++) {
                                Log.d(TAG, "" + infoEvents.size());




                                binding.articlesEvents.setHasFixedSize(true);

                                mLayoutManager = new LinearLayoutManager(getActivity());

                                // use a linear layout manager
                                binding.articlesEvents.setLayoutManager(mLayoutManager);

                                // create an Object for Adapter
                                mAdapter = new EventsAdapter(getActivity(), infoEvents, binding.articlesEvents);

                                // set the adapter object to the Recyclerview
                                binding.articlesEvents.setAdapter(mAdapter);

                                mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                    @Override
                                    public void onLoadMore() {

                                    }
                                });



                                binding.mSwipeRefreshLayout.setRefreshing(false);
                                //set click event
                                binding.articlesEvents.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesEvents, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent setIntent = new Intent(getActivity(), SingleEventActivity.class);
                                        setIntent.putExtra("event_id", infoEvents.get(position).getId());
                                        setIntent.putExtra("title",getResources().getString(R.string.events));

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

                        Toast.makeText(getActivity(), ""+articlesData.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Home> call, @NonNull Throwable t) {

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
