package com.domikado.itaxi.ui.menu;

import android.webkit.WebView;
import android.webkit.WebChromeClient;

/**
 * Created by domikado on 1/17/17.
 */

public class TaxiWebChromeClient extends WebChromeClient {

    private ProgressListener mListener;

    public TaxiWebChromeClient(ProgressListener listener) {
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
//        if(mListener != null){
//            mListener.onUpdateProgress(newProgress);
//        }
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public interface ProgressListener {
        void onUpdateProgress(int progressValue);
    }

}
