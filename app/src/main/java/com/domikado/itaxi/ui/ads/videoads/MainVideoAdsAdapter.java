package com.domikado.itaxi.ui.ads.videoads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.domikado.itaxi.data.entity.ads.Ads;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class MainVideoAdsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Ads> adses = new ArrayList<>();

    public MainVideoAdsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setAdses(List<Ads> adses) {
        this.adses = adses;
    }

    @Override
    public int getCount() {
        return adses.size();
    }

    @Override
    public Object getItem(int i) {
        return adses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Ads ads = (Ads) getItem(i);
        Timber.i(ads.getTags());
        Timber.i(ads.getType());
        Timber.i(String.valueOf(ads.getCallToActionId()));

        if(ads.getType().equalsIgnoreCase("video")){
            MainVideoAds mainVideoAds = new MainVideoAds(mContext);
            mainVideoAds.setAds(ads);
            return mainVideoAds;
        } else {
            MainImageAds mainImageAds = new MainImageAds(mContext);
            mainImageAds.setAds(ads);
            return mainImageAds;
        }


    }
}

