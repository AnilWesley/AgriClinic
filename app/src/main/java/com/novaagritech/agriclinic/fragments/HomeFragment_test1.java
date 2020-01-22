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
import com.novaagritech.agriclinic.adapters.ArticleListAdapter_test1;
import com.novaagritech.agriclinic.adapters.BannerListAdapter;
import com.novaagritech.agriclinic.adapters.StoryAdapter;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.FragmentHomeTestBinding;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment_test1 extends Fragment {



    private ProgressDialog pDialog;
    private static final String TAG = "ArticleListActivity11";
    //private ArticleListAdapterPagination adapter;
    private ArticleListAdapter_test1 articleListAdapterTest;
    private BannerListAdapter articlesListAdapter;

    private LinearLayoutManager linearLayoutManager;

    private int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES=1;
    private int currentPage = PAGE_START;
    private ApiInterface apiService;


    private List<Info> articlesDetails;
    private List<Banners.BannerDetails> bannerDetails;


    private String article_tag="";
    private Stories1 stories;


    public HomeFragment_test1() {
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



        Intent i = Objects.requireNonNull(getActivity()).getIntent();

        article_tag = i.getStringExtra("article_tag");
       // adapter = new ArticleListAdapterPagination(getActivity());


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
        callArticleListApi().enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        assert response.body() != null;
                        Articles articlesData = response.body();
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
                            articleListAdapterTest = new ArticleListAdapter_test1(getActivity(),articlesDetails,bannerDetails);
                            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            binding.articlesRecycle.setLayoutManager(linearLayoutManager);

                            binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                            binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));




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
                    public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {
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
    private Call<Articles> callArticleListApi() {
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