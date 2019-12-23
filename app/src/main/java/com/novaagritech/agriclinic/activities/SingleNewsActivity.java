package com.novaagritech.agriclinic.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivitySingleNewsBinding;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.SchemesData;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SingleNewsActivity extends AppCompatActivity {

    String TAG="Articles";
    private ProgressDialog pDialog;

    private List<InfoData> infoDataList;
    private DisplayImageOptions options;

    ActivitySingleNewsBinding binding;
    private  String news_id;
    private String scheme_id;

    String str ="http://agriclinic.org/";
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
        scheme_id = i.getStringExtra("scheme_id");
        String title = i.getStringExtra("title");

        binding.toolbarTitle.setText(title);

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getNews();
        getScheme();


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
        Call<SchemesData> call = service.processNewsDetails(jsonObject);
        call.enqueue(new Callback<SchemesData>() {
            @Override
            public void onResponse(@NonNull Call<SchemesData> call, @NonNull retrofit2.Response<SchemesData> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    SchemesData articlesData = response.body();
                    infoDataList = response.body().getResponse();

                    if (articlesData.isStatus()) {
                        if (infoDataList != null && infoDataList.size() > 0) {
                            for (int j = 0; j < infoDataList.size(); j++) {
                                Log.d(TAG, "" + infoDataList.size());


                                binding.textDate.setText("Published on : " + ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT,infoDataList.get(0).getCreated_on()));
                                binding.textTitle.setText(infoDataList.get(0).getTitle());

                              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription(), Html.FROM_HTML_MODE_COMPACT));

                                } else {
                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription()));

                                }*/

                                binding.textDesc.loadDataWithBaseURL(null,infoDataList.get(0).getDescription(), "text/html; charset=utf-8", "UTF-8",null);

                                ImageLoader.getInstance()
                                        .displayImage(infoDataList.get(0).getImage(), binding.textImage, options);




                                pDialog.dismiss();
                            }
                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<SchemesData> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Log.d("ResponseF",""+t);
            }
        });

    }


    public void getScheme(){

        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("govtscheme_id", scheme_id);


        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<SchemesData> call = service.processGovtSchemeDetails(jsonObject);
        call.enqueue(new Callback<SchemesData>() {
            @Override
            public void onResponse(@NonNull Call<SchemesData> call, @NonNull retrofit2.Response<SchemesData> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    SchemesData articlesData = response.body();
                    infoDataList = response.body().getResponse();

                    if (articlesData.isStatus()) {
                        if (infoDataList != null && infoDataList.size() > 0) {
                            for (int j = 0; j < infoDataList.size(); j++) {
                                Log.d(TAG, "" + infoDataList.size());


                                binding.textDate.setText("Published on : " + ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT,infoDataList.get(0).getCreated_on()));
                                binding.textTitle.setText(infoDataList.get(0).getTitle());

                              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription(), Html.FROM_HTML_MODE_COMPACT));

                                } else {
                                    text_desc.setText(Html.fromHtml(newsModalList.get(3).getDescription()));

                                }*/

                                binding.textDesc.loadDataWithBaseURL(null,infoDataList.get(0).getDescription(), "text/html; charset=utf-8", "UTF-8",null);

                                ImageLoader.getInstance()
                                        .displayImage(infoDataList.get(0).getImage(), binding.textImage, options);




                                pDialog.dismiss();
                            }
                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<SchemesData> call, @NonNull Throwable t) {
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


           // ConstantValues.shareDeepLink(SingleNewsActivity.this, String.format(getResources().getString(R.string.access_farmrise_articles), str));

            String sharelinktext  = "https://novaagritech.page.link/?"+
                    "link=http://agriclinic.org/viewcontent.php?id="+infoDataList.get(0).getId() +
                    "&apn="+ getPackageName()+
                    "&st="+getResources().getString(R.string.access_farmrise_articles1)+
                    "&sd="+infoDataList.get(0).getTitle()+
                    "&si="+infoDataList.get(0).getImage();


            // shorten the link
            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink((Uri.parse(sharelinktext)))  // manually
                    .buildShortDynamicLink()
                    .addOnCompleteListener((OnCompleteListener<ShortDynamicLink>) task -> {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink);


                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);


                        } else {
                            // Error
                            // ...
                            Log.e("main", " error "+task.getException() );

                        }
                    });

            Log.e("main ", "short link "+ shortLinkTask);


            return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
