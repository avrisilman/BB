package com.domikado.itaxi.ui.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.content.News;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.kiosk.Locker;
import com.domikado.itaxi.ui.base.BaseMvpFragment;
import com.domikado.itaxi.ui.pin.PinDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewsListFragment extends BaseMvpFragment<NewsListView, NewsListPresenter>
    implements NewsListView {

    @BindView(R.id.lvContent)
    ListView mListView;

    @Inject
    AnalyticsAgent mAnalyticsAgent;

    @Inject
    NewsListAdapter mListAdapter;

    @Inject
    NewsListPresenter mPresenter;

    @Inject
    Locker mLocker;

    private NewsListListener mNewsListListener;
    private Unbinder unbinder;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNewsListListener = (NewsListListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UiComponent.class)
            .inject(this);
    }

    @NonNull
    @Override
    public NewsListPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void onSetupView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            News news = mListAdapter.getItem(position);
            mNewsListListener.onNewsSelected(news);
            mAnalyticsAgent.reportNewsClick(news.getServerId());
        });
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_news_list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
    }

    @Override
    public void showContent() {
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
    }

    @Override
    public void setData(List<News> data) {
        mListAdapter.pushData(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        mPresenter.loadNews();
    }

    @OnClick(R.id.txtTitle)
    public void onTitleClick() {
        if (mLocker.enterCombination(KeyEvent.KEYCODE_BACK)) {
            PinDialog.show(getActivity().getSupportFragmentManager());
        }
    }
}
