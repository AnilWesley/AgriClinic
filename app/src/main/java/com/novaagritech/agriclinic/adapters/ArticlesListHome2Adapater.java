package com.novaagritech.agriclinic.adapters;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.Info;


import java.util.List;

public class ArticlesListHome2Adapater extends RecyclerView.Adapter<ArticlesListHome2Adapater.ViewHolder>{

    private List<Info> dataModelList;
    private Context context;

    ArticlesListHome2Adapater adapter;

    private DisplayImageOptions options;

    private String TAG="CONTEXT";

    String user_id;
    ProgressDialog pDialog;



    public ArticlesListHome2Adapater(Context ctx, List<Info> dataModelList) {
        this.dataModelList = dataModelList;
        context = ctx;
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
    public ArticlesListHome2Adapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.articlelayouthome2,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Info info =dataModelList.get(position);
        holder.textView.setText(info.getTitle());
        ImageLoader.getInstance()
                .displayImage(info.getImage_url(), holder.imageView, options,
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


        TextView textView;
        ImageView imageView;
        ProgressBar progressBar;



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.articleTitle);
            imageView=(ImageView) itemView.findViewById(R.id.articleImage);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }



}
