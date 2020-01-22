package com.novaagritech.agriclinic.fragments;




import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.ArticlesListAdapter;
import com.novaagritech.agriclinic.adapters.BannerListAdapter;
import com.novaagritech.agriclinic.adapters.MyAdapter;
import com.novaagritech.agriclinic.adapters.StoryAdapter;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;


import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.PaginationScrollListener;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {




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
    private SliderLayout sliderLayout;
    private PagerIndicator pagerIndicator;
    private List<Banners.BannerDetails> bannerDetailsList;
    private Stories1 stories;
    private LinearLayoutManager linearLayoutManager1;



    private static int currentPage1 = 0;


    public HomeFragment() {
        // Required empty public constructor
    }


    private String user_id;
    private StoryAdapter mAdapter;

    String token;
    private LinearLayoutManager mLayoutManager,mLayoutManager1;



    private View view1;


    private String name;
    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView articlesRecycle;
    private TextView emptyView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        shimmer=view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout=view.findViewById(R.id.mSwipeRefreshLayout);
        articlesRecycle=view.findViewById(R.id.articlesRecycle);
        emptyView=view.findViewById(R.id.emptyView);
        view1=view.findViewById(R.id.view1);
        sliderLayout = (SliderLayout) view.findViewById(R.id.banner_slider1);
        pagerIndicator = (PagerIndicator)view. findViewById(R.id.banner_slider_indicator);

        List<String> crops = new ArrayList<>();


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available)
                .showImageForEmptyUri(R.drawable.image_not_available)
                .showImageOnFail(R.drawable.image_not_available)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();


        List<Stories1> list = new ArrayList<>();

        shimmer.startShimmer();









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
        String selected = "" + currentYear + "-" + currentMonth;






        //data();
        //getStories1();

        loadFirstPage();
        getBanners();





        swipeRefreshLayout.setOnRefreshListener(() -> {
            articlesDetails = new ArrayList<Info>();
            bannerDetailsList = new ArrayList<Banners.BannerDetails>();

            swipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        swipeRefreshLayout.setRefreshing(true);
                        shimmer.startShimmer();

                        //getBanners();
                        loadFirstPage();


                    }
            );

        });
        swipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);






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
            public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {
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
                            articlesRecycle.setLayoutManager(linearLayoutManager);

                            articlesRecycle.setItemAnimator(new DefaultItemAnimator());
                            articlesRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

                            articlesRecycle.setAdapter(articleListAdapterTest);

                            articleListAdapterTest.notifyDataSetChanged();

                            swipeRefreshLayout.setRefreshing(false);
                            articlesRecycle.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);


                        } else {

                            articlesRecycle.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }


                       /* if (currentPage < TOTAL_PAGES) {
                           // adapter.addLoadingFooter();

                        } else {
                            isLastPage = true;
                        }*/
                    }
                    // Stopping Shimmer Effect's animation after data is loaded to ListView
                    view1.setVisibility(View.VISIBLE);
                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                }


                    }

                    @Override
                    public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        // TODO: 08/11/16 handle failure
                    }
                });


    }


    private void getBanners() {





        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_type", "2");

        //Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Banners> call = service.processBanners1(jsonObject);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(@NonNull Call<Banners> call, @NonNull retrofit2.Response<Banners> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    //Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Banners banners = response.body();
                    bannerDetailsList = response.body().getResponse();


                    if (banners.isStatus()){
                        if (bannerDetailsList != null&&bannerDetailsList.size()>0  ) {
                            for (int i = 0; i <bannerDetailsList.size(); i++) {
                                Log.d(TAG, "" + bannerDetailsList.size());


                            }
                            setupBannerSlider(bannerDetailsList);
                            //get values

                        }
                    } else {
                        Toast.makeText(getActivity(), ""+ banners.getMessage(), Toast.LENGTH_SHORT).show();
                    }



                }
            }

            @Override
            public void onFailure(@NonNull Call<Banners> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });




    }




    private void setupBannerSlider(final List<Banners.BannerDetails> bannerImages) {


        // Initialize new LinkedHashMap<ImageName, ImagePath>
        //final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();

        HashMap<String,String> slider_covers = new HashMap<String, String>();
        //Log.e(TAG,"BANNERSIZE"+bannerImages.size());



        for (int i=0;  i< bannerImages.size();  i++) {
            // Get bannerDetails at given Position from bannerImages List
            Banners.BannerDetails bannerData = bannerImages.get(i);

            // Put Image's Name and URL to the HashMap slider_covers
            slider_covers.put
                    (

                            bannerData.getId(), bannerData.getImage_url()
                    );





        }

        //Log.d(TAG,"BANNER1"+slider_covers.size());



        for(String name : slider_covers.keySet()) {
            // Initialize DefaultSliderView
            final DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());




            // Set Attributes(Name, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .empty(R.drawable.image_not_available)
                    .error(R.drawable.image_not_available)
                    .image(slider_covers.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);




            // Add DefaultSliderView to the SliderLayout
            sliderLayout.addSlider(defaultSliderView);


        }

        // Set PresetTransformer type of the SliderLayout
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);


        // Check if the size of Images in the Slider is less than 2
        if (slider_covers.size() < 2) {
            // Disable PagerTransformer
            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });

            // Hide Slider PagerIndicator
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        } else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }




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
        String article_tag = "";
        jsonObject.addProperty("search_value", article_tag);
        jsonObject.addProperty("search_byDate", "");
        jsonObject.addProperty("user_id", user_id);

        //Log.d(TAG,"JSONOB"+jsonObject);

        return apiService.processArticlesList1(jsonObject);
    }


   /* private void getStories1(){
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

    }*/

    @Override
    public void onResume() {
        super.onResume();
        shimmer.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmer.stopShimmer();
    }


}