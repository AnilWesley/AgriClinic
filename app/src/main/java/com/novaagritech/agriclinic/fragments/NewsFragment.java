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
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.SingleEventActivity;
import com.novaagritech.agriclinic.activities.SingleNewsActivity;
import com.novaagritech.agriclinic.adapters.NewsAdapter;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.FragmentNewsBinding;
import com.novaagritech.agriclinic.databinding.FragmentSchemesBinding;
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
public class NewsFragment extends Fragment {

    private RecyclerView articlesNews;
    private RecyclerView articlesSchemes;

    private NewsAdapter mAdapter;

    private List<InfoData> infoDataNews;
    private LinearLayoutManager mLayoutManager;
    private List<InfoData> infoDataSchemes;
    private List<InfoData> infoDataEvents;


    private String TAG = "News";


    private Handler handler;
    private ProgressDialog pDialog;

    public NewsFragment() {
        // Required empty public constructor
    }


    private FragmentNewsBinding binding;

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
        Call<SchemesData> call = service.processNewsList(jsonObject);
        call.enqueue(new Callback<SchemesData>() {
            @Override
            public void onResponse(@NonNull Call<SchemesData> call, @NonNull retrofit2.Response<SchemesData> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    SchemesData articlesData = response.body();
                    infoDataNews = response.body().getResponse();


                    if (articlesData.isStatus()) {
                        if (response.code() == 200) {


                            if (infoDataNews != null && infoDataNews.size() > 0) {
                                for (int i = 0; i < infoDataNews.size(); i++) {
                                    Log.d(TAG, "" + infoDataNews.size());


                                    binding.articlesNews.setHasFixedSize(true);

                                    mLayoutManager = new LinearLayoutManager(getActivity());

                                    // use a linear layout manager
                                    binding.articlesNews.setLayoutManager(mLayoutManager);

                                    // create an Object for Adapter
                                    mAdapter = new NewsAdapter(getActivity(), infoDataNews, binding.articlesNews);

                                    // set the adapter object to the Recyclerview
                                    binding.articlesNews.setAdapter(mAdapter);


                                    pDialog.dismiss();

                                    //set click event
                                    //set click event
                                    binding.articlesNews.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesNews, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            Intent setIntent = new Intent(getActivity(), SingleNewsActivity.class);

                                            setIntent.putExtra("news_id", infoDataNews.get(position).getId());
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
            public void onFailure(@NonNull Call<SchemesData> call, @NonNull Throwable t) {
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
