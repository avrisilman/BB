package com.domikado.itaxi.ui.ads.videoads;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.Placement;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.repository.PlacementRepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventCallToAction;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.events.EventVideoAdsCompletion;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.ui.ads.video.VideoAdsManager;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.video.ScalableType;
import com.domikado.itaxi.utils.video.ScaleManager;
import com.domikado.itaxi.utils.video.Size;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MainVideoAds extends LinearLayout {

    @BindView(R.id.texture_video)
    TextureView textureView;

    @Inject
    AnalyticsAgent mAnalyticsAgent;

    @BindView(R.id.taxivid_btn_calltoaction)
    ImageView mBtnCallToAction;

    private Subscription subscription;
    private Unbinder unbinder;
    private MediaPlayer mediaPlayer;
    private Surface mSurface;
    private Ads ads;
    private long metricsId = -1;
    private int videoPosition;

    public MainVideoAds(Context context) {
        super(context);
        inflate(context, R.layout.view_category_video, this);

        UiComponent.Initializer
                .init(TaxiApplication.getComponent())
                .inject(this);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EBus.register(this);
        unbinder = ButterKnife.bind(this);

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                releaseSurface();
                mSurface = new Surface(surfaceTexture);
                playVideo();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {}

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        unbinder.unbind();
        EBus.unregister(this);
        super.onDetachedFromWindow();
        if (subscription != null)
            subscription.unsubscribe();
    }

    public void setAds(Ads ads) {
        this.ads = ads;
        setDataSource(ads.getFilepath());
    }

    @Subscribe
    public void onComplete() {
        mAnalyticsAgent.reportAdsFinished(metricsId);
        if(ads.haveCallToAction()){
            showCalltoAction();
        }else{
            EBus.post(new EventVideoAdsCompletion());
        }
    }

    @Subscribe
    public void onEvent(EventPause e) {
        Log.e("xxx","EventPause");
        getMediaPlayer().setVolume(0f, 0f);
        getMediaPlayer().pause();
        videoPosition = getMediaPlayer().getCurrentPosition();
    }

    @Subscribe
    public void onEvent(EventUnpause e) {
        Log.e("xxx","EventUnpause");
        getMediaPlayer().setVolume(1f, 1f);
        getMediaPlayer().seekTo(videoPosition);
        getMediaPlayer().start();
    }

    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnErrorListener((mp, i, i1) -> {
                Timber.d("MediaPlayer:" + mp.toString());
                Timber.d("MediaPlayer error:" + i + " what: " + i1);
                release();
                return false;
            });
            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> onComplete());
        }
        return mediaPlayer;
    }

    public void initLayout(int videoWidth, int videoHeight) {
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();

        Size viewSize = new Size(viewWidth, viewHeight);
        Size videoSize = new Size(videoWidth, videoHeight);

        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        textureView.setTransform(scaleManager.getScaleMatrix(ScalableType.FIT_CENTER));
    }

    private void playVideo() {
        getMediaPlayer().setSurface(mSurface);
        getMediaPlayer().setOnPreparedListener(mp -> {
            int width = getMediaPlayer().getVideoWidth();
            int height = getMediaPlayer().getVideoHeight();
            Timber.d("Width:" + width + " height:" + height);
            initLayout(width, height);

            mAnalyticsAgent.reportAdsStart(ads)
                    .subscribe(aLong -> {
                        metricsId = aLong;
                    });
            mp.start();
        });
        setDataSource(ads.getFilepath());
        getMediaPlayer().prepareAsync();
    }

    private void release() {
        releaseSurface();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void releaseSurface() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

    private synchronized boolean setDataSource(String videoPath) {
        try {
            getMediaPlayer().setDataSource(getContext(), Uri.parse(videoPath));
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

    public void showCalltoAction(){
        callToActionImage();
        String cta_file = CTARepository.findCTAById(ads.getCallToActionId()).getImageFile();
        String filepath = new File(cta_file).getAbsolutePath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;

        mBtnCallToAction.getLayoutParams().width = imgWidth;
        mBtnCallToAction.getLayoutParams().height = imgHeight;

        Placement bottomBanner = PlacementRepository.findByName(Placement.ZONE_BANNER_RIGHT);
        MarginLayoutParams lp = (MarginLayoutParams) mBtnCallToAction.getLayoutParams();
        lp.leftMargin = 0;
        lp.topMargin = 0;
        lp.rightMargin = 0;
        lp.bottomMargin = bottomBanner.getHeight();
        mBtnCallToAction.setLayoutParams(lp);
        mBtnCallToAction.requestLayout();

        mBtnCallToAction.setVisibility(VISIBLE);
        String url = "file://" + cta_file;
        Glide.with(getContext())
                .load(url)
                .crossFade()
                .into(mBtnCallToAction);
    }

    private void callToActionImage(){
        subscription = Observable
                .interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::timerCallToAction);
    }

    private void timerCallToAction(long tick){
        final int duration = TaxiApplication.getSettings().getCallToActionShow();
        if(tick == duration){
            mBtnCallToAction.setVisibility(GONE);
            EBus.post(new EventVideoAdsCompletion());
        }
    }

    @OnClick(R.id.taxivid_btn_calltoaction)
    public void onCallToActionClick(){
        EBus.post(new EventCallToAction(ads));
        mAnalyticsAgent.reportCTAClick(ads.getServerId());
    }

}
