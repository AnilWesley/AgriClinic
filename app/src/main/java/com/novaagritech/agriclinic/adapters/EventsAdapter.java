package com.novaagritech.agriclinic.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.interfaces.OnLoadMoreListener;
import com.novaagritech.agriclinic.modals.Info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Info> newsModalList;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;

    private DisplayImageOptions options;

    EventsAdapter newsAdapter;

    public EventsAdapter(Context context1, List<Info> newsModalList1, RecyclerView recyclerView) {
        context=context1;
        this.newsModalList = newsModalList1;
        this.newsAdapter = this; //This is an important line, you need this line to keep track the adapter variable
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

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return newsModalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.eventslayout1, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @SuppressLint("SetTextI18n,SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            Info newsModal = (Info) newsModalList.get(position);

            ((MyViewHolder) holder).tvName.setText(newsModal.getTitle());
            ((MyViewHolder) holder).tvStartDate.setText("Start : "+ConstantValues.getFormattedDate1(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, newsModal.getStart_date()));
            ((MyViewHolder) holder).tvEndDate.setText("End : "+ConstantValues.getFormattedDate1(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, newsModal.getEnd_date()));
            //((MyViewHolder) holder).tvDate.setText(articleModal.getCreated_on());

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = null;
            try {
                date = inputFormat.parse(newsModal.getCreated_on());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));

            ((MyViewHolder)holder).tvDate.setText(niceDateStr);
            ImageLoader.getInstance()
                    .displayImage( newsModal.getImage_url(), ((MyViewHolder) holder).imageView, options, new SimpleImageLoadingListener(){
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


            ((MyViewHolder) holder).tvName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent setIntent = new Intent(context, SingleArticleActivity.class);

                    setIntent.putExtra("article_id", articleModal.getId());
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(setIntent);*/
                }
            });

        }


        else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);

        }



    }

    public void setLoaded() {
        loading = false;


    }

    @Override
    public int getItemCount() {
        return newsModalList == null ? 0 : newsModalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }



    //
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDate;

        TextView tvStartDate;
        TextView tvEndDate;
        ImageView imageView;
        ProgressBar progressBar;




        MyViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.newsTitle);
            tvDate = (TextView) v.findViewById(R.id.newsDate);

            tvStartDate = (TextView) v.findViewById(R.id.textStartTime);
            tvEndDate = (TextView) v.findViewById(R.id.textEndTime);

            imageView=(ImageView) itemView.findViewById(R.id.newsImage);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);


        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}