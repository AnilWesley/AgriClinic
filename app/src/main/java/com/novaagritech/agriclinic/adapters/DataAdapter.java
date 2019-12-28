package com.novaagritech.agriclinic.adapters;


import android.content.Context;
import android.graphics.Bitmap;
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
import com.novaagritech.agriclinic.activities.ArticlesListActivity;
import com.novaagritech.agriclinic.interfaces.OnLoadMoreListener;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.InfoData;
import com.novaagritech.agriclinic.utilities.Urls;

import java.util.List;



public class DataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<InfoData> articleModalList;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    Context context;

    private DisplayImageOptions options;


    public DataAdapter(ArticlesListActivity context, List<InfoData> articleModal1List, RecyclerView recyclerView) {
        this.context=context;
        articleModalList = articleModal1List;

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


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        return articleModalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.articlelayout1, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            InfoData articleModal = (InfoData) articleModalList.get(position);

            ((MyViewHolder) holder).tvName.setText(articleModal.getTitle());
            ((MyViewHolder) holder).tvDate.setText(ConstantValues.getFormattedDate(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, articleModal.getCreated_on()));
            //((MyViewHolder) holder).tvDate.setText(articleModal.getCreated_on());

            ImageLoader.getInstance()
                    .displayImage(Urls.IMAGE_URL+ articleModal.getImage_path(), ((MyViewHolder) holder).imageView, options,
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



            ((MyViewHolder) holder).tvName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent setIntent = new Intent(context, SingleArticleActivity.class);

                    setIntent.putExtra("article_id", articleModal.getId());
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(setIntent);*/
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);

        }

    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return articleModalList == null ? 0 : articleModalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvDate;
        ImageView imageView;
        ProgressBar progressBar;




        public MyViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.articleTitle);
            tvDate = (TextView) v.findViewById(R.id.articleDate);

            imageView=(ImageView) itemView.findViewById(R.id.articleImage);
            progressBar=(ProgressBar) itemView.findViewById(R.id.progressBar);


        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}