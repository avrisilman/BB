package com.domikado.itaxi.ui.ads.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventPopupQuizDisappear;
import com.domikado.itaxi.utils.EBus;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import com.domikado.itaxi.data.entity.ads.Ads;

public class PopupQuiz extends RxDialogFragment {

    @Inject
    PopupPresenter presenter;

    @BindView(R.id.webView)
    WebView mwebView;

    @BindView(R.id.seconds)
    TextView mseconds;
    private Unbinder unbinder;
    private Subscription subscription;
    public static String TAG = "Quiz";
    static long id;
    static long duration;

    public static PopupQuiz newInstance(Ads adses) {
        id = adses.getId();
        duration = adses.getDuration();
        return new PopupQuiz();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_popup_quiz, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mseconds.setBackgroundColor(Color.parseColor("#00FF0000"));

        SubmitJsInterfacePopup jsInterfacePopup = new SubmitJsInterfacePopup(id);
        jsInterfacePopup.setOnSubmitListener(isSuccess -> ctaResultSubmit());

        String url = "file://" + AdsRepository.getInstance(id).getFilepath();

        mwebView.getSettings().setJavaScriptEnabled(true);
        mwebView.addJavascriptInterface(jsInterfacePopup, JSInterfacePopup.NAME);
        mwebView.setWebViewClient(new WebViewClient());
        mwebView.loadUrl(url);

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        startTimer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        subscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        EBus.post(new EventPopupQuizDisappear());
        super.onDestroyView();
    }

    private void ctaResultSubmit() {
        dismiss();
    }

    public void startTimer() {
        subscription = Observable
                .range(0, (int) duration + 1)
                .flatMap(v -> Observable.just(v).delay(duration - v, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(this::dismiss)
                .subscribe(tick -> {
                    mseconds.setText(String.valueOf(tick) + " seconds" );
                });
    }

}