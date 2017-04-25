package com.domikado.itaxi.ui.ads.cta;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.repository.CTARepository;

public class SubmitJsInterface implements JSInterface {

    public final Context mContext = TaxiApplication.getComponent().context();
    public final long mAdsId;

    public OnSubmitListener mOnSubmitListener;

    public SubmitJsInterface(long adsId) {
        mAdsId = adsId;
        Log.v("tx","mAdsId = " +mAdsId);
    }

    @JavascriptInterface
    @Override
    public void post(String value) {
        CTARepository.submitCTAResult(mContext, value, mAdsId);
    }

    @JavascriptInterface
    public void close(){
        if (mOnSubmitListener != null)
            mOnSubmitListener.onSubmit(true);
    }

    public void setOnSubmitListener(OnSubmitListener mOnSubmitListener) {
        this.mOnSubmitListener = mOnSubmitListener;
    }

    public interface OnSubmitListener {
        void onSubmit(boolean isSuccess);
    }
}
