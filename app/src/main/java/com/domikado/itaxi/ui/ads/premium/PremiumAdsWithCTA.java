package com.domikado.itaxi.ui.ads.premium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventVideoCompletion;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.ui.ads.cta.JSInterface;
import com.domikado.itaxi.ui.ads.cta.SubmitJsInterface;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.video.ScalableType;
import com.domikado.itaxi.utils.video.ScaleManager;
import com.domikado.itaxi.utils.video.Size;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class PremiumAdsWithCTA extends LinearLayout {

    @BindView(R.id.webView)
    WebView mwebView;

    @BindView(R.id.texture_video)
    TextureView textureView;

    @Inject
    AnalyticsAgent mAnalyticsAgent;

    @BindView(R.id.ads_premium)
    ImageView adsPremium;

    private MediaPlayer mediaPlayer;
    private Surface mSurface;
    private Unbinder unbinder;
    private Ads ads;
    private CallToAction cta;
    private long metricsId = -1;

    public PremiumAdsWithCTA(Context context) {
        super(context);

        inflate(context, R.layout.view_premium_video_cta, this);
        ButterKnife.bind(this);

        UiComponent.Initializer
            .init(TaxiApplication.getComponent())
            .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
        setResolution();
        setImage();
        webView();

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
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void webView(){
        SubmitJsInterface jsInterfce = new SubmitJsInterface(ads.getId());
        jsInterfce.setOnSubmitListener(isSuccess -> ctaResultSubmit());

        mwebView.clearCache(true);
        mwebView.getSettings().setSaveFormData(false);
        mwebView.addJavascriptInterface(jsInterfce, JSInterface.NAME);
        mwebView.getSettings().setJavaScriptEnabled(true);
        mwebView.setWebViewClient(new WebViewClient());
        String url = "file://" + CTARepository.findCTAById(ads.getCallToActionId()).getFile();
        mwebView.loadUrl(url);
    }

    private void ctaResultSubmit() {
        Log.e("result", "ctaResultSubmit");
    }

    private void setImage(){
        Glide.with(getContext())
                .load(ads.getBannerpath())
                .crossFade()
                .into(adsPremium);
    }

    public void setAds(Ads ads) {
        this.ads = ads;
        setDataSource(ads.getFilepath());
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
          //  mp.start();
            Handler handler = new Handler();
            handler.postDelayed(mp::start, 1000);
        });
        getMediaPlayer().prepareAsync();
    }

    public void onComplete() {
        mAnalyticsAgent.reportAdsFinished(metricsId);
        EBus.post(new EventVideoCompletion());
    }

    public void initLayout(int videoWidth, int videoHeight) {
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();

        Size viewSize = new Size(viewWidth, viewHeight);
        Size videoSize = new Size(videoWidth, videoHeight);

        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        textureView.setTransform(scaleManager.getScaleMatrix(ScalableType.FIT_CENTER));
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

    private synchronized boolean setDataSource(String videoPath) {
        try {
            getMediaPlayer().setDataSource(getContext(), Uri.parse(videoPath));
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
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

    private void setResolution() {
        textureView.getLayoutParams().width = 1152;
        textureView.getLayoutParams().height = 864;
        textureView.requestLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

}
