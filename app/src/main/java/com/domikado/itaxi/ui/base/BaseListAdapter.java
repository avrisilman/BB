package com.domikado.itaxi.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.domikado.itaxi.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BaseListAdapter<T> extends BaseAdapter {

    public static final String PAGE = "BaseListAdapter.Page";

    protected List<T> mListData = new ArrayList<>();

    protected Context mContext;
    protected LayoutInflater mInflater;

    private AccelerateDecelerateInterpolator mTimeInterpolator;

    private int mLastPosition = -1;
    private boolean mAnimating = false;

    public BaseListAdapter(Context context) {
        this(context, false);
    }

    public BaseListAdapter(Context context, boolean animateView) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mAnimating = animateView;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public T getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mAnimating && position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
            animation.setDuration(400);
            animation.setInterpolator(getInterpolator());
            convertView.startAnimation(animation);
            mLastPosition = position;
        }
        return convertView;
    }

    private AccelerateDecelerateInterpolator getInterpolator() {
        if (mTimeInterpolator == null)
            mTimeInterpolator = new AccelerateDecelerateInterpolator();
        return mTimeInterpolator;
    }

    public void pushData(final List<T> data) {
        pushData(data, null);
    }

    public void setData(final int position, T data) {
        mListData.set(position, data);
    }

    public List<T> getListData() {
        return mListData;
    }

    public void clear() {
        mListData.clear();
        notifyDataSetChanged();
    }

    public void pushData(final List<T> data, Bundle bundle) {

        if (data == null) {
            Timber.w("Data null pushed");
            return;
        }

        if (bundle == null || !bundle.containsKey(PAGE) || bundle.getInt(PAGE) == 1) {
            mListData.clear();
            mLastPosition = -1;
        }

        mListData.addAll(data);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mListData.remove(position);
        notifyDataSetChanged();
    }
}
