package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticleListAdapterPagination;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.ActivityArticlesListBinding;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ArticleListActivityPagination extends AppCompatActivity {

    private static final String TAG = "ArticleListActivity11";

    ArticleListAdapterPagination adapter;
    LinearLayoutManager linearLayoutManager;

    int PAGE_START = 1;
    boolean isLoading = false;
    boolean isLastPage = false;
    int TOTAL_PAGES=1;
    int currentPage = PAGE_START;
    ApiInterface movieService;
    ActivityArticlesListBinding binding;
    ProgressDialog pDialog;
    private List<Info> articlesDetails;
    String article_tag="";
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_articles_list);

        Intent i = getIntent();

        article_tag = i.getStringExtra("article_tag");
        adapter = new ArticleListAdapterPagination(this);

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(ArticleListActivityPagination.this);
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
        pDialog = new ProgressDialog(ArticleListActivityPagination.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        callArticleListApi().enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Articles articlesData = response.body();
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
            public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });

        //set click event
        binding.articlesRecycle.addOnItemTouchListener(new RecyclerItemClickListener(this, binding.articlesRecycle, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent setIntent = new Intent(ArticleListActivityPagination.this, SingleArticleActivity.class);

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
     * @param response extracts List<{@link Info >} from response
     * @return
     */
    private List<Info> fetchResults(Response<Articles> response) {

        Articles articles = response.body();
        if (articles.isStatus()){
            Log.d(TAG,""+ articles.getResponse());
        }

        return articles.getResponse();

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callArticleListApi().enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                articlesDetails = fetchResults(response);

                adapter.addAll(articlesDetails);
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
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
