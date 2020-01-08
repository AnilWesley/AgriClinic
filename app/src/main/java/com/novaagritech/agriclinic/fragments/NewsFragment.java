package com.novaagritech.agriclinic.fragments;


import android.annotation.SuppressLint;
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
import com.novaagritech.agriclinic.activities.SingleNewsActivity;
import com.novaagritech.agriclinic.adapters.NewsAdapter;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.FragmentNewsBinding;
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
public class NewsFragment extends Fragment {



    private NewsAdapter mAdapter;

    private List<Info> infoNews;
    private LinearLayoutManager mLayoutManager;



    private String TAG = "News";

    private ProgressDialog pDialog;

    public NewsFragment() {
        // Required empty public constructor
    }


    private Handler handler;
    private FragmentNewsBinding binding;


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_news, container, false);
        View view = binding.getRoot();

        binding.shimmerViewContainer.startShimmer();

        handler = new Handler();

        pDialog = new ProgressDialog(getActivity());
        /*pDialog.setMessage("Loading...");
        pDialog.show();

*/
        getNews();


        binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            infoNews = new ArrayList<Info>();

            binding.mSwipeRefreshLayout.post(() -> {
                //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        binding.shimmerViewContainer.startShimmer();

                        getNews();
            }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);






        return view;
    }




    private void getNews(){

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("limit", "");
        jsonObject.addProperty("crop_id", "5");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processNewsList(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull retrofit2.Response<Home> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    infoNews = response.body().getResponse();

                    if (infoNews.isEmpty()) {
                        binding.articlesNews.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);


                    } else {
                        binding.emptyView.setVisibility(View.GONE);
                        binding.articlesNews.setVisibility(View.VISIBLE);

                    }

                    if (articlesData.isStatus()) {
                        if (response.code() == 200) {


                            if (infoNews != null && infoNews.size() > 0) {
                                for (int i = 0; i < infoNews.size(); i++) {
                                    Log.d(TAG, "" + infoNews.size());


                                    binding.articlesNews.setHasFixedSize(true);

                                    mLayoutManager = new LinearLayoutManager(getActivity());

                                    // use a linear layout manager
                                    binding.articlesNews.setLayoutManager(mLayoutManager);

                                    // create an Object for Adapter
                                    mAdapter = new NewsAdapter(getActivity(), infoNews,binding.articlesNews);

                                    // set the adapter object to the Recyclerview
                                    binding.articlesNews.setAdapter(mAdapter);
                                    binding.mSwipeRefreshLayout.setRefreshing(false);

                                    //pDialog.dismiss();

                                  /*  mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                        @Override
                                        public void onLoadMore() {
                                            //add null , so the mAdapter will check view_type and show progress bar at bottom
                                            infoNews.add(null);
                                            mAdapter.notifyItemInserted(infoNews.size() - 1);

                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //   remove progress item
                                                    infoNews.remove(infoNews.size() - 1);
                                                    mAdapter.notifyItemRemoved(infoNews.size());
                                                    //add items one by one

                                                    int start = infoNews.size();

                                                    int end = start + 7;

                                                    for (int i = start + 1; i <= end; i++) {



                                                    }


                                                    mAdapter.notifyDataSetChanged();
                                                    mAdapter.setLoaded();



                                                }


                                            }, 1000);

                                        }
                                    });*/


                                    //set click event
                                    //set click event
                                    binding.articlesNews.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesNews, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            Intent setIntent = new Intent(getActivity(), SingleNewsActivity.class);

                                            setIntent.putExtra("news_id", infoNews.get(position).getId());
                                            setIntent.putExtra("title", getResources().getString(R.string.news));
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
                            Toast.makeText(getActivity(), "" + articlesData.getMessage(), Toast.LENGTH_SHORT).show();
                        }



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
