package com.novaagritech.agriclinic.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.novaagritech.agriclinic.activities.SingleArticleActivity;
import com.novaagritech.agriclinic.adapters.ArticlesListAdapter;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.databinding.FragmentArticlesBinding;
import com.novaagritech.agriclinic.modals.ArticlesData;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ArticlesFragment extends Fragment {


    private ArticlesListAdapter articlesListAdapater;
    private List<ArticlesData.ArticlesDetails> articlesDetails;
    private List<InfoData> infoData;
    private List<InfoData> infoData1;
    private List<InfoData> infoData2;

    private ProgressDialog pDialog;
    private String TAG="Articles";

    private FragmentArticlesBinding binding;
    private  String user_id;
    public ArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_articles, container, false);
        View view = binding.getRoot();

        //here data must be an instance of the class MarsDataProvider

        pDialog = new ProgressDialog(getActivity());
      /*  pDialog.setMessage("Loading...");
        pDialog.show();*/

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        user_id= myAppPrefsManager.getUserId();
        getArticles1();

        return view;
    }


    private void getArticles1(){

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("limit", "4");
        jsonObject.addProperty("crop_id", "5");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ArticlesData> call = service.processArticlesTypes(jsonObject);
        call.enqueue(new Callback<ArticlesData>() {
            @Override
            public void onResponse(@NonNull Call<ArticlesData> call, @NonNull retrofit2.Response<ArticlesData> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    ArticlesData articlesData = response.body();
                    articlesDetails = response.body().getResponse();
                    infoData =articlesDetails.get(0).getTrending();
                    infoData1 =articlesDetails.get(0).getLatest();
                    infoData2 =articlesDetails.get(0).getRecommended();

                    if (articlesData.isStatus()){
                        if (articlesDetails != null&&articlesDetails.size()>0  ) {
                            for (int i = 0; i <articlesDetails.size(); i++) {
                                Log.d(TAG, "" + articlesDetails.size());

                                articlesListAdapater = new ArticlesListAdapter(getActivity(), infoData);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                                binding.articlesRecycleTrending.setLayoutManager(linearLayoutManager);
                                binding.articlesRecycleTrending.setAdapter(articlesListAdapater);

                                //set click event
                                binding.articlesRecycleTrending.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesRecycleTrending, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent setIntent = new Intent(getActivity(), SingleArticleActivity.class);
                                        setIntent.putExtra("article_id", infoData.get(position).getId());
                                        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(setIntent);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {

                                    }
                                }));

                                articlesListAdapater = new ArticlesListAdapter(getActivity(), infoData1);

                                LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                                binding.articlesRecycleLatest.setLayoutManager(linearLayoutManager1);
                                binding.articlesRecycleLatest.setAdapter(articlesListAdapater);

                                //set click event
                                binding.articlesRecycleLatest.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesRecycleTrending, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent setIntent = new Intent(getActivity(), SingleArticleActivity.class);
                                        setIntent.putExtra("article_id", infoData1.get(position).getId());
                                        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(setIntent);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {

                                    }
                                }));


                                articlesListAdapater = new ArticlesListAdapter(getActivity(), infoData2);
                                LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                                binding.articlesRecycleRecomended.setLayoutManager(linearLayoutManager2);
                                binding.articlesRecycleRecomended.setAdapter(articlesListAdapater);

                                //set click event
                                binding.articlesRecycleRecomended.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), binding.articlesRecycleTrending, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent setIntent = new Intent(getActivity(), SingleArticleActivity.class);
                                        setIntent.putExtra("article_id", infoData2.get(position).getId());
                                        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(setIntent);
                                    }

                                    @Override
                                    public void onLongItemClick(View view, int position) {

                                    }
                                }));



                            }
                            //get values

                            pDialog.dismiss();
                        }

                    } else {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), ""+articlesData.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<ArticlesData> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });



    }


}