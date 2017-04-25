package com.domikado.itaxi.ui.news;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.ui.base.BaseListAdapter;
import com.domikado.itaxi.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsListAdapter extends BaseListAdapter<News> {

    NewsListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = mInflater.inflate(R.layout.list_item_news, parent, false);

        News news = getItem(position);
        NewsViewHolder holder = NewsViewHolder.get(convertView);

        Glide.with(mContext)
            .load(news.getFilepath())
            .into(holder.mImageContent);

        holder.mTxtTitle.setText(Html.fromHtml(news.getTitle()));
        holder.mTxtCaption.setText(news.getSource());
        holder.mTxtTime.setText(DateUtils.getTimeInString(news.getPublishedDate(), "EEE, d MMMM yyyy"));

        return convertView;
    }

    static class NewsViewHolder {

        @BindView(R.id.txtTitle)
        TextView mTxtTitle;

        @BindView(R.id.txtTime)
        TextView mTxtTime;

        @BindView(R.id.txtCaption)
        TextView mTxtCaption;

        @BindView(R.id.imgContent)
        ImageView mImageContent;

        NewsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public static NewsViewHolder get(View view) {
            if (view.getTag() != null) {
                return (NewsViewHolder) view.getTag();
            }
            return new NewsViewHolder(view);
        }
    }
}
