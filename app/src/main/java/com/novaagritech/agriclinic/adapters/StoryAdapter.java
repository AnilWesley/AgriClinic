package com.novaagritech.agriclinic.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.HomeActivity1;
import com.novaagritech.agriclinic.activities.StoryActivity;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.Stories;
import com.novaagritech.agriclinic.modals.Stories1;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Stories1> mStory;
    private DisplayImageOptions options;
    private static final String TAG = "STORYADAPTER1";
    private MyAppPrefsManager myAppPrefsManager;
    public StoryAdapter(Context mContext, List<Stories1> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, viewGroup, false);
            return new StoryAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Stories1 story = mStory.get(i);


        //userInfo(viewHolder, story.getUser_id(), i);

       //seenStory(viewHolder,story.isViewed_status());

        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+ story.getImage_url(), viewHolder.story_photo, options);

        //Glide.with(mContext).load(Urls.IMAGE_URL+ story.getImage_path()).into(viewHolder.story_photo);

        if (i!=0){
            //Glide.with(mContext).load(Urls.IMAGE_URL+ story.getImage_path()).into(viewHolder.story_photo_seen);
            ImageLoader.getInstance()
                    .displayImage(Urls.IMAGE_URL1+ story.getImage_url(), viewHolder.story_photo_seen, options);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    // TODO: go to story


                myAppPrefsManager=new MyAppPrefsManager(mContext);
                if (ConstantValues.IS_USER_LOGGED_IN) {
                    // prepare call in Retrofit 2.0
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("user_id", myAppPrefsManager.getUserId());
                    jsonObject.addProperty("story_id", story.getStory_id());

                    ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                    Call<Stories> call = service.processStoriesViews(jsonObject);
                    call.enqueue(new Callback<Stories>() {
                        @Override
                        public void onResponse(@NonNull Call<Stories> call, @NonNull Response<Stories> response) {

                            // Check if the Response is successful
                            if (response.isSuccessful()){
                                // Check the Success status
                                if (response.body().isStatus()) {
                                    //story.setViewed_status(true);
                                    Log.d(TAG,""+response.body().getCount());

                                    Intent intent = new Intent(mContext, StoryActivity.class);
                                    intent.putExtra("user_id", story.getUser_id());
                                    intent.putExtra("response", ""+response.body().getCount());
                                    mContext.startActivity(intent);

                                }

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Stories> call, @NonNull Throwable t) {

                            Log.d("ResponseF",""+t);
                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo, story_photo_seen;
        public VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            videoView = itemView.findViewById(R.id.videoView);

            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }


    private void userInfo(final ViewHolder viewHolder, String userid, final int pos){
        final Stories1 story = mStory.get(pos);
        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+ story.getImage_url(), viewHolder.story_photo, options);
        if (pos != 0) {
            ImageLoader.getInstance()
                    .displayImage(Urls.IMAGE_URL1+ story.getImage_url(), viewHolder.story_photo_seen, options);
        }

    }



    private void seenStory(final ViewHolder viewHolder, boolean isViewed_status){

        if (isViewed_status){
            viewHolder.story_photo.setVisibility(View.GONE);
            viewHolder.story_photo_seen.setVisibility(View.VISIBLE);

        } else{
            viewHolder.story_photo.setVisibility(View.VISIBLE);
            viewHolder.story_photo_seen.setVisibility(View.GONE);
        }
    }



}
