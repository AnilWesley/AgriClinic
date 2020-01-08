package com.novaagritech.agriclinic.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticlesListAdapter;
import com.novaagritech.agriclinic.adapters.BannerListAdapter;
import com.novaagritech.agriclinic.adapters.StoryAdapter;
import com.novaagritech.agriclinic.app.AppController;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.FragmentHome1Binding;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment1 extends Fragment {




    private static final String TAG = "ArticleListActivity11";
    //private ArticleListAdapterPagination adapter;
    private ArticlesListAdapter articleListAdapterTest;
    private BannerListAdapter bannerListAdapter;

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


    public HomeFragment1() {
        // Required empty public constructor
    }


    private String user_id,selected;
    private StoryAdapter mAdapter;

    String token;
    private LinearLayoutManager mLayoutManager,mLayoutManager1;


    private FragmentHome1Binding binding;


    private List<Stories1> list;
    private List<String> crops;
    private String name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home1, container, false);
        View view = binding.getRoot();
        crops=new ArrayList<>();





        list=new ArrayList<>();

        binding.shimmerViewContainer.startShimmer();








      /* binding.button1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               try {

                   article_tag = binding.editTextInput.getText().toString().trim();
                   callArticleListApi();
                   loadFirstPage();


               } catch (Exception e) {
                   // TODO: handle exception
               }
           }
       });*/


       /* Intent i = getActivity().getIntent();

        article_tag = i.getStringExtra("article_tag");*/
       // adapter = new ArticleListAdapterPagination(getActivity());


        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        user_id= myAppPrefsManager.getUserId();


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        selected= ""+ currentYear +"-"+ currentMonth;






        //data();
        getStories1();

        loadFirstPage();

        loadBanners();




        binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            articlesDetails = new ArrayList<Info>();
            bannerDetails = new ArrayList<Banners.BannerDetails>();

            binding.mSwipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        binding.shimmerViewContainer.startShimmer();

                        loadFirstPage();

                        loadBanners();
                    }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);






        return  view;


    }


   /* public void data(){




        String url = "http://omnisoft.in/farmer/api/v1/crops.php";


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("URL",""+ url);
                try {
                    crops.clear();
                    JSONObject jsonObject = new JSONObject(response);


                    JSONArray jsonArray = jsonObject.getJSONArray("Content");

                    Log.d("JSONOBJ1",""+jsonArray.length());



                    for (int i = 0; i < jsonArray.length(); i++) {

                       name =jsonArray.getJSONObject(i).getString("cropName");
                       crops.add(name);


                    }
                    Log.d("JSONOBJ1",""+crops.size());
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, crops);
                    binding.editTextInput.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }*/


    private void loadFirstPage() {
        //init service and load data

        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        callArticleListApi().enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull retrofit2.Response<Articles> response) {
                // Got data. Send it to adapter

                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        assert response.body() != null;
                        Articles articlesData = response.body();
                        if (articlesData.isStatus()) {

                            articlesDetails = response.body().getResponse();
                            TOTAL_PAGES = response.body().getArticle_pages();

                            articleListAdapterTest = new ArticlesListAdapter(getActivity(),articlesDetails);
                            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            binding.articlesRecycle.setLayoutManager(linearLayoutManager);

                            binding.articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                            binding.articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                            binding.articlesRecycle.setAdapter(articleListAdapterTest);

                            articleListAdapterTest.notifyDataSetChanged();

                            binding.mSwipeRefreshLayout.setRefreshing(false);
                            binding.articlesRecycle.setVisibility(View.VISIBLE);
                            binding.emptyView.setVisibility(View.GONE);


                        } else {

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

    private void loadBanners() {



        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_type", "2");

        //Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Banners> call = service.processBanners1(jsonObject);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(@NonNull Call<Banners> call, @NonNull Response<Banners> response) {

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        Banners articlesData1 = response.body();
                        bannerDetails = response.body().getResponse();

                        if (articlesData1.isStatus()) {
                            if (bannerDetails != null && bannerDetails.size() > 0) {
                                for (int i = 0; i < bannerDetails.size(); i++) {
                                    //Log.d(TAG, "SIZE" + bannerDetails.size());

                                    //get values
                                    bannerListAdapter = new BannerListAdapter(getActivity(), bannerDetails);

                                    LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                                    binding.articlesBanners.setLayoutManager(linearLayoutManager1);


                                    binding.articlesBanners.setAdapter(bannerListAdapter);
                                    bannerListAdapter.notifyDataSetChanged();

                                    binding.mSwipeRefreshLayout.setRefreshing(false);




                                }


                            }
                        }


                    }
                }
                // Stopping Shimmer Effect's animation after data is loaded to ListView
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Banners> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
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
        jsonObject.addProperty("limit", "20");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "");
        jsonObject.addProperty("page", currentPage);
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("search_byDate", "");
        jsonObject.addProperty("user_id", user_id);

        //Log.d(TAG,"JSONOB"+jsonObject);

        return apiService.processArticlesList1(jsonObject);
    }


    private void getStories1(){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("language_id", "2");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = jsonBody.toString();
        //Log.d(TAG, ""+mRequestBody);
        // Tag used to cancel the request
        String tag_json_obj = "pqlist";

        String url ="https://www.agriclinic.org/admin/app/ws/stories";


        JsonObjectRequest jsonObjReq = new JsonObjectRequest( Request.Method.POST,
                url, null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                       // Log.d(TAG, response.toString());


                        try{
                            if (response.getString("status").equalsIgnoreCase("true")) {

                                try
                                {

                                    JSONObject jObject= response.getJSONObject("response");
                                    Iterator<String> keys = jObject.keys();
                                    while( keys.hasNext() )
                                    {
                                        String key = keys.next();
                                       // Log.v("**********", "**********");
                                        //Log.v(TAG, key);
                                        JSONObject innerJObject = jObject.getJSONObject(key);
                                        Iterator<String> innerKeys = innerJObject.keys();
                                        while( innerKeys.hasNext() )
                                        {
                                            String key1 = innerKeys.next();

                                            String value1 = innerJObject.getString(key1);

                                            Log.v(TAG, value1);

                                            JSONObject innerJObject1 = new JSONObject(value1);

                                            stories=new Stories1(innerJObject1.getString("story_id"),
                                                    innerJObject1.getString("user_id"),
                                                    innerJObject1.getString("image_url"),
                                                    innerJObject1.getString("vedio_url"),
                                                    innerJObject1.getBoolean("viewed_status"),
                                                    innerJObject1.getInt("is_vedio"));


                                        }
                                        list.add(stories);

                                        binding.articlesStories.setHasFixedSize(true);


                                        mLayoutManager = new LinearLayoutManager(getContext(),
                                                LinearLayoutManager.HORIZONTAL, false);
                                        // use a linear layout manager
                                        binding.articlesStories.setLayoutManager(mLayoutManager);
                                        // create an Object for Adapter

                                        mAdapter = new StoryAdapter(getActivity(), list);
                                        // set the adapter object to the Recyclerview
                                        binding.articlesStories.setAdapter(mAdapter);





                                    }
                                }
                                catch (JSONException e)
                                {   e.printStackTrace();    }



                            }
                            else {

                                Log.d(TAG,"MSG");
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }



                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Try Again...", Toast.LENGTH_SHORT).show();
                // hide the progress dialog

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");

                } catch (UnsupportedEncodingException uee) {


                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }

            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

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