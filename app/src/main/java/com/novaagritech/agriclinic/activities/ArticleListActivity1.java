package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticleListAdapter1;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.ActivityArticlesListBinding;
import com.novaagritech.agriclinic.databinding.ActivityArticlesListBindingImpl;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ArticleListActivity1 extends AppCompatActivity {

    private static final String TAG = "ArticleListActivity11";

    ArticleListAdapter1 adapter;
    LinearLayoutManager linearLayoutManager;

    int PAGE_START = 1;
    boolean isLoading = false;
    boolean isLastPage = false;
    int TOTAL_PAGES=1;
    int currentPage = PAGE_START;
    ApiInterface movieService;
    ActivityArticlesListBinding binding;
    ProgressDialog pDialog;
    private List<InfoData> articlesDetails;
    String article_tag="";
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_articles_list);

        Intent i = getIntent();

        article_tag = i.getStringExtra("article_tag");
        adapter = new ArticleListAdapter1(this);

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(ArticleListActivity1.this);
        user_id= myAppPrefsManager.getUserId();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.articlesRecycle.setLayoutManager(linearLayoutManager);

        binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
        binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        binding.articlesRecycle.setAdapter(adapter);



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

        //init service and load data
        movieService = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);


        loadFirstPage();

    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");
        pDialog = new ProgressDialog(ArticleListActivity1.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        callArticleListApi().enqueue(new Callback<ArticlesList>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    ArticlesList articlesData = response.body();
                    if (articlesData.isStatus()) {
                        articlesDetails = fetchResults(response);
                        TOTAL_PAGES = response.body().getArticle_pages();
                        int size = articlesDetails.size();
                        Log.d(TAG, "SIZE" + size);
                        adapter.addAll(articlesDetails);
                        pDialog.dismiss();
                    }else{
                        pDialog.dismiss();
                        binding.articlesRecycle.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                    if (currentPage <= TOTAL_PAGES)
                    {
                        adapter.addLoadingFooter();

                    }
                    else {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });

        //set click event
        binding.articlesRecycle.addOnItemTouchListener(new RecyclerItemClickListener(this, binding.articlesRecycle, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent setIntent = new Intent(ArticleListActivity1.this, SingleArticleActivity.class);

                setIntent.putExtra("article_id", articlesDetails.get(position).getId());
                Log.d(TAG,""+articlesDetails.get(position).getId());
                setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

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
        jsonObject.addProperty("limit", "10");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "5");
        jsonObject.addProperty("page", currentPage);
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("user_id", user_id);
        Log.d(TAG,""+jsonObject);

        return movieService.processArticlesList1(jsonObject);
}


}
