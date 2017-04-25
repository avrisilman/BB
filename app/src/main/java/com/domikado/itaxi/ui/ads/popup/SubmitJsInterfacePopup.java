package com.domikado.itaxi.ui.ads.popup;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.utils.CommonUtils;

public class SubmitJsInterfacePopup implements JSInterfacePopup{

    private final Context mContext = TaxiApplication.getComponent().context();
    private final long mAdsId;

    private SubmitJsInterfacePopup.OnSubmitListener mOnSubmitListener;

    SubmitJsInterfacePopup(long adsId) {
        mAdsId = adsId;
    }

    @JavascriptInterface
    @Override
    public void post(String value) {
        CTARepository.submitCTAResult(mContext, value, mAdsId);
        if (mOnSubmitListener != null)
            mOnSubmitListener.onSubmit(true);
    }

    void setOnSubmitListener(SubmitJsInterfacePopup.OnSubmitListener mOnSubmitListener) {
        this.mOnSubmitListener = mOnSubmitListener;
    }

    interface OnSubmitListener {
        void onSubmit(boolean isSuccess);
    }

}
