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
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.modals.Crops;

import java.util.List;

public class CropsListAdapater  extends RecyclerView.Adapter<CropsListAdapater.ViewHolder>{

    private List<Crops.CropDetails> dataModelList;
    private Context context;

    CropsListAdapater adapter;

    private DisplayImageOptions options;

    private String TAG="CONTEXT";

    String user_id;
    ProgressDialog pDialog;

    MyAppPrefsManager myAppPrefsManager;

    public  CropsListAdapater( Context ctx,List<Crops.CropDetails> dataModelList) {
        this.dataModelList = dataModelList;
        context = ctx;
        this.adapter = this; //This is an important line, you need this line to keep track the adapter variable

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_account_circle_black_24dp)
                .showImageForEmptyUri(R.drawable.ic_account_circle_black_24dp)
                .showImageOnFail(R.drawable.ic_account_circle_black_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    @NonNull
    @Override
    public CropsListAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.cropslayout0,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Crops.CropDetails cropsModal=dataModelList.get(position);
        holder.textView.setText(cropsModal.getTitle());
        ImageLoader.getInstance()
                .displayImage(cropsModal.getImage_url(), holder.imageView, options);

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
            textView=(TextView)itemView.findViewById(R.id.cropTitle);
            imageView=(ImageView) itemView.findViewById(R.id.cropImage);
        }
    }



}
