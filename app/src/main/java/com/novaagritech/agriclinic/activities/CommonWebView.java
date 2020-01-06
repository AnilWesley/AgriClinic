package com.novaagritech.agriclinic.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.databinding.ActivityCommonWebViewBinding;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonWebView extends AppCompatActivity {

    ActivityCommonWebViewBinding binding;

    String TAG = "Articles";


    private List<InfoData> articlesDetails;

    private ProgressDialog pDialog;
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView ( CommonWebView.this, R.layout.activity_common_web_view );
        binding.actionImage.setOnClickListener (v -> onBackPressed ( ));

        pDialog = new ProgressDialog ( this );


        Intent intent=getIntent();
        value=intent.getStringExtra("value");

        if (value.equalsIgnoreCase("1")){
            binding.cardView1.setVisibility(View.VISIBLE);
            binding.cardView2.setVisibility(View.GONE);
            getArticle();
        }
        if (value.equalsIgnoreCase("2")){
            binding.cardView1.setVisibility(View.GONE);
            binding.cardView2.setVisibility(View.VISIBLE);
            getArticle1();
        }



    }


    public void getArticle() {

        pDialog.setMessage ( "Loading..." );
        pDialog.show ( );
        pDialog.setCancelable ( false );

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject ( );


        jsonObject.addProperty ( "user_id", "1" );

        ApiInterface service = RetrofitClientInstance.getRetrofitInstance ( ).create ( ApiInterface.class );
        Call<ArticlesList> call = service.processPrivacyPolicy ( jsonObject );
        call.enqueue ( new Callback<ArticlesList>( ) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {


                // Check if the Response is successful
                if (response.isSuccessful ( )) {

                    assert response.body ( ) != null;
                    ArticlesList articlesData = response.body ( );
                    articlesDetails = response.body ( ).getResponse ( );

                    if (articlesData.isStatus ( )) {
                        if (articlesDetails != null && articlesDetails.size ( ) > 0) {
                            for (int j = 0; j < articlesDetails.size ( ); j++) {
                                Log.d(TAG, "" + articlesDetails.size());

                                binding.textDesc1.loadDataWithBaseURL(null, articlesDetails.get(0).getTerms_conditions() , "text/html; charset=utf-8", "UTF-8", null);
                                pDialog.dismiss ( );
                            }


                        }


                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                pDialog.dismiss ( );
                Log.d ( "ResponseF", "" + t );
            }
        } );

    }


    public void getArticle1() {

        pDialog.setMessage ( "Loading..." );
        pDialog.show ( );
        pDialog.setCancelable ( false );

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject ( );


        jsonObject.addProperty ( "user_id", "1" );

        ApiInterface service = RetrofitClientInstance.getRetrofitInstance ( ).create ( ApiInterface.class );
        Call<ArticlesList> call = service.processPrivacyPolicy ( jsonObject );
        call.enqueue ( new Callback<ArticlesList>( ) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {


                // Check if the Response is successful
                if (response.isSuccessful ( )) {

                    assert response.body ( ) != null;
                    ArticlesList articlesData = response.body ( );
                    articlesDetails = response.body ( ).getResponse ( );

                    if (articlesData.isStatus ( )) {
                        if (articlesDetails != null && articlesDetails.size ( ) > 0) {
                            for (int j = 0; j < articlesDetails.size ( ); j++) {
                                Log.d(TAG, "" + articlesDetails.size());

                                binding.textDesc2.loadDataWithBaseURL(null, articlesDetails.get(0).getPrivacy_policy() , "text/html; charset=utf-8", "UTF-8", null);
                                pDialog.dismiss ( );
                            }


                        }


                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
                pDialog.dismiss ( );
                Log.d ( "ResponseF", "" + t );
            }
        } );

    }
}
