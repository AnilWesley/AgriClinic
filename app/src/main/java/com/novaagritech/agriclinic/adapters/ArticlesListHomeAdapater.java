package com.novaagritech.agriclinic.adapters;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.InfoData;


import java.util.List;

public class ArticlesListHomeAdapater extends RecyclerView.Adapter<ArticlesListHomeAdapater.ViewHolder>{

    private List<InfoData> dataModelList;
    private Context context;

    ArticlesListHomeAdapater adapter;

    private DisplayImageOptions options;

    private String TAG="CONTEXT";

    String user_id;
    ProgressDialog pDialog;



    public ArticlesListHomeAdapater(Context ctx, List<InfoData> dataModelList) {
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
    public ArticlesListHomeAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.articlelayout0,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InfoData infoData=dataModelList.get(position);
        holder.textView.setText(infoData.getTitle());
        ImageLoader.getInstance()
                .displayImage(infoData.getImage_url(), holder.imageView, options);

    }


    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {


        TextView textView;
        ImageView imageView;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.articleTitle);
            imageView=(ImageView) itemView.findViewById(R.id.articleImage);
        }
    }



}
