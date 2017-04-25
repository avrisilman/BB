package com.domikado.itaxi.ui.news;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.ui.base.BaseFragment;
import com.domikado.itaxi.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewsFragment extends BaseFragment {

    @BindView(R.id.imgContent)
    ImageView mImgContent;

    @BindView(R.id.txtTitle)
    TextView mTxtTitle;

    @BindView(R.id.txtCaption)
    TextView mTxtSource;

    @BindView(R.id.txtTime)
    TextView mTxtTime;

    @BindView(R.id.txtContent)
    TextView mTxtContent;

    private Unbinder unbinder;

    public static NewsFragment newInstance(News news) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(News.class.getSimpleName(), news);

        NewsFragment newsFragment = new NewsFragment();
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void onSetupView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        mImgContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mImgContent.getViewTreeObserver().removeOnPreDrawListener(this);
                mImgContent.getLayoutParams().height = mImgContent.getWidth() * 2 / 3;
                return false;
            }
        });
    }

    @OnClick(R.id.toolbar_action_item)
    public void onHomeButtonClick() {
        mOnHomeUpSelectedListener.onHomeUpSelected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        News news = bundle.getParcelable(News.class.getSimpleName());

        assert news != null;
        String mTitle = news.getTitle();
        String mImageUrl = news.getFilepath();
        String mContent = news.getContent();
        String source = news.getSource();

        Glide.with(mContext)
            .load(mImageUrl)
            .centerCrop()
            .into(mImgContent);

        mTxtTitle.setText(mTitle);
        mTxtSource.setText(source);
        mTxtTime.setText(DateUtils.getTimeInString(news.getPublishedDate(), "EEE, d MMMM yyyy"));
        mTxtContent.setText(Html.fromHtml(mContent));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
