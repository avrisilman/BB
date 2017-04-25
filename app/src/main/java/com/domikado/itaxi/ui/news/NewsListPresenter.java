package com.domikado.itaxi.ui.news;

import com.domikado.itaxi.data.repository.NewsRepository;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewsListPresenter extends MvpNullObjectBasePresenter<NewsListView> {

    void loadNews() {
        getView().showLoading(true);

        Observable.just(NewsRepository.getAll())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(news -> {
                getView().setData(news);
                getView().showContent();
            }, Timber::e);
    }
}
