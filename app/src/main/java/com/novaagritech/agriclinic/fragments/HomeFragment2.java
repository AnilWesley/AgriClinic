package com.novaagritech.agriclinic.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.novaagritech.agriclinic.activities.ArticleListActivityPagination;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.LocationTrack;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.adapters.ArticlesListHome2Adapater;
import com.novaagritech.agriclinic.activities.SingleArticleActivity;
import com.novaagritech.agriclinic.modals.weathermodel.OpenWeatherMap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment2 extends Fragment {

    private String TAG = "HOME_ACTIVITY";
    private ProgressDialog pDialog;
    private ArticlesListHome2Adapater articlesListHome2Adapater;

    private RecyclerView articlesRecycle;
    private SliderLayout sliderLayout;
    private PagerIndicator pagerIndicator;

    private List<Banners.BannerDetails> bannerDetailsList;
    TextView articleMore;
    Address address = null;
    String addr = "";
    String zipcode = "";
    String city = "";
    String state = "";

    double longitude;
    double latitude;
    private TextView temp_degree;
    private TextView temp_type;
    private TextView temp_desc;
    private OpenWeatherMap openWeatherMap;
    private List<Info> articlesDetails;
    private String user_id;
    public HomeFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
        pDialog = new ProgressDialog(getActivity());


        openWeatherMap=new OpenWeatherMap();

        sliderLayout = (SliderLayout) rootView.findViewById(R.id.banner_slider1);
        pagerIndicator = (PagerIndicator)rootView. findViewById(R.id.banner_slider_indicator);

        articlesRecycle=(RecyclerView)rootView.findViewById(R.id.articlesRecycle);

        articleMore=(TextView) rootView.findViewById(R.id.articleMore);

        temp_degree=(TextView) rootView.findViewById(R.id.temp_degree);
        temp_type=(TextView) rootView.findViewById(R.id.temp_type);
        temp_desc=(TextView) rootView.findViewById(R.id.temp_desc);


        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(getActivity());
        user_id= myAppPrefsManager.getUserId();



        getArticles();


        getBanners();
        getLocation();

        articleMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), ArticleListActivityPagination.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        return  rootView;
    }


    private void getArticles(){
        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", "4");
        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("crop_id", "5");
        jsonObject.addProperty("search_value", "");
        jsonObject.addProperty("user_id",user_id);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Articles> call = service.processArticlesList1(jsonObject);
        call.enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull retrofit2.Response<Articles> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Articles articlesData = response.body();
                    articlesDetails = response.body().getResponse();

                    if (articlesData.isStatus()) {
                        if (articlesDetails != null && articlesDetails.size() > 0) {
                            for (int i = 0; i < articlesDetails.size(); i++) {
                                Log.d(TAG, "" + articlesDetails.size());

                                articlesListHome2Adapater = new ArticlesListHome2Adapater(getActivity(), articlesDetails);
                               /* DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(articlesRecycle.getContext(),DividerItemDecoration.VERTICAL);
                                articlesRecycle.addItemDecoration(dividerItemDecoration);*/

                                GridLayoutManager llm = new GridLayoutManager(getActivity(),2);
                                llm.setOrientation(GridLayoutManager.VERTICAL);
                                articlesRecycle.setLayoutManager(llm);
                                articlesRecycle.setAdapter(articlesListHome2Adapater);


                                articlesListHome2Adapater.notifyDataSetChanged();
                                pDialog.dismiss();
                            }
                            //get values

                        }
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });

        //set click event
        articlesRecycle.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), articlesRecycle, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent setIntent = new Intent(getActivity(), SingleArticleActivity.class);
                setIntent.putExtra("article_id", articlesDetails.get(position).getId());
                setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }




    private void getBanners() {


        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_type", "2");

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Banners> call = service.processBanners1(jsonObject);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(@NonNull Call<Banners> call, @NonNull retrofit2.Response<Banners> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Banners banners = response.body();
                    bannerDetailsList = response.body().getResponse();


                    if (banners.isStatus()){
                        if (bannerDetailsList != null&&bannerDetailsList.size()>0  ) {
                            for (int i = 0; i <bannerDetailsList.size(); i++) {
                                Log.d(TAG, "" + bannerDetailsList.size());

                                pDialog.dismiss();
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
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });




    }




    private void setupBannerSlider(final List<Banners.BannerDetails> bannerImages) {


        // Initialize new LinkedHashMap<ImageName, ImagePath>
        //final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();

        HashMap<String,String> slider_covers = new HashMap<String, String>();
        Log.e(TAG,"BANNERSIZE"+bannerImages.size());


        for (int i=0;  i< bannerImages.size();  i++) {
            // Get bannerDetails at given Position from bannerImages List
            Banners.BannerDetails bannerData = bannerImages.get(i);

            // Put Image's Name and URL to the HashMap slider_covers
            slider_covers.put
                    (

                            bannerData.getId(), bannerData.getImage_url()
                    );
            // Log.d(TAG,"BANNER"+slider_covers.size());




        }

        Log.d(TAG,"BANNER1"+slider_covers.size());



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


    private void getLocation(){

        LocationTrack locationTrack = new LocationTrack(getActivity());
        if (locationTrack.canGetLocation()) {


            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();

            String lat= String.valueOf(latitude);
            String lon= String.valueOf(longitude);
            // Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();

            Log.d("LOCATION", "" + "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));

            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);


                if (addresses != null && addresses.size() > 0) {

                    addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();

                    for (int i = 0; i < addresses.size(); i++) {
                        address = addresses.get(i);
                        if (address.getPostalCode() != null) {
                            zipcode = address.getPostalCode();
                            city = address.getLocality();
                            Log.d("LOCATION", "" + zipcode + "" + city);
                            break;
                        }


                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            String  url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&" + "lon=" + lon + "&" + "appid=71916be0edf19ffd3b2018e6289fb6d3";
            Log.e("Ressponse",""+url);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Gson gson = new Gson();
                    Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
                    openWeatherMap = gson.fromJson(response,mType);

                    Log.e("Ressponse",""+response);
                    double temp= (openWeatherMap.getMain().getTemp()-(273.15));

                    temp_degree.setText(String.format("%.0f °C",temp));
                    temp_type.setText(openWeatherMap.getWeather().get(0).getMain());
                    temp_desc.setText(openWeatherMap.getWeather().get(0).getDescription());
                    //temp_degree.setText(String.format("%.2f °C",""+temp));



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            requestQueue.add(stringRequest);



        } else {

            locationTrack.showSettingsAlert();
        }



    }





}