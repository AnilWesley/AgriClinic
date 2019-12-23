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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticleListAdapter1;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;

import com.novaagritech.agriclinic.databinding.FragmentSearchMarticlesBinding;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMArticlesFragment extends Fragment {


    private FragmentSearchMarticlesBinding binding;
    private ProgressDialog pDialog;
    private static final String TAG = "ArticleListActivity11";
    private ArticleListAdapter1 adapter;
    private LinearLayoutManager linearLayoutManager;

    private int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES=1;
    private int currentPage = PAGE_START;
    private ApiInterface apiService;

    private List<InfoData> articlesDetails;
    private String article_tag="";
    private int currentYear,currentMonth;

    public SearchMArticlesFragment() {
        // Required empty public constructor
    }

    private String user_id,selected;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_search_marticles, container, false);
        View view = binding.getRoot();

        //here data must be an instance of the class MarsDataProvider


        pDialog = new ProgressDialog(getActivity());
       /* pDialog.setMessage("Loading...");
        pDialog.show();*/





        Intent i = getActivity().getIntent();

        article_tag = i.getStringExtra("article_tag");
        adapter = new ArticleListAdapter1(getActivity());

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        user_id= myAppPrefsManager.getUserId();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.articlesRecycle.setLayoutManager(linearLayoutManager);

        binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
        binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        binding.articlesRecycle.setAdapter(adapter);

        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String year = String.valueOf(currentYear);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
        @SuppressLint("SimpleDateFormat")
        String monthName = new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());


        selected= ""+currentYear+"-"+currentMonth;


        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int j = 2018; j <= thisYear; j++) {

            years.add(Integer.toString(j));
        }

        int yearPos=years.indexOf(year);

        ArrayList<String> months = new ArrayList<>();

        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        int monthPos= months.indexOf(monthName);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        binding.spinnerYear.setAdapter(adapter1);
        binding.spinnerYear.setSelection(yearPos);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, months);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        binding.spinnerMonth.setAdapter(adapter2);
        binding.spinnerMonth.setSelection(monthPos);


        binding.articlesRecycle.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                Log.d(TAG,""+currentPage);

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        loadFirstPage();

        binding.spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (currentMonth==position+1){
                  Log.d(TAG,"DO NOTHING");

                }
                else {

                    currentMonth=(position+1);
                    selected= ""+currentYear+"-"+currentMonth;

                    callArticleListApi();
                    loadFirstPage();
                    adapter = new ArticleListAdapter1(getActivity());
                    linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    binding.articlesRecycle.setLayoutManager(linearLayoutManager);
                    binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                    binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    binding.articlesRecycle.setAdapter(adapter);


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (currentYear==Integer.parseInt(years.get(position))){
                    Log.d(TAG,"DO NOTHING");
                }else {
                    currentYear= Integer.parseInt(years.get(position));
                    selected= ""+currentYear+"-"+currentMonth;

                    callArticleListApi();
                    loadFirstPage();
                    adapter = new ArticleListAdapter1(getActivity());
                    linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    binding.articlesRecycle.setLayoutManager(linearLayoutManager);
                    binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                    binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    binding.articlesRecycle.setAdapter(adapter);


                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






        return  view;


    }



    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        //init service and load data

        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        callArticleListApi().enqueue(new Callback<ArticlesList>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    if (response.code() == 200) {


                        Log.d(TAG, "" + response.toString());
                        assert response.body() != null;
                        ArticlesList articlesData = response.body();
                        if (articlesData.isStatus()) {
                            articlesDetails = fetchResults(response);
                            TOTAL_PAGES = response.body().getArticle_pages();
                            int size = articlesDetails.size();
                            Log.d(TAG, "SIZE" + size);
                            adapter.addAll(articlesDetails);
                            pDialog.dismiss();
                            binding.articlesRecycle.setVisibility(View.VISIBLE);
                            binding.emptyView.setVisibility(View.GONE);

                        } else {
                            pDialog.dismiss();
                            binding.articlesRecycle.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }


                        if (currentPage < TOTAL_PAGES) {
                            adapter.addLoadingFooter();

                        } else {
                            isLastPage = true;
                        }
                    }

                }
                // Stopping Shimmer Effect's animation after data is loaded to ListView
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        // TODO: 08/11/16 handle failure
                    }
                });


    }

    /**
     * @param response extracts List<{@link InfoData>} from response
     * @return
     */
    private List<InfoData> fetchResults(Response<ArticlesList> response) {

        ArticlesList articlesList = response.body();
        if (articlesList.isStatus()){
            Log.d(TAG,""+articlesList.getResponse());
        }

        return articlesList.getResponse();

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callArticleListApi().enqueue(new Callback<ArticlesList>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                articlesDetails = fetchResults(response);

                adapter.addAll(articlesDetails);
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
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
        jsonObject.addProperty("limit", "");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "5");
        jsonObject.addProperty("page", currentPage);
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("search_byDate", selected);
        jsonObject.addProperty("user_id", user_id);
        Log.d(TAG,""+jsonObject);

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