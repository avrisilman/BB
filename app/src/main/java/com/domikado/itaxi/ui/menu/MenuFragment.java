package com.domikado.itaxi.ui.menu;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.domikado.itaxi.Constant;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.injection.HasComponent;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.kiosk.Locker;
import com.domikado.itaxi.ui.ads.LeftAdsFragment;
import com.domikado.itaxi.ui.pin.PinDialog;
import com.domikado.itaxi.utils.DateUtils;
import com.trello.rxlifecycle.components.support.RxFragment;
import java.util.Locale;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by domikado on 12/15/16.
 */

public class MenuFragment extends RxFragment implements TaxiWebChromeClient.ProgressListener {

    @BindView(R.id.webView)
    WebView mwebView;

    @BindView(R.id.date)
    TextView mdate;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.refresh)
    ImageView refresh;

    @BindView(R.id.progressBar)
    ProgressBar mprogressBar;

    @BindView(R.id.layoutRequestError)
    LinearLayout mlLayoutRequestError = null;

    @Inject
    Locker mLocker;

    private Unbinder unbinder;

    private Handler mhErrorLayoutHide = null;

    private boolean mbErrorOccured = false;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        getComponent(UiComponent.class).inject(this);
        unbinder = ButterKnife.bind(this, view);
        settingWebviewClient();
        setDateTime();
        mwebView.loadUrl(String.format("%s/%s",
            TaxiApplication.getSettings().getMenuUrl(),
            TaxiApplication.getComponent().device().getIMEI()));
        mhErrorLayoutHide = getErrorLayoutHideHandler();

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.imageView)
    void onTitleClick() {
        if (mLocker.enterCombination(KeyEvent.KEYCODE_BACK)) {
            PinDialog.show(getActivity().getSupportFragmentManager());
        }
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    private void settingWebviewClient() {
        mwebView.getSettings().setJavaScriptEnabled(true);
        mwebView.setWebChromeClient(new TaxiWebChromeClient(this));
        mwebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                moveFragment(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mprogressBar != null){
                    mprogressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(mprogressBar != null){
                    mprogressBar.setVisibility(View.GONE);
                }

                if (!mbErrorOccured) {
                    hideErrorLayout();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mbErrorOccured = true;
                showErrorLayout();
                super.onReceivedError(view, request, error);
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
                if(mlLayoutRequestError != null){
                    mlLayoutRequestError.setVisibility(View.GONE);
                }
                super.handleMessage(msg);
            }
        };
    }

    @OnClick({R.id.reload,R.id.refresh})
    void onWebViewRefreshButton() {
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

    public void moveFragment(String url) {
        MenuFragmentList second = MenuFragmentList.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL_MENU_WEBVIEW, url);
        second.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.side_container, second)
                .addToBackStack(null)
                .commit();
    }

    public void setDateTime() {
        Long now = System.currentTimeMillis();
        String date = DateUtils.getTimeInString(now, "EEEE, dd MMMM yyyy", new Locale("in", "ID", "ID"));
        mdate.setText(date);
    }

}