package com.novaagritech.agriclinic.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.SchemesData;
import com.novaagritech.agriclinic.modals.Stories;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image, story_photo;



    //
    LinearLayout r_seen;
    TextView seen_number;

    String TAG="STORYACTIVITY1";

    //
    private  String user_id,count;

    List<Stories.StoriesDetails> images;
    List<String> storyids;
    private DisplayImageOptions options;
    private int is_video;
    //SimpleExoPlayerView exoPlayerView;
    //SimpleExoPlayer exoPlayer;


    String videoURL = "http://novaagri.in/agriclinic/storage/stories/204321894.mp4";
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story1);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        //
        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        //exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exo_player_view);
        r_seen.setVisibility(View.VISIBLE);

        //
        Intent i = getIntent();

        user_id = i.getStringExtra("user_id");
        count = i.getStringExtra("response");
       // seenNumber(user_id);
        seen_number.setText(count);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available)
                .showImageForEmptyUri(R.drawable.image_not_available)
                .showImageOnFail(R.drawable.image_not_available)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();




        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);


        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);





        getNews();




    }

    @Override
    public void onNext() {
        Log.d(TAG,""+"next");


        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+ images.get(++counter).getImage_url(), image, options);
      /*  if (images.get(counter).getIs_vedio()==1) {

            try {
                image.setVisibility(View.GONE);
                exoPlayerView.setVisibility(View.VISIBLE);

                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = ExoPlayerFactory.newSimpleInstance(StoryActivity.this, trackSelector);
                Uri videoURI = Uri.parse(Urls.IMAGE_URL1 + images.get(++counter).getVedio_url());

                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

                exoPlayerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }catch (Exception e){
                Log.e("MainAcvtivity"," exoplayer error "+ e.toString());
            }


        }else if(images.get(counter).getIs_vedio()==0) {
            image.setVisibility(View.VISIBLE);
            //exoPlayerView.setVisibility(View.GONE);
            ImageLoader.getInstance()
                    .displayImage(Urls.IMAGE_URL1+ images.get(++counter).getImage_url(), image, options);
            Toast.makeText(this, "No Video", Toast.LENGTH_SHORT).show();
        }*/
      //  addView(storyids.get(counter));
        //seenNumber(storyids.get(counter));

    }

    @Override
    public void onPrev() {

        Log.d(TAG,""+"previous");

        if ((counter - 1) < 0)
            return;
        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+ images.get(--counter).getImage_url(), image, options);

      /*  if (images.get(counter).getIs_vedio()==1) {

            try {
                exoPlayerView.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);

                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = ExoPlayerFactory.newSimpleInstance(StoryActivity.this, trackSelector);
                Uri videoURI = Uri.parse(Urls.IMAGE_URL1+ images.get(--counter).getVedio_url());
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                exoPlayerView.setVisibility(View.VISIBLE);
                exoPlayerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }catch (Exception e){
                Log.e("MainAcvtivity"," exoplayer error "+ e.toString());
            }

        }
        else if(images.get(counter).getIs_vedio()==0) {
            exoPlayerView.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
              ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+ images.get(--counter).getImage_url(), image, options);
            Toast.makeText(this, "No Video", Toast.LENGTH_SHORT).show();
        }*/
    }

        //
       // seenNumber(storyids.get(counter));
        //


    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }


    public void getNews(){



        // prepare call in Retrofit 2.0
        // prepare call in Retrofit 2.0
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("language_id", "2");

        jsonObject.addProperty("user_id", user_id);



        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Stories> call = service.processUserStories(jsonObject);
        call.enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {


                // Check if the Response is successful
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Stories articlesData = response.body();


                     images=response.body().getResponse();



                    if (articlesData.isStatus()) {

                        if (images != null && images.size() > 0) {
                            for (int j = 0; j < images.size(); j++) {
                                Log.d(TAG,""+images.get(counter).getStory_id());

                            }
                            //storyids.add(images.get(counter).getId());

                            //get values
                            storiesProgressView.setStoriesCount(images.size());
                            storiesProgressView.setStoryDuration(20000L);
                            storiesProgressView.setStoriesListener(StoryActivity.this);
                            storiesProgressView.startStories(counter);

                            ImageLoader.getInstance()
                                    .displayImage(Urls.IMAGE_URL1+ images.get(counter).getImage_url(), image, options);


                           /* if (images.get(counter).getIs_vedio()==1) {


                                try {
                                    image.setVisibility(View.GONE);
                                    exoPlayerView.setVisibility(View.VISIBLE);

                                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                                    exoPlayer = ExoPlayerFactory.newSimpleInstance(StoryActivity.this, trackSelector);

                                    Uri videoURI = Uri.parse(Urls.IMAGE_URL1 + images.get(counter).getVedio_url());
                                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                    MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

                                    exoPlayerView.setPlayer(exoPlayer);
                                    exoPlayer.prepare(mediaSource);
                                    exoPlayer.setPlayWhenReady(true);
                                }catch (Exception e){
                                    Log.e("MainAcvtivity"," exoplayer error "+ e.toString());
                                }

                            }
                            else if(images.get(counter).getIs_vedio()==0){
                                image.setVisibility(View.VISIBLE);
                                exoPlayerView.setVisibility(View.GONE);
                                Toast.makeText(StoryActivity.this, "No Video", Toast.LENGTH_SHORT).show();
                                ImageLoader.getInstance()
                                        .displayImage(Urls.IMAGE_URL1+ images.get(counter).getImage_url(), image, options);
                            }*/

                          //  addView(storyids.get(counter));


                        }


                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<Stories> call, @NonNull Throwable t) {
                Log.d("ResponseF",""+t);
            }
        });

    }



    private void addView(String storyid){

    }

    private void seenNumber(String storyid){

        seen_number.setText(count);


    }
}
