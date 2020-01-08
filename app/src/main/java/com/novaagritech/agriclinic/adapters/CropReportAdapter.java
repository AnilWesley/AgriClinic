package com.novaagritech.agriclinic.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
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
import com.novaagritech.agriclinic.utilities.Urls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CropReportAdapter extends RecyclerView.Adapter<CropReportAdapter.NameHolder> {

    private Context context;
    CropReportAdapter adapter;
    private List<Info> list;
    private DisplayImageOptions options;

    public CropReportAdapter(Context context, List<Info> list) {
        this.context = context;
        this.list = list;
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public NameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.reportlayout0,null);
        return new NameHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(NameHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Info visitorDetails = list.get(position);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        holder.cropName.setText("Crop Name : "+ visitorDetails.getCrop_name());
        holder.cropRemarks.setText("Remarks : "+ visitorDetails.getRemarks());
        Date date = null;
        try {
            date = inputFormat.parse(visitorDetails.getCreated_on());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));

        holder.cropDate.setText(niceDateStr);
        if (visitorDetails.getReply().equalsIgnoreCase("")){
            holder.cropReply.setVisibility(View.GONE);
        }else {
            holder.cropReply.setText("Solution : " + visitorDetails.getReply());
        }
        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+visitorDetails.getImage(), holder.imageView, options,
                        new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                (holder).progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ( holder).progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ( holder).progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                ( holder).progressBar.setVisibility(View.GONE);

            }
        });


    }

    class NameHolder extends RecyclerView.ViewHolder
    {

        TextView cropName;
        TextView cropRemarks;
        TextView cropReply;
        TextView cropDate;
        ImageView imageView;
        ProgressBar progressBar;

        NameHolder(View itemView) {

            super(itemView);

            cropName = (TextView) itemView.findViewById(R.id.cropName);
            cropRemarks = (TextView) itemView.findViewById(R.id.cropRemarks);
            cropReply = (TextView) itemView.findViewById(R.id.cropReply);
            cropDate = (TextView) itemView.findViewById(R.id.cropDate);
            imageView = (ImageView) itemView.findViewById(R.id.profile_Image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

        }

    }
}


