package com.novaagritech.agriclinic.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticleListAdapter1_test;
import com.novaagritech.agriclinic.adapters.ArticlesListAdapter3;
import com.novaagritech.agriclinic.adapters.StoryAdapter;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.FragmentHomeTestBinding;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.BannerData;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment_test extends Fragment {



    private ProgressDialog pDialog;
    private static final String TAG = "ArticleListActivity11";
    //private ArticleListAdapter1 adapter;
    private ArticleListAdapter1_test articleListAdapterTest;
    private ArticlesListAdapter3 articlesListAdapter;

    private LinearLayoutManager linearLayoutManager;

    private int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES=1;
    private int currentPage = PAGE_START;
    private ApiInterface apiService;


    private List<InfoData> articlesDetails;
    private List<BannerData.BannerDetails> bannerDetails;


    private String article_tag="";
    private Stories1 stories;


    public HomeFragment_test() {
        // Required empty public constructor
    }


    private String user_id,selected;
    private StoryAdapter mAdapter;


    private LinearLayoutManager mLayoutManager,mLayoutManager1;


    private FragmentHomeTestBinding binding;


    private List<Stories1> list;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home_test, container, false);
        View view = binding.getRoot();



        //list=new ArrayList<>();

        binding.shimmerViewContainer.startShimmer();



        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();



        Intent i = getActivity().getIntent();

        article_tag = i.getStringExtra("article_tag");
       // adapter = new ArticleListAdapter1(getActivity());


        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        user_id= myAppPrefsManager.getUserId();


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        selected= ""+ currentYear +"-"+ currentMonth;

        //getStories1();

        loadFirstPage();

        //loadBanners();









        return  view;


    }



    private void loadFirstPage() {
        //init service and load data

        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        callArticleListApi().enqueue(new Callback<ArticlesList>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        assert response.body() != null;
                        ArticlesList articlesData = response.body();
                        if (articlesData.isStatus()) {

                            articlesDetails = response.body().getResponse();
                            bannerDetails = response.body().getBlist();


                            TOTAL_PAGES = response.body().getArticle_pages();
                            int size = articlesDetails.size();
                            int size1 = bannerDetails.size();
                            Log.d(TAG, "SIZEs" + size+"a"+size1);
                           /* linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            binding.articlesRecycle.setLayoutManager(linearLayoutManager);

                            binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                            binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                            binding.articlesRecycle.setAdapter(adapter);
                            adapter.addAll(articlesDetails);

                            adapter.notifyDataSetChanged();*/
                            articleListAdapterTest = new ArticleListAdapter1_test (getActivity(),articlesDetails,bannerDetails);
                            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            binding.articlesRecycle.setLayoutManager(linearLayoutManager);

                            binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                            binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                            binding.articlesRecycle.setAdapter(articleListAdapterTest);

                            articleListAdapterTest.notifyDataSetChanged();
                            pDialog.dismiss();
                            binding.articlesRecycle.setVisibility(View.VISIBLE);
                            binding.emptyView.setVisibility(View.GONE);


                        } else {
                            pDialog.dismiss();
                            binding.articlesRecycle.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }


                       /* if (currentPage < TOTAL_PAGES) {
                           // adapter.addLoadingFooter();

                        } else {
                            isLastPage = true;
                        }*/
                    }
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                }


                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        // TODO: 08/11/16 handle failure
                    }
                });


    }





    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<ArticlesList> callArticleListApi() {
        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", "4");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "");
        jsonObject.addProperty("page", currentPage);
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("search_byDate", "");
        jsonObject.addProperty("user_id", user_id);

        return apiService.processArticlesList1(jsonObject);
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