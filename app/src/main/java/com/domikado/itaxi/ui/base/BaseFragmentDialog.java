package com.domikado.itaxi.ui.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.trello.rxlifecycle.components.support.RxDialogFragment;

public class BaseFragmentDialog extends RxDialogFragment {

    protected Context mContext;
    protected LayoutInflater mInflater;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
    }
}
