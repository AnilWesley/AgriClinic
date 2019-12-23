package com.novaagritech.agriclinic.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.CustomButton;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;
import com.novaagritech.agriclinic.databinding.ActivitySingleArticleBinding;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleArticleActivity extends AppCompatActivity  {


    String TAG="Articles";
    private ProgressDialog pDialog;

    private List<InfoData> articlesDetails;

    private DisplayImageOptions options;

    ActivitySingleArticleBinding binding;
    private  String article_id ,user_id;

    String str ="http://agriclinic.org/";
    MyAppPrefsManager myAppPrefsManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil. setContentView(SingleArticleActivity.this,R.layout.activity_single_article);

        pDialog=new ProgressDialog(this);
        myAppPrefsManager=new MyAppPrefsManager(SingleArticleActivity.this);
        user_id=myAppPrefsManager.getUserId();

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
        article_id = i.getStringExtra("article_id");


        binding.actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        getArticle();




    }

    public void getArticle(){

        pDialog.setMessage("Loading...");
        pDialog.show();
        pDialog.setCancelable(false);

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");
        jsonObject.addProperty("article_id", article_id);
        jsonObject.addProperty("user_id", user_id);


        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ArticlesList> call = service.processArticlesDetails(jsonObject);
        call.enqueue(new Callback<ArticlesList>() {
            @SuppressLint("SetTextI18n")
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
                            for (int j = 0; j < articlesDetails.size(); j++) {
                                Log.d(TAG, "" + articlesDetails.size());


                                binding.textAuthor.setText(articlesDetails.get(0).getAuthor_info());
                                //binding.textTags.setText(articleModalList.get(0).getTags());
                                String line=articlesDetails.get(0).getTags();

                                //using String split function
                                String[] words = line.split(",");
                                Log.d(TAG,""+Arrays.toString(words));

                                Log.d(TAG,""+words.length);

                                final CustomButton[] myTextViews = new CustomButton[words.length]; // create an empty array;


                                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        55);
                                layoutParams.setMargins(5, 5, 5, 5);


                                for (int i = 0; i < words.length; i++) {
                                    // create a new textview
                                    final CustomButton rowTextView = new CustomButton(SingleArticleActivity.this);

                                    // set some properties of rowTextView or something
                                    rowTextView.setText(words[i]+" ");
                                    rowTextView.setTextColor(Color.parseColor("#FFFFFF"));
                                    rowTextView.setBackgroundResource(R.drawable.button_rounded);
                                    rowTextView.setPadding(5,5,5,5);

                                    rowTextView.setLayoutParams(layoutParams);
                                    // add the textview to the linearlayout
                                    binding.myLinearLayout.addView(rowTextView);

                                    // save a reference to the textview for later
                                    myTextViews[i] = rowTextView;

                                    int finalI = i;
                                    rowTextView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(SingleArticleActivity.this, ""+ words[finalI], Toast.LENGTH_SHORT).show();

                                            Intent setIntent = new Intent(SingleArticleActivity.this, ArticlesListActivity.class);
                                            setIntent.putExtra("article_tag", ""+words[finalI]);
                                            Log.d(TAG,""+words[finalI]);
                                            setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(setIntent);
                                        }
                                    });
                                }


                                binding.textDate.setText("Published on : " + ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT,articlesDetails.get(0).getCreated_on()));

                              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    text_desc.setText(Html.fromHtml(articleModalList.get(3).getDescription(), Html.FROM_HTML_MODE_COMPACT));

                                } else {
                                    text_desc.setText(Html.fromHtml(articleModalList.get(3).getDescription()));

                                }*/

                                binding.textDesc.loadDataWithBaseURL(null,articlesDetails.get(0).getDescription(), "text/html; charset=utf-8", "UTF-8",null);

                                ImageLoader.getInstance()
                                        .displayImage(Urls.IMAGE_URL+articlesDetails.get(0).getImage_path(), binding.textImage, options);


                                pDialog.dismiss();
                            }
                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {
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


            //ConstantValues.shareDeepLink(SingleArticleActivity.this, String.format(getResources().getString(R.string.access_farmrise_articles), str));

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {
                try {


                           /* DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                                    .setLink(Uri.parse("http://agriclinic.org/"))
                                    .setDomainUriPrefix("novaagritech1.page.link")
                                    // Open links with this app on Android
                                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                    // Open links with com.example.ios on iOS
                                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                                    .buildDynamicLink();

                            Uri dynamicLinkUri = dynamicLink.getUri();
                            Log.d("dynamicLinkUri",""+articleModal.getTitle());*/


                    String sharelinktext  = "https://novaagritech1.page.link/?"+
                            "link=http://agriclinic.org/viewcontent.php?id="+articlesDetails.get(0).getId() +
                            "&apn="+ getPackageName();


                    // shorten the link
                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLink(Uri.parse(sharelinktext))// manually
                            .setDomainUriPrefix("https://novaagritech1.page.link")
                            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                                             .setMinimumVersion(0)
                                             .build())
                            .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                                            .setTitle(articlesDetails.get(0).getTitle())
                                            .setDescription(getResources().getString(R.string.access_farmrise_articles1))
                                            .build())
                            .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                            .addOnCompleteListener(this , (OnCompleteListener<ShortDynamicLink>) task -> {
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();
                                    Log.e("main ", "short link "+ shortLink);
                                    Log.e("main ", "short link "+ flowchartLink);


                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);

                                    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                                    intent.setType("text/plain");
                                    startActivity(intent);


                                } else {
                                    // Error
                                    // ...
                                    Log.e("main", " error "+task.getException() );

                                }
                            });

                    Log.e("main ", "short link "+ shortLinkTask);

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }


            return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
