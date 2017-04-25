package com.domikado.itaxi.ui.ads.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.entity.ads.Ads;

import java.util.ArrayList;
import java.util.List;

class BannerAdsAdapter extends PagerAdapter {

    interface OnItemClickListener {
        void onClick(Ads ads);
    }

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Ads> adses = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    BannerAdsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    void setAdses(List<Ads> adses) {
        this.adses = adses;

    }

    Ads getItem(int position) {
        return adses.get(position);
    }

    void setOnClickItemListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return adses.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mLayoutInflater.inflate(R.layout.view_banner_ads_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.taxi_ads_item_image);
        Ads ads = adses.get(position);

        Glide.with(mContext)
            .load(ads.getFilepath())
            .into(imageView);

        Log.e("POSSITION ID=== ", String.valueOf(ads.getId()));

        imageView.setOnClickListener(v -> onItemClickListener.onClick(ads));

        container.addView(view, 0);
        return view;
    }


//    private void checkClickPosition(Ads ads){
//
//        if(ads.getPlacement().equalsIgnoreCase(TOP_BANNER)){
//            BannerAds.ID_BANNER = ads.getId();
//            onItemClickListener.onClick(ads);
//        }
//
//        if(ads.getPlacement().equalsIgnoreCase(BOTTOM_BANNER_LEFT)){
//            BannerAds.ID_BANNER = ads.getId();
//            onItemClickListener.onClick(ads);
//        }
//
//        if(ads.getPlacement().equalsIgnoreCase(BOTTOM_BANNER_RIGHT)){
//            BannerAds.ID_BANNER = ads.getId();
//            onItemClickListener.onClick(ads);
//        }
//
//    }

}
