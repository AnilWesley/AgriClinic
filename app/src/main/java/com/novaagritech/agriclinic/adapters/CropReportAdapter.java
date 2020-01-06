package com.novaagritech.agriclinic.adapters;

import android.annotation.SuppressLint;
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
import com.novaagritech.agriclinic.modals.ReportDetails;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.List;

public class CropReportAdapter extends RecyclerView.Adapter<CropReportAdapter.NameHolder>
{

    private Context context;
    CropReportAdapter adapter;
    private List<ReportDetails> list;
    private DisplayImageOptions options;


    public CropReportAdapter(Context context, List<ReportDetails> list) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(NameHolder holder, @SuppressLint("RecyclerView") final int position) {

        final ReportDetails visitorDetails = list.get(position);
        holder.textView.setText("Name : "+ visitorDetails.getUser_name());
        holder.textView1.setText("Number : "+ visitorDetails.getMobile());
        holder.textView2.setText("Remarks : "+ visitorDetails.getRemarks());
        holder.textView3.setText("Date : "+ visitorDetails.getCreated_on());
        ImageLoader.getInstance()
                .displayImage(Urls.IMAGE_URL1+visitorDetails.getImage(), holder.imageView, options);

    }

    class NameHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        ImageView imageView;

        public NameHolder(View itemView) {

            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_name);
            textView1 = (TextView) itemView.findViewById(R.id.text_number);
            textView2 = (TextView) itemView.findViewById(R.id.text_whom_they_meet);
            textView3 = (TextView) itemView.findViewById(R.id.text_sign_in_time);
            imageView = (ImageView) itemView.findViewById(R.id.profile_Image);

        }

    }
}


