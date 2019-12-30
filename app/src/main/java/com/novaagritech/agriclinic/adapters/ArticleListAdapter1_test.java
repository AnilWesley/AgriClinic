package com.novaagritech.agriclinic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.SingleArticleActivity;
import com.novaagritech.agriclinic.comments.ChatActivity;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.ArticlesList;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.modals.InfoData_test;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Suleiman on 19/10/16.
 */

public class ArticleListAdapter1_test extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ARTICLES = 0;
    private static final int LOADING = 1;
    private List<InfoData> infoDataList;
    private List<InfoData_test> infoDataList1;
    private Context context;
    private DisplayImageOptions options;


    private MyAppPrefsManager myAppPrefsManager;
    private String user_id;
    private int likes_count;

    private List<Object> items = new ArrayList<> (  );


    private static final String TAG = "ArticleListActivity11";

    public ArticleListAdapter1_test(Context context, List<InfoData> infoDataList, List<InfoData_test> infoDataList1) {
        this.context = context;
        this.infoDataList = infoDataList;
        this.infoDataList1 = infoDataList1;

        items.addAll ( infoDataList );
        items.addAll ( infoDataList1 );

        myAppPrefsManager = new MyAppPrefsManager ( context );
        options = new DisplayImageOptions.Builder ( )
                .showImageOnLoading ( R.drawable.image_not_available )
                .showImageForEmptyUri ( R.drawable.image_not_available )
                .showImageOnFail ( R.drawable.image_not_available )
                .cacheInMemory ( true )
                .cacheOnDisk ( true )
                .considerExifParams ( true )
                .bitmapConfig ( Bitmap.Config.RGB_565 )
                .displayer ( new RoundedBitmapDisplayer ( 20 ) )
                .build ( );
    }


    public List<InfoData> getInfoDataList() {
        return infoDataList;
    }

    public void setInfoDataList(List<InfoData> infoDataList) {
        this.infoDataList = infoDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from ( parent.getContext ( ) );

        switch (viewType) {
            case TYPE_ARTICLES:
                View v1 = inflater.inflate ( R.layout.articlelayout, parent, false );
                viewHolder = new MyViewHolder ( v1 );
                break;
            case LOADING:
                View v2 = inflater.inflate ( R.layout.articlelayout_test, parent, false );
                viewHolder = new ProgressViewHolder ( v2 );
                break;
        }
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {

            InfoData articleModal = (InfoData) items.get ( position );

            user_id = myAppPrefsManager.getUserId ( );


            ((MyViewHolder) holder).tvName.setText ( articleModal.getTitle ( ) );
            ((MyViewHolder) holder).articlelikes.setText ( articleModal.getLikes_count ( ) + " " + "likes" );
            ((MyViewHolder) holder).articleviews.setText ( articleModal.getViews_count ( ) + " " + "views" );
            ((MyViewHolder) holder).articleCommentCount.setText ( "View All " + articleModal.getComment_count ( ) + " Comments" );
            ((MyViewHolder) holder).tvDate.setText ( ConstantValues.getFormattedDate ( MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, articleModal.getCreated_on ( ) ) );
            //((MyViewHolder) holder).tvDate.setText(articleModal.getCreated_on());

            ImageLoader.getInstance ( )
                    .displayImage ( Urls.IMAGE_URL + articleModal.getImage_path ( ), ((MyViewHolder) holder).imageView, options,
                            new SimpleImageLoadingListener ( ) {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    ((MyViewHolder) holder).progressBar.setVisibility ( View.VISIBLE );
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ((MyViewHolder) holder).progressBar.setVisibility ( View.GONE );

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ((MyViewHolder) holder).progressBar.setVisibility ( View.GONE );

                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    ((MyViewHolder) holder).progressBar.setVisibility ( View.GONE );

                                }
                            } );


            ((MyViewHolder) holder).imageView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent ( context, SingleArticleActivity.class );
                    setIntent.putExtra ( "article_id", infoDataList.get ( position ).getId ( ) );
                    setIntent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    context.startActivity ( setIntent );
                }
            } );


            // Check if the User has Liked the Product
            if (articleModal.getIs_liked ( ) == 1) {
                ((MyViewHolder) holder).product_like_btn.setChecked ( true );

            } else {
                ((MyViewHolder) holder).product_like_btn.setChecked ( false );
            }

            // Check if the User has Liked the Product
            if (articleModal.getComment_count ( ) == 0) {

                ((MyViewHolder) holder).articleCommentCount.setText ( "Add Comments" );


            }
            // Handle Click event of product_like_btn Button
            ((MyViewHolder) holder).product_like_btn.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        // Check if the User has Checked the Like Button
                        if (((MyViewHolder) holder).product_like_btn.isChecked ( )) {
                            articleModal.setIs_liked ( 1 );
                            ((MyViewHolder) holder).product_like_btn.setChecked ( true );
                            // Request the Server to Like the Product for the User

                            // prepare call in Retrofit 2.0
                            JsonObject jsonObject = new JsonObject ( );

                            jsonObject.addProperty ( "user_id", user_id );
                            jsonObject.addProperty ( "article_id", articleModal.getId ( ) );
                            jsonObject.addProperty ( "is_liked", 1 );

                            Log.d ( TAG, "" + jsonObject );
                            ApiInterface service = RetrofitClientInstance.getRetrofitInstance ( ).create ( ApiInterface.class );
                            Call<ArticlesList> call = service.processArticlesLikes ( jsonObject );
                            call.enqueue ( new Callback<ArticlesList> ( ) {
                                @Override
                                public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {

                                    // Check if the Response is successful
                                    if (response.isSuccessful ( )) {
                                        // Check the Success status
                                        if (response.body ( ).getMessage ( ).equalsIgnoreCase ( "Data Updated successfully!" )) {

                                            // Product has been Disliked. Show the message to User
                                            likes_count = response.body ( ).getLikes_count ( );
                                            Log.d ( TAG, "Count : " + likes_count );
                                            //((MyViewHolder) holder).articlelikes.setText((likes_count)+" "+"likes");
                                            ((MyViewHolder) holder).articlelikes.setText ( ((infoDataList.get ( position ).getLikes_count ( )) + 1) + " " + "likes" );
                                            infoDataList.get ( position ).setLikes_count ( ((infoDataList.get ( position ).getLikes_count ( )) + 1) );
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {

                                    Log.d ( "ResponseF", "" + t );
                                }
                            } );

                        } else {
                            articleModal.setIs_liked ( 0 );
                            ((MyViewHolder) holder).product_like_btn.setChecked ( false );

                            // Request the Server to Unlike the Product for the User
                            // prepare call in Retrofit 2.0
                            JsonObject jsonObject = new JsonObject ( );


                            jsonObject.addProperty ( "user_id", user_id );
                            jsonObject.addProperty ( "article_id", articleModal.getId ( ) );
                            jsonObject.addProperty ( "is_liked", 0 );

                            Log.d ( TAG, "" + jsonObject );
                            ApiInterface service = RetrofitClientInstance.getRetrofitInstance ( ).create ( ApiInterface.class );
                            Call<ArticlesList> call = service.processArticlesUnLikes ( jsonObject );
                            call.enqueue ( new Callback<ArticlesList> ( ) {
                                @Override
                                public void onResponse(@NonNull Call<ArticlesList> call, @NonNull Response<ArticlesList> response) {

                                    // Check if the Response is successful
                                    if (response.isSuccessful ( )) {
                                        // Check the Success status
                                        if (response.body ( ).getMessage ( ).equalsIgnoreCase ( "Data Updated successfully!" )) {

                                            // Product has been Disliked. Show the message to User
                                            likes_count = response.body ( ).getLikes_count ( );
                                            Log.d ( TAG, "Count : " + likes_count );
                                            // ((MyViewHolder) holder).articlelikes.setText((likes_count)+" "+"likes");
                                            ((MyViewHolder) holder).articlelikes.setText ( ((infoDataList.get ( position ).getLikes_count ( )) - 1) + " " + "likes" );
                                            infoDataList.get ( position ).setLikes_count ( ((infoDataList.get ( position ).getLikes_count ( )) - 1) );

                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ArticlesList> call, @NonNull Throwable t) {

                                    Log.d ( "ResponseF", "" + t );
                                }
                            } );


                        }


                    }
                }
            } );

            ((MyViewHolder) holder).product_share_btn.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        //ConstantValues.shareDeepLink1(context, String.format(context.getResources().getString(R.string.access_farmrise_articles), articleModal.getTitle()));

                        try {
                            String shareMessage;
                            shareMessage = context.getResources ( ).getString ( R.string.access_farmrise_articles1 ) + "\n" + articleModal.getTitle ( );
                            Intent shareIntent = new Intent ( Intent.ACTION_SEND );
                            shareIntent.setType ( "text/plain" );

                            shareIntent.putExtra ( Intent.EXTRA_TEXT, shareMessage );
                            context.startActivity ( Intent.createChooser ( shareIntent, "choose one" ) );
                        } catch (Exception e) {
                            e.printStackTrace ( );

                        }

                    }
                }
            } );
            ((MyViewHolder) holder).articleCommentCount.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        //ConstantValues.shareDeepLink1(context, String.format(context.getResources().getString(R.string.access_farmrise_articles), articleModal.getTitle()));

                        try {
                            Intent intent = new Intent ( context, ChatActivity.class );
                            intent.putExtra ( "article_id", articleModal.getId ( ) );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            context.startActivity ( intent );
                        } catch (Exception e) {
                            e.printStackTrace ( );

                        }

                    }
                }
            } );
            ((MyViewHolder) holder).product_comment_btn.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {

                        //ConstantValues.shareDeepLink1(context, String.format(context.getResources().getString(R.string.access_farmrise_articles), articleModal.getTitle()));

                        try {
                            Intent intent = new Intent ( context, ChatActivity.class );
                            intent.putExtra ( "article_id", articleModal.getId ( ) );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            context.startActivity ( intent );
                        } catch (Exception e) {
                            e.printStackTrace ( );

                        }

                    }
                }
            } );


        }



        else {

            InfoData_test articleModal = (InfoData_test) items.get ( position );

            Log.v ( "hiiii",Urls.IMAGE_URL1 + articleModal.getImage ( ) );

            ImageLoader.getInstance ( )
                    .displayImage ( Urls.IMAGE_URL1 + articleModal.getImage ( ), ((ProgressViewHolder) holder).articleImage1, options,
                            new SimpleImageLoadingListener ( ) {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    ((ProgressViewHolder) holder).articleImage1.setVisibility ( View.VISIBLE );
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ((ProgressViewHolder) holder).articleImage1.setVisibility ( View.GONE );

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ((ProgressViewHolder) holder).articleImage1.setVisibility ( View.GONE );

                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    ((ProgressViewHolder) holder).articleImage1.setVisibility ( View.GONE );

                                }
                            } );

        }

    }


    @Override
    public int getItemCount() {

        return items.size ();
    }

    @Override
    public int getItemViewType(int position) {


        return items.get ( position ) instanceof InfoData? TYPE_ARTICLES:LOADING;

        //return (position == infoDataList.size() - 1 && isLoadingAdded) ? LOADING : TYPE_ARTICLES;

    }








   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
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


        private MyViewHolder(View itemView) {
            super ( itemView );

            tvName = (TextView) itemView.findViewById ( R.id.articleTitle );
            tvDate = (TextView) itemView.findViewById ( R.id.articleDate );
            articlelikes = (TextView) itemView.findViewById ( R.id.articlelikes );
            articleviews = (TextView) itemView.findViewById ( R.id.articleviews );
            articleCommentCount = (TextView) itemView.findViewById ( R.id.articleCommentCount );
            imageView = (ImageView) itemView.findViewById ( R.id.articleImage );
            product_share_btn = (ImageButton) itemView.findViewById ( R.id.product_share_btn );
            progressBar = (ProgressBar) itemView.findViewById ( R.id.progressBar );

            product_like_btn = (ToggleButton) itemView.findViewById ( R.id.product_like_btn1 );
            product_comment_btn = (ImageButton) itemView.findViewById ( R.id.product_comment_btn );


        }
    }


    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ImageView articleImage1;

        private ProgressViewHolder(View v) {
            super ( v );
            articleImage1 = (ImageView) v.findViewById ( R.id.articleImage1 );
        }
    }

}