package com.domikado.itaxi.injection.component;

import com.domikado.itaxi.injection.scope.PerView;
import com.domikado.itaxi.ui.UiModule;
import com.domikado.itaxi.ui.ads.MainActivity;
import com.domikado.itaxi.ui.ads.PremiumActivity;
import com.domikado.itaxi.ui.ads.SplashActivity;
import com.domikado.itaxi.ui.ads.VacantActivity;
import com.domikado.itaxi.ui.ads.banner.BannerAds;
import com.domikado.itaxi.ui.ads.cta.CTAFragment;
import com.domikado.itaxi.ui.ads.popup.PopupQuiz;
import com.domikado.itaxi.ui.ads.premium.PremiumAds;
import com.domikado.itaxi.ui.ads.premium.PremiumAdsWithCTA;
import com.domikado.itaxi.ui.ads.video.VideoAds;
import com.domikado.itaxi.ui.ads.videoads.MainImageAds;
import com.domikado.itaxi.ui.ads.videoads.MainVideoAds;
import com.domikado.itaxi.ui.base.BaseActivity;
import com.domikado.itaxi.ui.base.UsbSerialActivity;
import com.domikado.itaxi.ui.menu.MenuFragment;
import com.domikado.itaxi.ui.menu.MenuFragmentList;
import com.domikado.itaxi.ui.news.NewsListFragment;
import com.domikado.itaxi.ui.settings.ContentManagerActivity;
import com.domikado.itaxi.ui.settings.SettingPanelActivity;

import dagger.Subcomponent;

@PerView
@Subcomponent(modules = {
    UiModule.class
})
public interface UiComponent {

    void inject(BaseActivity activity);
    void inject(UsbSerialActivity activity);
    void inject(VacantActivity activity);
    void inject(SplashActivity activity);
    void inject(PremiumActivity activity);
    void inject(MainActivity activity);
    void inject(SettingPanelActivity activity);
    void inject(ContentManagerActivity activity);

    void inject(NewsListFragment newsListFragment);
    void inject(CTAFragment CTAFragment);
    void inject(MenuFragment menuFragment);
    void inject(MenuFragmentList menuFragmentList);

    void inject(BannerAds bannerAds);
    void inject(VideoAds videoAds);
    void inject(PopupQuiz popupQuiz);
    void inject(PremiumAds ads);
    void inject(PremiumAdsWithCTA ads);
    void inject(MainVideoAds mainVideoAds);
    void inject(MainImageAds mainImageAds);

    class Initializer {
        public static UiComponent init(ApplicationComponent applicationComponent) {
            return applicationComponent.plus(new UiModule());
        }
    }
}
