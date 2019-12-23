package com.novaagritech.agriclinic.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.adapters.YoutubeVideoAdapter;
import com.novaagritech.agriclinic.interfaces.OnLoadMoreListener;
import com.novaagritech.agriclinic.constants.RecyclerItemClickListener;
import com.novaagritech.agriclinic.utilities.Config;

import com.novaagritech.agriclinic.modals.YoutubeVideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VideoMoreActivity extends YouTubeBaseActivity
        implements  YouTubePlayer.OnInitializedListener  {
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    private RecyclerView recyclerView;
    private static final String TAG = "YOUTUBE1";
    int mVideoType;
    String videoid,videoid1,videotitle,videourl,videoTime;
    String url;
    List<YoutubeVideoModel> youtubeVideoModelArrayList;
    DrawerLayout drawer;
    Toolbar toolbar;
    YoutubeVideoAdapter mAdapter;


    SimpleDateFormat inputFormat;
    String niceDateStr;
    ProgressDialog pDialog;
    ImageView action_image;
    protected Handler handler;
    private LinearLayoutManager mLayoutManager;
    int index ;
    String mNextPageToken="";
    String channelID;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_more);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        action_image = (ImageView) findViewById(R.id.action_image);
        action_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        pDialog = new ProgressDialog(VideoMoreActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        handler = new Handler();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();


        recyclerView = findViewById(R.id.recycler_view);
        Intent intent =getIntent();
        channelID=intent.getStringExtra("channelID");
        Log.d("channelID",""+channelID);

        youtubeVideoModelArrayList = new ArrayList<>();
        data(channelID);




    }

    public void data(String channelID){


        if (this.mVideoType == 2) {
            url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,id&fields=nextPageToken,pageInfo(totalResults),items(snippet(title,thumbnails,publishedAt,resourceId(videoId)))&key=" +Config.YOUTUBE_API_KEY + "&" + Config.PARAM_PLAYLIST_ID_YOUTUBE + channelID + "&" + Config.PARAM_PAGE_TOKEN_YOUTUBE + this.mNextPageToken + "&" + Config.PARAM_MAX_RESULT_YOUTUBE + 50;
        } else {
            url = "https://www.googleapis.com/youtube/v3/search?part=snippet,id&order=date&type=video&fields=nextPageToken,pageInfo(totalResults),items(id(videoId),snippet(title,thumbnails,publishedAt))&key=" +Config.YOUTUBE_API_KEY + "&" + Config.PARAM_CHANNEL_ID_YOUTUBE + channelID+ "&" + Config.PARAM_PAGE_TOKEN_YOUTUBE + this.mNextPageToken + "&" + Config.PARAM_MAX_RESULT_YOUTUBE + 50;
        }


       // url = "https://www.googleapis.com/youtube/v3/search?part=snippet,id&order=date&type=video&fields=nextPageToken,pageInfo(totalResults),items(id(videoId),snippet(title,thumbnails,publishedAt))&key=" + Config.YOUTUBE_API_KEY + "&" + Config.PARAM_CHANNEL_ID_YOUTUBE + "UCicsA32B7SO_RUJqzDKbKNA" + "&" + Config.PARAM_PAGE_TOKEN_YOUTUBE + mNextPageToken + "&" + Config.PARAM_MAX_RESULT_YOUTUBE + 50;


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("URL",""+ url);
                try {
                    youtubeVideoModelArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    mNextPageToken=jsonObject.getString("nextPageToken");
                    Log.e("TOKEN1",""+mNextPageToken);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < 5; i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectthumbnail = jsonsnippet.getJSONObject("thumbnails");

                        JSONObject jsonObjectdefault = jsonObjectthumbnail.getJSONObject("default");
                        videoid = jsonVideoId.getString("videoId");
                        videotitle = jsonsnippet.getString("title");
                        videourl = jsonObjectdefault.getString("url");
                        videoTime = jsonsnippet.getString("publishedAt");

                        try {
                            Date date = inputFormat.parse(videoTime);
                            niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        youtubeVideoModelArrayList.add(new YoutubeVideoModel(videoid, videotitle, videourl, niceDateStr));



                    }

                    recyclerView.setHasFixedSize(true);

                    mLayoutManager = new LinearLayoutManager(VideoMoreActivity.this);

                    // use a linear layout manager
                    recyclerView.setLayoutManager(mLayoutManager);

                    // create an Object for Adapter

                    mAdapter = new YoutubeVideoAdapter(VideoMoreActivity.this, youtubeVideoModelArrayList,recyclerView);

                    recyclerView.setAdapter(mAdapter);


                    mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            //add null , so the mAdapter will check view_type and show progress bar at bottom
                            youtubeVideoModelArrayList.add(null);
                            mAdapter.notifyItemInserted(youtubeVideoModelArrayList.size() - 1);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //   remove progress item
                                    youtubeVideoModelArrayList.remove(youtubeVideoModelArrayList.size() - 1);
                                    mAdapter.notifyItemRemoved(youtubeVideoModelArrayList.size());
                                    //add items one by one

                                    int start = youtubeVideoModelArrayList.size();

                                    int end = start + 7;

                                    for (int i = start + 1; i <= end; i++) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            mNextPageToken=jsonObject.getString("nextPageToken");
                                            Log.e(TAG,""+mNextPageToken);
                                            JSONArray jsonArray = jsonObject.getJSONArray("items");

                                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                            JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                                            JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                                            JSONObject jsonObjectthumbnail = jsonsnippet.getJSONObject("thumbnails");

                                            JSONObject jsonObjectdefault = jsonObjectthumbnail.getJSONObject("default");
                                            videoid = jsonVideoId.getString("videoId");
                                            videotitle = jsonsnippet.getString("title");
                                            videourl = jsonObjectdefault.getString("url");
                                            videoTime = jsonsnippet.getString("publishedAt");
                                            try {
                                                Date date = inputFormat.parse(videoTime);
                                                niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                            youtubeVideoModelArrayList.add(new YoutubeVideoModel(videoid, videotitle, videourl, niceDateStr));

                                            mAdapter.notifyItemInserted(youtubeVideoModelArrayList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }


                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setLoaded();



                                }


                            }, 1000);

                        }
                    });

                    if (youtubeVideoModelArrayList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);


                    } else {
                        recyclerView.setVisibility(View.VISIBLE);

                    }


                    mAdapter.notifyDataSetChanged();
                    videoid1 = youtubeVideoModelArrayList.get(0).getVideoId();
                    Log.e("VideoId",videoid1);
                    player.cueVideo(videoid1);
                    pDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

        //set click event
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                player.loadVideo(youtubeVideoModelArrayList.get(position).getVideoId());
                TextView picture=(TextView) view.findViewById(R.id.video_title_label);
               /* index= position;
                if (index==position){
                    picture.setTextColor(Color.parseColor("#5CC615"));

                }else {
                    picture.setTextColor(Color.parseColor("#000000"));

                }*/





            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        Log.d(TAG,""+videoid1);
        if (wasRestored) {
            player.loadVideo(videoid1);
        }else {
            player.loadVideo("SpTpuIWZEoo");
        }

        pDialog.dismiss();

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }



    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            //showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            // showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            //showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }




}


