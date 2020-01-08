package com.novaagritech.agriclinic.activities;

import android.annotation.SuppressLint;
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
import com.novaagritech.agriclinic.constants.CustomButton;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.databinding.ActivitySingleArticleBinding;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleArticleActivity extends AppCompatActivity {


    String TAG = "Articles";
    private ProgressDialog pDialog;

    private List<Info> articlesDetails;

    private DisplayImageOptions options;

    ActivitySingleArticleBinding binding;
    private String article_id, user_id;

    String str = "https://agriclinic.org/";
    MyAppPrefsManager myAppPrefsManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = DataBindingUtil.setContentView ( SingleArticleActivity.this, R.layout.activity_single_article );

        pDialog = new ProgressDialog ( this );
        myAppPrefsManager = new MyAppPrefsManager ( SingleArticleActivity.this );
        user_id = myAppPrefsManager.getUserId ( );

        setSupportActionBar ( binding.toolbar );

        binding.toolbar.inflateMenu ( R.menu.share );
        options = new DisplayImageOptions.Builder ( )
                .showImageOnLoading ( R.drawable.image_not_available )
                .showImageForEmptyUri ( R.drawable.image_not_available )
                .showImageOnFail ( R.drawable.image_not_available )
                .cacheInMemory ( true )
                .cacheOnDisk ( true )
                .considerExifParams ( true )
                .bitmapConfig ( Bitmap.Config.RGB_565 )
                .displayer ( new RoundedBitmapDisplayer ( 1 ) )
                .build ( );
        Intent i = getIntent ( );

        article_id = i.getStringExtra ( "article_id" );


        binding.actionImage.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                onBackPressed ( );


            }
        } );

       /* binding.mSwipeRefreshLayout.setOnRefreshListener(() -> {

            articlesDetails = new ArrayList<Info>();


            binding.mSwipeRefreshLayout.post(() -> {
                        //mSwipeLayout = true;

                        binding.mSwipeRefreshLayout.setRefreshing(true);
                        getArticle();
                    }
            );

        });
        binding.mSwipeRefreshLayout.setColorSchemeResources(R.color.green,R.color.red,R.color.blue);*/


        getArticle();




    }

    public void getArticle() {


        pDialog.setMessage ( "Loading..." );
        pDialog.show ( );
        pDialog.setCancelable ( false );

        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject ( );

        jsonObject.addProperty ( "language_id", "2" );
        jsonObject.addProperty ( "article_id", article_id );
        jsonObject.addProperty ( "user_id", user_id );


        Log.d ( TAG, "" + jsonObject );
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance ( ).create ( ApiInterface.class );
        Call<Articles> call = service.processArticlesDetails ( jsonObject );
        call.enqueue ( new Callback<Articles> ( ) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {


                // Check if the Response is successful
                if (response.isSuccessful ( )) {
                    Log.d ( TAG, "" + response.toString ( ) );
                    assert response.body ( ) != null;
                    Articles articlesData = response.body ( );
                    articlesDetails = response.body ( ).getResponse ( );

                    if (articlesData.isStatus ( )) {
                        if (articlesDetails != null && articlesDetails.size ( ) > 0) {
                            for (int j = 0; j < articlesDetails.size ( ); j++) {
                                Log.d ( TAG, "" + articlesDetails.size ( ) );


                                binding.textAuthor.setText ( articlesDetails.get ( 0 ).getAuthor_info ( ) );
                                binding.textTitle.setText(articlesDetails.get(0).getTitle());
                                String line = articlesDetails.get ( 0 ).getTags ( );

                                //using String split function
                                String[] words = line.split ( "," );


                                Log.d ( TAG, "" + Arrays.toString ( words ) );

                                Log.d ( TAG, "" + words.length );

                                final CustomButton[] myTextViews = new CustomButton[words.length]; // create an empty array;


                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        75 );
                                layoutParams.setMargins ( 5, 5, 5, 5 );


                                for (int i = 0; i < words.length; i++) {
                                    // create a new textview
                                    final CustomButton rowTextView = new CustomButton ( SingleArticleActivity.this );


                                    // set some properties of rowTextView or something
                                    rowTextView.setText ( words[i] + " " );
                                    rowTextView.setTextColor ( Color.parseColor ( "#FFFFFF" ) );
                                    rowTextView.setBackgroundResource ( R.drawable.button_rounded );
                                    rowTextView.setPadding ( 5, 5, 5, 5 );

                                    rowTextView.setLayoutParams ( layoutParams );
                                    // add the textview to the linearlayout
                                    binding.myLinearLayout.addView ( rowTextView );

                                    // save a reference to the textview for later
                                    myTextViews[i] = rowTextView;


                                    int finalI = i;
                                    rowTextView.setOnClickListener ( new View.OnClickListener ( ) {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(SingleArticleActivity.this, ""+ words[finalI], Toast.LENGTH_SHORT).show();

                                            Intent setIntent = new Intent ( SingleArticleActivity.this, ArticlesListActivity.class );
                                            setIntent.putExtra ( "article_tag", "" + words[finalI] );
                                            Log.d ( TAG, "" + words[finalI] );
                                            setIntent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                            startActivity ( setIntent );
                                        }
                                    } );
                                }


                                binding.textDate.setText ( "Published on : " + ConstantValues.getFormattedDate ( MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, articlesDetails.get ( 0 ).getCreated_on ( ) ) );


                                binding.textDesc.loadDataWithBaseURL ( null, articlesDetails.get ( 0 ).getDescription ( ) + articlesDetails.get ( 0 ).getDescription2 ( ) + articlesDetails.get ( 0 ).getDescription3 ( ), "text/html; charset=utf-8", "UTF-8", null );

                                ImageLoader.getInstance ( )
                                        .displayImage ( Urls.IMAGE_URL + articlesDetails.get ( 0 ).getImage_path ( ), binding.textImage, options, new SimpleImageLoadingListener ( ) {
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                binding.progressBar.setVisibility ( View.VISIBLE );
                                            }

                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                                binding.progressBar.setVisibility ( View.GONE );

                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                binding.progressBar.setVisibility ( View.GONE );

                                            }

                                            @Override
                                            public void onLoadingCancelled(String imageUri, View view) {
                                                binding.progressBar.setVisibility ( View.GONE );

                                            }
                                        } );

                                //binding.mSwipeRefreshLayout.setRefreshing(false);
                                pDialog.dismiss ( );
                            }
                            //get values

                        }

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {
                pDialog.dismiss ( );
                Log.d ( "ResponseF", "" + t );
            }
        } );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ( );
        inflater.inflate ( R.menu.share, menu );


        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId ( ) == R.id.share) {
            // do your code

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {
                try {


                    // shorten the link
                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance ( ).createDynamicLink ( )
                            .setLink ( Uri.parse ( "https://agriclinic.org/viewcontent.php?id=" + articlesDetails.get ( 0 ).getId ( )+ "articles" ) )// manually
                            .setDomainUriPrefix ( "https://agcl.in/a")
                            .setAndroidParameters ( new DynamicLink.AndroidParameters.Builder ( )
                                    .build ( ) )
                            .setSocialMetaTagParameters ( new DynamicLink.SocialMetaTagParameters.Builder ( )
                                    .setTitle ( articlesDetails.get ( 0 ).getTitle ( ) )
                                    .setImageUrl(Uri.parse(Urls.IMAGE_URL + articlesDetails.get ( 0 ).getImage_path ( )))

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

                                    intent.putExtra ( Intent.EXTRA_TEXT, shortLink.toString ( ) );
                                    intent.putExtra("title","articles");
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
        return super.onOptionsItemSelected ( item );
    }


}
