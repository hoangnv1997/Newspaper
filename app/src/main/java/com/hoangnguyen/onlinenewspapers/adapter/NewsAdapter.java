package com.hoangnguyen.onlinenewspapers.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hoangnguyen.onlinenewspapers.R;
import com.hoangnguyen.onlinenewspapers.commom.Utils;
import com.hoangnguyen.onlinenewspapers.model.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> mListNews;
    private Context mContext;
    private ItemClickListener onItemClickListener;

    public NewsAdapter(List<News> mListNews, Context mContext) {
        this.mListNews = mListNews;
        this.mContext = mContext;
    }

    public void clear() {
        mListNews.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflate custom layout
        View newsView = inflater.inflate(R.layout.item_news, parent, false);
        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(newsView, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        News news = mListNews.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(mContext)
                .load(news.getmUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mImage);
        holder.mTvTitle.setText(news.getmTitle());
        holder.mTvDescription.setText(news.getmDescription());
        holder.mTvPublishDate.setText(Utils.DateFormat(news.getmPubDate()));
        holder.mTvAuthor.setText(news.getmAuthor());
        holder.mTvSource.setText(news.getmSource());
        holder.mTvTime.setText(" \u2022 " + Utils.DateToTimeFormat(news.getmPubDate()));


    }

    @Override
    public int getItemCount() {
        return mListNews.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
    public void setOnItemClickListener(ItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView mImage;
        private TextView mTvTitle, mTvDescription, mTvAuthor, mTvSource, mTvPublishDate, mTvTime;
        private ProgressBar mProgressBar;
        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvDescription = itemView.findViewById(R.id.tv_description);
            mTvAuthor = itemView.findViewById(R.id.author);
            mTvSource = itemView.findViewById(R.id.tv_source);
            mTvPublishDate = itemView.findViewById(R.id.tv_publish_date);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mProgressBar = itemView.findViewById(R.id.progress_load_photo);
            itemView.setOnClickListener(this);
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }
}
