package com.novaagritech.agriclinic.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.novaagritech.agriclinic.activities.SingleArticleActivity;
import com.novaagritech.agriclinic.activities.ChatActivity;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.Articles;
import com.novaagritech.agriclinic.modals.Info;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


     Context context;

    ArticlesListAdapter adapter;

    private DisplayImageOptions options;

    private String TAG="CONTEXT";

    private String user_id;


    private List<Info> infoList;


    private MyAppPrefsManager myAppPrefsManager;

    private int likes_count;




    public ArticlesListAdapter(Context context, List<Info> infoList) {
        this.infoList = infoList;
        this.context=context;
        this.adapter = this; //This is an important line, you need this line to keep track the adapter variable

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available)
                .showImageForEmptyUri(R.drawable.image_not_available)
                .showImageOnFail(R.drawable.image_not_available)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n,SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof MyViewHolder) {
            Info articleModal = (Info) infoList.get(position);
            myAppPrefsManager=new MyAppPrefsManager(context);
            user_id = myAppPrefsManager.getUserId();


            ((MyViewHolder) holder).tvName.setText(articleModal.getTitle());
            ((MyViewHolder) holder).articlelikes.setText(articleModal.getLikes_count() + " " + "likes");
            ((MyViewHolder) holder).articleviews.setText(articleModal.getViews_count() + " " + "views");
            ((MyViewHolder) holder).articleCommentCount.setText("View All " + articleModal.getComment_count() + " Comments");

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = null;
            try {
                date = inputFormat.parse(articleModal.getCreated_on());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));

            ((MyViewHolder)holder).tvDate.setText(niceDateStr);

            //((MyViewHolder) holder).tvDate.setText(ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, articleModal.getCreated_on()));
            //((MyViewHolder) holder).tvDate.setText(articleModal.getCreated_on());

            ImageLoader.getInstance()
                    .displayImage(Urls.IMAGE_URL + articleModal.getImage_path(), ((MyViewHolder) holder).imageView, options,
                            new SimpleImageLoadingListener(){
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    ((MyViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);

                }
            });


            ((MyViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(context, SingleArticleActivity.class);
                    setIntent.putExtra("article_id", infoList.get(position).getId());
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(setIntent);
                }
            });


            // Check if the User has Liked the Product
            if (articleModal.getIs_liked() == 1) {
                ((MyViewHolder) holder).product_like_btn.setChecked(true);

            } else {
                ((MyViewHolder) holder).product_like_btn.setChecked(false);
            }

            // Check if the User has Liked the Product
            if (articleModal.getComment_count() == 0) {

                ((MyViewHolder) holder).articleCommentCount.setText("Add Comments");


            }
            // Handle Click event of product_like_btn Button
            ((MyViewHolder) holder).product_like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        // Check if the User has Checked the Like Button
                        if (((MyViewHolder) holder).product_like_btn.isChecked()) {
                            articleModal.setIs_liked(1);
                            ((MyViewHolder) holder).product_like_btn.setChecked(true);
                            // Request the Server to Like the Product for the User

                            // prepare call in Retrofit 2.0
                            JsonObject jsonObject = new JsonObject();

                            jsonObject.addProperty("user_id", user_id);
                            jsonObject.addProperty("article_id", articleModal.getId());
                            jsonObject.addProperty("is_liked", 1);

                            Log.d(TAG, "" + jsonObject);
                            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                            Call<Articles> call = service.processArticlesLikes(jsonObject);
                            call.enqueue(new Callback<Articles>() {
                                @Override
                                public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {

                                    // Check if the Response is successful
                                    if (response.isSuccessful()) {
                                        // Check the Success status
                                        if (response.body().getMessage().equalsIgnoreCase("Data Updated successfully!")) {

                                            // Product has been Disliked. Show the message to User
                                            likes_count = response.body().getLikes_count();
                                            Log.d(TAG, "Count : " + likes_count);
                                            //((MyViewHolder) holder).articlelikes.setText((likes_count)+" "+"likes");
                                            ((MyViewHolder) holder).articlelikes.setText(((infoList.get(position).getLikes_count()) + 1) + " " + "likes");
                                            infoList.get(position).setLikes_count(((infoList.get(position).getLikes_count()) + 1));
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {

                                    Log.d("ResponseF", "" + t);
                                }
                            });

                        } else {
                            articleModal.setIs_liked(0);
                            ((MyViewHolder) holder).product_like_btn.setChecked(false);

                            // Request the Server to Unlike the Product for the User
                            // prepare call in Retrofit 2.0
                            JsonObject jsonObject = new JsonObject();


                            jsonObject.addProperty("user_id", user_id);
                            jsonObject.addProperty("article_id", articleModal.getId());
                            jsonObject.addProperty("is_liked", 0);

                            Log.d(TAG, "" + jsonObject);
                            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                            Call<Articles> call = service.processArticlesUnLikes(jsonObject);
                            call.enqueue(new Callback<Articles>() {
                                @Override
                                public void onResponse(@NonNull Call<Articles> call, @NonNull Response<Articles> response) {

                                    // Check if the Response is successful
                                    if (response.isSuccessful()) {
                                        // Check the Success status
                                        if (response.body().getMessage().equalsIgnoreCase("Data Updated successfully!")) {

                                            // Product has been Disliked. Show the message to User
                                            likes_count = response.body().getLikes_count();
                                            Log.d(TAG, "Count : " + likes_count);
                                            // ((MyViewHolder) holder).articlelikes.setText((likes_count)+" "+"likes");
                                            ((MyViewHolder) holder).articlelikes.setText(((infoList.get(position).getLikes_count()) - 1) + " " + "likes");
                                            infoList.get(position).setLikes_count(((infoList.get(position).getLikes_count()) - 1));

                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Articles> call, @NonNull Throwable t) {

                                    Log.d("ResponseF", "" + t);
                                }
                            });


                        }


                    }
                }
            });

            ((MyViewHolder) holder).product_share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {
                        try {


                            // shorten the link
                            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance ( ).createDynamicLink ( )
                                    .setLink ( Uri.parse ( "https://agriclinic.org/viewcontent.php?id=" + articleModal.getId ( )+ "articles" ) )// manually
                                    .setDomainUriPrefix ( "https://agcl.in/a")
                                    .setAndroidParameters ( new DynamicLink.AndroidParameters.Builder ( )
                                            .build ( ) )
                                    .setSocialMetaTagParameters ( new DynamicLink.SocialMetaTagParameters.Builder ( )
                                            .setTitle ( articleModal.getTitle ( ) )
                                            .setDescription ( context.getResources ( ).getString ( R.string.access_farmrise_articles1 ) )
                                            .build ( ) )
                                    .buildShortDynamicLink ( ShortDynamicLink.Suffix.SHORT )
                                    .addOnCompleteListener ((Activity) context, (OnCompleteListener<ShortDynamicLink>) task -> {
                                        if (task.isSuccessful ( )) {
                                            // Short link created
                                            Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink ( );
                                            Uri flowchartLink = task.getResult ( ).getPreviewLink ( );
                                            Log.e ( "main ", "substring1 " + shortLink );
                                            Log.e ( "main ", "substring1 " + flowchartLink );


                                            Intent intent = new Intent ( );
                                            intent.setAction ( Intent.ACTION_SEND );

                                            assert shortLink != null;
                                            intent.putExtra ( Intent.EXTRA_TEXT, shortLink.toString ( ) );
                                            intent.putExtra("title","articles");
                                            intent.setType ( "text/plain" );
                                            context.startActivity ( intent );


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

                }
            });
            ((MyViewHolder) holder).articleCommentCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        //ConstantValues.shareDeepLink1(context, String.format(context.getResources().getString(R.string.access_farmrise_articles), articleModal.getTitle()));

                        try {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("article_id", articleModal.getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
            });
            ((MyViewHolder) holder).product_comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        //ConstantValues.shareDeepLink1(context, String.format(context.getResources().getString(R.string.access_farmrise_articles), articleModal.getTitle()));

                        try {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("article_id", articleModal.getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }
            });
        }
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.articlelayout, parent, false);
        viewHolder = new MyViewHolder(v1);
        return viewHolder;
    }



    @Override
    public int getItemCount() {
        return infoList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

         private TextView tvName;
         private TextView tvDate;
         private TextView articlelikes;
         private TextView articleviews;
         private TextView articleCommentCount;
         private ImageButton product_share_btn;

         private ImageButton product_comment_btn;

         private ImageView imageView;
         private ToggleButton product_like_btn;
         ProgressBar progressBar;

      private   MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.articleTitle);
            tvDate = (TextView) itemView.findViewById(R.id.articleDate);
            articlelikes = (TextView) itemView.findViewById(R.id.articlelikes);
            articleviews = (TextView) itemView.findViewById(R.id.articleviews);
            articleCommentCount = (TextView) itemView.findViewById(R.id.articleCommentCount);
            imageView=(ImageView) itemView.findViewById(R.id.articleImage);
            product_share_btn=(ImageButton) itemView.findViewById(R.id.product_share_btn);

            product_like_btn = (ToggleButton) itemView.findViewById(R.id.product_like_btn1);
            product_comment_btn = (ImageButton) itemView.findViewById(R.id.product_comment_btn);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }



}
