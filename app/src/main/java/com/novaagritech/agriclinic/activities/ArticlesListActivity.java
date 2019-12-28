package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.DataAdapter;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.ActivityArticlesListBinding;
import com.novaagritech.agriclinic.interfaces.OnLoadMoreListener;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.YoutubeVideoModel;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesListActivity extends AppCompatActivity {


    

    private DataAdapter mAdapter;

    private List<InfoData> articlesDetails;

    ActivityArticlesListBinding binding;

    ProgressDialog pDialog;
    String TAG="ARTICLE_LIST";


    protected Handler handler;
    String article_tag="";

    int PAGE_START = 1;
    boolean isLoading = false;
    boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    int TOTAL_PAGES=1;
    int currentPage = PAGE_START;
     String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(this, R.layout.activity_articles_list);

        handler = new Handler();
        //getAllArticles();

        Intent i = getIntent();

        article_tag = i.getStringExtra("article_tag");
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(ArticlesListActivity.this);
        user_id= myAppPrefsManager.getUserId();

        binding.actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        pDialog = new ProgressDialog(ArticlesListActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", "");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "5");
        jsonObject.addProperty("page", currentPage);
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("user_id", user_id);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ArticlesList> call = service.processArticlesList1(jsonObject);
        call.enqueue(new Callback<ArticlesList>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    ArticlesList articlesData = response.body();
                    articlesDetails = response.body().getResponse();

                    if (articlesData.isStatus()) {
                        if (articlesDetails != null && articlesDetails.size() > 0) {
                            for (int i = 0; i < articlesDetails.size(); i++) {
                                Log.d(TAG, "" + articlesDetails.size());

                                mAdapter = new DataAdapter(ArticlesListActivity.this, articlesDetails, binding.articlesRecycle);

                                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(ArticlesListActivity.this);
                                //gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                binding.articlesRecycle.setLayoutManager(gridLayoutManager);
                                //homeRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                binding.articlesRecycle.setHasFixedSize(true);
                                // Set the Adapter and LayoutManager to the RecyclerView
                                binding.articlesRecycle.setAdapter(mAdapter);




                                mAdapter.notifyDataSetChanged();
                                pDialog.dismiss();
                            }
                            //get values


                        }else{
                            pDialog.dismiss();
                            binding.articlesRecycle.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                        }

                    }else {
                        pDialog.dismiss();
                        binding.articlesRecycle.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                        binding.emptyView.setText(articlesData.getMessage());
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });
        //set click event
        binding.articlesRecycle.addOnItemTouchListener(new RecyclerItemClickListener(this, binding.articlesRecycle, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent setIntent = new Intent(ArticlesListActivity.this, SingleArticleActivity.class);
                setIntent.putExtra("article_id", articlesDetails.get(position).getId());
                setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }





}
