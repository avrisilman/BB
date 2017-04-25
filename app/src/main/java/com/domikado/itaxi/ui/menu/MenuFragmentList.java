package com.domikado.itaxi.ui.menu;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.R;
import com.domikado.itaxi.injection.HasComponent;
import com.domikado.itaxi.injection.component.UiComponent;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by domikado on 1/17/17.
 */

public class MenuFragmentList extends RxFragment implements TaxiWebChromeClient.ProgressListener {

    @BindView(R.id.webView)
    WebView mwebView;

    @BindView(R.id.close)
    ImageButton mclose;

    @BindView(R.id.back)
    ImageButton mback;

    @BindView(R.id.forward)
    ImageButton mforward;

    @BindView(R.id.progressBar)
    ProgressBar mprogressBar;

    @BindView(R.id.layoutRequestError)
    LinearLayout mlLayoutRequestError = null;

    @BindView(R.id.reload)
    ImageButton mreload;

    private Unbinder unbinder;

    private Handler mhErrorLayoutHide = null;

    private boolean mbErrorOccured = false;

    public static MenuFragmentList newInstance() {
        return new MenuFragmentList();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        getComponent(UiComponent.class).inject(this);
        unbinder = ButterKnife.bind(this, view);
        settingWebviewClient();
        getBundleUrlmenu();
        mhErrorLayoutHide = getErrorLayoutHideHandler();

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    private void getBundleUrlmenu(){
        Bundle b = getArguments();
        String url = b.getString(Constant.URL_MENU_WEBVIEW);
        mwebView.loadUrl(url);
    }

    private void settingWebviewClient() {
        mwebView.getSettings().setJavaScriptEnabled(true);
        mwebView.setWebChromeClient(new TaxiWebChromeClient(this));
        mwebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mprogressBar != null){
                    mprogressBar.setVisibility(View.VISIBLE);
                    setBackForwardButtonState();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                setBackForwardButtonState();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(mprogressBar != null){
                    mprogressBar.setVisibility(View.GONE);
                    setBackForwardButtonState();

                    if (!mbErrorOccured) {
                        hideErrorLayout();
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mbErrorOccured = true;
                showErrorLayout();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

    }

    private void showErrorLayout() {
        mlLayoutRequestError.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        mhErrorLayoutHide.sendEmptyMessageDelayed(10000, 200);
    }

    private Handler getErrorLayoutHideHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mlLayoutRequestError.setVisibility(View.GONE);
                super.handleMessage(msg);
            }
        };
    }

    private void setBackForwardButtonState() {
        if(mwebView != null && mback != null && mforward != null) {
            mback.setEnabled(mwebView.canGoBack());
            mforward.setEnabled(mwebView.canGoForward());
        }
    }

    @OnClick(R.id.back)
    void onWebViewBackButton() {
        mwebView.goBack();
    }

    @OnClick(R.id.forward)
    void onWebViewForwardButton() {
        mwebView.goForward();
    }

    @OnClick(R.id.close)
    void onWebViewClosebutton() {
        getActivity().getSupportFragmentManager().popBackStack();
        if (mwebView != null) {
            mwebView.destroy();
            mwebView.loadUrl("about: blank");
            this.mwebView = null;
        }
    }

    @OnClick({R.id.reload, R.id.refresh})
    void onWebviewReloadButton() {
        mwebView.reload();
        mbErrorOccured = false;
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        if(mprogressBar != null){
            mprogressBar.setProgress(progressValue);
            if (progressValue == 100) {
                mprogressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

}
