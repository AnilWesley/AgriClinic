package com.novaagritech.agriclinic.adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.Banners;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.List;

public class BannerListAdapter extends RecyclerView.Adapter<BannerListAdapter.ViewHolder>{

    private List<Banners.BannerDetails> dataModelList;
    private Context context;



    private DisplayImageOptions options;

    private String TAG="CONTEXT";

    String user_id;
    ProgressDialog pDialog;



    public BannerListAdapter(Context ctx, List<Banners.BannerDetails> dataModelList) {
        this.dataModelList = dataModelList;
        this.context = ctx;

        /*options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available) // resource or drawable
                .showImageForEmptyUri(R.drawable.image_not_available) // resource or drawable
                .showImageOnFail(R.drawable.image_not_available) // resource or drawable
                .resetViewBeforeLoading(true)  // default
                .cacheInMemory(false) // default
                .cacheOnDisk(false) // default
                .considerExifParams(false) // default

                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();*/
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
    public BannerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.articlelayout_adds,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Banners.BannerDetails infoData=dataModelList.get(position);

        Log.d("IMAGES",""+Urls.IMAGE_URL1+infoData.getImage());

        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1 +infoData.getImage(), holder.imageView, options,
                        new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.progressBar.setVisibility(View.GONE);

                    }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.progressBar.setVisibility(View.GONE);

                    }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        holder.progressBar.setVisibility(View.GONE);

                    }
                    });





    }


    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {



        ImageView imageView;
        ProgressBar progressBar;



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.articleImageAdd);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }



}
