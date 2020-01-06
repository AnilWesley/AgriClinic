package com.novaagritech.agriclinic.fragments;


import android.annotation.SuppressLint;
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

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.databinding.FragmentTermsBinding;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TermsFragment extends Fragment {

    String TAG="ABOUTFRAGMENT";


    private List<InfoData> infoDataList;
    public TermsFragment() {
        // Required empty public constructor
    }
    private ProgressDialog pDialog;

    private FragmentTermsBinding binding;
    private List<InfoData> articlesDetails;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_terms, container, false);
        View view = binding.getRoot();



        pDialog = new ProgressDialog( getActivity() );



        getArticle();



        return view;
    }






    private void getArticle() {

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
