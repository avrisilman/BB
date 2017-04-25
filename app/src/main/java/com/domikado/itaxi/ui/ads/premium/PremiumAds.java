package com.domikado.itaxi.ui.ads.premium;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;

import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventVideoCompletion;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.video.ScalableType;
import com.domikado.itaxi.utils.video.ScaleManager;
import com.domikado.itaxi.utils.video.Size;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class PremiumAds extends LinearLayout {

    @BindView(R.id.texture_video)
    TextureView textureView;

    @Inject
    AnalyticsAgent mAnalyticsAgent;

    private MediaPlayer mediaPlayer;
    private Surface mSurface;
    private Unbinder unbinder;
    private Ads ads;
    private long metricsId = -1;

    public PremiumAds(Context context) {
        super(context);
        inflate(context, R.layout.view_premium_video, this);

        UiComponent.Initializer
            .init(TaxiApplication.getComponent())
            .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    public void setAds(Ads ads) {
        this.ads = ads;
        setDataSource(ads.getFilepath());
    }

    public void onComplete() {
        mAnalyticsAgent.reportAdsFinished(metricsId);
        EBus.post(new EventVideoCompletion());
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
           // mp.start();
            Handler handler = new Handler();
            handler.postDelayed(mp::start, 1000);
        });
        getMediaPlayer().prepareAsync();
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
}
