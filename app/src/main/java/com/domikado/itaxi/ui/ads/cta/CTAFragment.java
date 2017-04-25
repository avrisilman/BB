package com.domikado.itaxi.ui.ads.cta;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.repository.AdsRepository;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.ui.base.BaseMvpFragment;
import com.domikado.itaxi.utils.CommonUtils;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CTAFragment extends BaseMvpFragment<CTAView, CTAPresenter> implements CTAView {

    private static final String ADS_ID = "Extra.AdsId";

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.pbLoad)
    ProgressBar mProgressBar;

    @Inject
    CTAPresenter mPresenter;

    private Unbinder unbinder;
    protected OnHomeUpSelectedListener mOnHomeUpSelectedListener;

    public static CTAFragment newInstance(Ads ads) {
        Bundle bundle = new Bundle();
        bundle.putLong(ADS_ID, ads.getId());

        CTAFragment fragment = new CTAFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_cta;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UiComponent.class).inject(this);
    }

    @NonNull
    @Override
    public CTAPresenter createPresenter() {
        return mPresenter;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onSetupView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        long adsId = getArguments().getLong(ADS_ID);
        Ads ads = AdsRepository.getInstance(adsId);

        assert ads != null;
//        String adsServerId = String.valueOf(ads.getServerId());

        SubmitJsInterface jsInterface = new SubmitJsInterface(adsId);
        jsInterface.setOnSubmitListener(isSuccess -> ctaSubmit());

        mPresenter.setAdsModel(ads);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.addJavascriptInterface(jsInterface, JSInterface.NAME);

        if (getActivity() instanceof OnHomeUpSelectedListener) {
            mOnHomeUpSelectedListener = (OnHomeUpSelectedListener) getActivity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.toolbar_action_item)
    public void onHomeButtonClick() {
        ctaSkip();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Ads ads = AdsRepository.getInstance(getArguments().getLong(ADS_ID));
        mPresenter.loadCTA(ads);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        CommonUtils.toast(mContext, e.getMessage());
    }

    @Override
    public void setData(CallToAction data) {
        mWebView.stopLoading();

        String url = "file://" + data.getFile();
        mWebView.loadUrl(url);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
    }

    private void ctaSkip() {
        mPresenter.recordCTABounce();
        popFragment();
    }

    private void ctaSubmit() {
        mPresenter.recordCTASubmit();
        mOnHomeUpSelectedListener.onHomeUpSelected();
        ctaSkip();
        Log.d("HELLO ","TES");

    }

    private boolean popFragment() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mOnHomeUpSelectedListener.onHomeUpSelected();
            return true;
        }
        return false;
    }
}
