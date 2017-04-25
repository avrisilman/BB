package com.domikado.itaxi.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.domikado.itaxi.injection.HasComponent;
import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class BaseFragment extends RxFragment {

    protected Context mContext;
    protected LayoutInflater mInflater;

    protected OnHomeUpSelectedListener mOnHomeUpSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);

        if (activity instanceof OnHomeUpSelectedListener) {
            mOnHomeUpSelectedListener = (OnHomeUpSelectedListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mInflater.inflate(getResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onSetupView(view, savedInstanceState);
    }

    protected abstract void onSetupView(View view, Bundle savedInstanceState);

    protected abstract int getResId();

    public ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void finish() {
        if (isAdded())
            getActivity().finish();
    }

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    public interface OnHomeUpSelectedListener {
        void onHomeUpSelected();
    }
}
