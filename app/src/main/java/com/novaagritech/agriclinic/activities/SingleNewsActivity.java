package com.novaagritech.agriclinic.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivitySingleNewsBinding;
import com.novaagritech.agriclinic.modals.Home;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SingleNewsActivity extends AppCompatActivity {

    String TAG="Articles";
    private ProgressDialog pDialog;

    private List<Info> infoList;
    private DisplayImageOptions options;

    ActivitySingleNewsBinding binding;
    private  String news_id;


    String str ="https://agriclinic.org/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(SingleNewsActivity.this,R.layout.activity_single_news);

        pDialog=new ProgressDialog(this);

        setSupportActionBar(binding.toolbar);

        binding.toolbar.inflateMenu(R.menu.share);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available)
                .showImageForEmptyUri(R.drawable.image_not_available)
                .showImageOnFail(R.drawable.image_not_available)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(1))
                .build();
        Intent i = getIntent();
        news_id = i.getStringExtra("news_id");

        String title = i.getStringExtra("title");

        //binding.toolbarTitle.setText(title);

        binding.actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        getNews();
        binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            infoList = new ArrayList<Info>();

            binding.mSwipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        getNews();
                    }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);


    }


    public void getNews(){

        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("news_id", news_id);


        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Home> call = service.processNewsDetails(jsonObject);
        call.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NonNull Call<Home> call, @NonNull retrofit2.Response<Home> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Home articlesData = response.body();
                    infoList = response.body().getResponse();

                    if (articlesData.isStatus()) {
                        if (infoList != null && infoList.size() > 0) {
                            for (int j = 0; j < infoList.size(); j++) {
                                Log.d(TAG, "" + infoList.size());


                                binding.textDate.setText("Published on : " + ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, infoList.get(0).getCreated_on()));
                                binding.textTitle1.setText(infoList.get(0).getTitle());

                              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription(), Html.FROM_HTML_MODE_COMPACT));

                                } else {
                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription()));

                                }*/

                                //Font must be placed in assets/fonts folder
                                String text = "<html><style type='text/css'>@font-face { font-family: Mandali-Regular; src: url('fonts/Mandali-Regular.ttf'); } body p {font-family: Mandali-Regular;}</style>"
                                        + "<body >" + "<p align=\"justify\" style=\"font-size: 24px; font-family: Mandali-Regular;\">" + infoList.get(0).getDescription() + "</p> "+ "</body></html>";

                                binding.textDesc.loadDataWithBaseURL("file:///android_asset/",text,"text/html","utf-8",null);

                               // binding.textDesc.loadDataWithBaseURL(null, infoList.get(0).getDescription(), "text/html; charset=utf-8", "UTF-8",null);

                                ImageLoader.getInstance()
                                        .displayImage(infoList.get(0).getImage(), binding.textImage, options,new SimpleImageLoadingListener(){
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                binding.progressBar.setVisibility(View.VISIBLE);
                                            }
                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onLoadingCancelled(String imageUri, View view) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }
                                        });


                                Log.d(TAG,""+Urls.IMAGE_URL1+ infoList.get(0).getSource_logo());


                                ImageLoader.getInstance()
                                        .displayImage(Urls.IMAGE_URL1+ infoList.get(0).getSource_logo(), binding.textSource, options,new SimpleImageLoadingListener(){
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                binding.progressBar.setVisibility(View.VISIBLE);
                                            }
                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onLoadingCancelled(String imageUri, View view) {
                                                binding.progressBar.setVisibility(View.GONE);

                                            }
                                        });



                                binding.mSwipeRefreshLayout.setRefreshing(false);

                                pDialog.dismiss();
                            }
                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<Home> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share, menu);


        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            // do your code
            if (ConstantValues.IS_USER_LOGGED_IN) {
                try {



                  
                    // shorten the link
                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance ( ).createDynamicLink ( )
                            .setLink ( Uri.parse ( "https://agriclinic.org/viewcontent.php?id=" + infoList.get ( 0 ).getId ( )+"news" ) )// manually
                            .setDomainUriPrefix ( "https://agcl.in/a" )
                            .setAndroidParameters ( new DynamicLink.AndroidParameters.Builder ( )
                                    .build ( ) )
                            .setSocialMetaTagParameters ( new DynamicLink.SocialMetaTagParameters.Builder ( )
                                    .setTitle ( infoList.get ( 0 ).getTitle ( ) )
                                    .setImageUrl(Uri.parse(infoList.get(0).getImage()))
                                    .setDescription ( getResources ( ).getString ( R.string.access_farmrise_articles1 ) )
                                    .build ( ) )
                            .buildShortDynamicLink ( ShortDynamicLink.Suffix.SHORT )
                            .addOnCompleteListener ( this, (OnCompleteListener<ShortDynamicLink>) task -> {
                                if (task.isSuccessful ( )) {
                                    // Short link created
                                    Uri shortLink = task.getResult ( ).getShortLink ( );
                                    Uri flowchartLink = task.getResult ( ).getPreviewLink ( );
                                    Log.e ( "main ", "substring1 " + shortLink );
                                    Log.e ( "main ", "substring1 " + flowchartLink );
                                    Intent intent = new Intent ( );
                                    intent.setAction ( Intent.ACTION_SEND );
                                    intent.putExtra ( Intent.EXTRA_TEXT, shortLink.toString () );
                                    intent.setType ( "text/plain" );
                                    startActivity ( intent );


                                } else {
                                    // Error
                                    // ...
                                    Log.e ( "main", " error " + task.getException ( ) );

                                }
                            } );

                    Log.e ( "main ", "short link " + shortLinkTask );

                } catch (Exception e) {
                    e.printStackTrace ( );

                }

            }


            return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
