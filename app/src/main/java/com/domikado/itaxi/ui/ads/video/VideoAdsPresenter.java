package com.domikado.itaxi.ui.ads.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.data.entity.ads.CallToAction;
import com.domikado.itaxi.data.repository.CTARepository;
import com.domikado.itaxi.data.service.analytics.AnalyticsAgent;
import com.domikado.itaxi.events.EventCallToAction;
import com.domikado.itaxi.events.EventPause;
import com.domikado.itaxi.events.EventUnpause;
import com.domikado.itaxi.utils.EBus;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@SuppressWarnings("ConstantConditions")
public class VideoAdsPresenter extends MvpBasePresenter<VideoAdsView> {

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_DESTROYED = 3;

    private Context mContext;
    private VideoAdsManager mVideoManager;
    private AnalyticsAgent mAnalyticsAgent;
    private Subscription mCATTimerSub;

    private MediaPlayer mediaPlayer;
    private int videoPosition;

    private int mState = STATE_IDLE;
    private long mCurrentAdsId = -1;

    @Inject
    public VideoAdsPresenter(Context c, VideoAdsManager vm, AnalyticsAgent tr) {
        mContext = c;
        mVideoManager = vm;
        mAnalyticsAgent = tr;
    }

    void registerBus() {
        EBus.register(this);
    }
    void unregisterBus() {
        EBus.unregister(this);
    }

    public void setAds(List<Ads> ads) {
        mVideoManager.setAdses(ads);
    }

    @Subscribe
    public void onEvent(EventPause e) {
        getMediaPlayer().setVolume(0f, 0f);
        getMediaPlayer().pause();
        videoPosition = getMediaPlayer().getCurrentPosition();
    }

    @Subscribe
    public void onEvent(EventUnpause e) {
        getMediaPlayer().setVolume(1f, 1f);
        getMediaPlayer().seekTo(videoPosition);
        getMediaPlayer().start();
    }

    private void playForAds(Ads ads) {
        if (ads.getFilepath().isEmpty() || ads.getFilepath() == null) {
            playNextAds();
        } else {
            mAnalyticsAgent.reportAdsStart(ads)
                .subscribe(aLong -> {
                    mCurrentAdsId = aLong;
                });
            playForPath(ads.getFilepath());
        }
    }

    private void playForPath(String path) {
        Timber.d("Playing for path: " + path);
        reset();
        if (isViewAttached()) {
            getView().setVideoViewListener(surface -> {
                getMediaPlayer().setSurface(surface);
                setDataSource(path);
                play();
            });
        }
    }

    private synchronized void play() {
        if (!isViewAttached())
            return;

        mState = STATE_PLAYING;

        getView().showLoading(true);
        getMediaPlayer().setOnPreparedListener(mp -> {
            getView().showLoading(false);

            int width = getMediaPlayer().getVideoWidth();
            int height = getMediaPlayer().getVideoHeight();
            Timber.d("Width:" + width + " height:" + height);
            getView().initLayout(width, height);

            Handler handler = new Handler();
            handler.postDelayed(mp::start, 1000);
        });
        getMediaPlayer().prepareAsync();
    }

    private void continueToNextAds() {
        if (isViewAttached()) {
            getView().showCallToActionPrompt(0);
            playNextAds();
        }
    }

    void playNextAds() {
        if (!mVideoManager.isEmpty())
            playForAds(mVideoManager.getNextAds());
    }

    private void reportAdsFinished() {
        if (mCurrentAdsId > 0)
            mAnalyticsAgent.reportAdsFinished(mCurrentAdsId);
    }

    private void onComplete() {
        reportAdsFinished();

        if (!isViewAttached())
            return;

        if (isStopPlaylist()) {
            getView().onVideoComplete();
            return;
        }

        Ads ads = mVideoManager.getCurrentAds();
        if (ads.haveCallToAction()) {
            CallToAction callToAction = CTARepository.findCTAById(ads.getCallToActionId());
            if (callToAction != null && callToAction.getImageFile() != null) {
                final int duration = TaxiApplication.getSettings().getCallToActionShow();

                getView().setCallToActionImage(callToAction.getImageFile());
                getView().showCallToActionPrompt(duration);

                mCATTimerSub = Observable.interval(1, TimeUnit.SECONDS)
                    .takeUntil(sequence -> sequence > duration)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnTerminate(this::continueToNextAds)
                    .subscribe(aLong -> getView().showCallToActionPrompt((int) (duration - aLong)),
                        Timber::e);
            } else
                playNextAds();
        } else
            playNextAds();
    }

    private boolean isStopPlaylist() {
        if (isViewAttached())
            if (mVideoManager.isLastVideo() && !getView().isLoop()) {
                return true;
            }
        return false;
    }

    private void onLoadError() {
        reportAdsFinished();
        if (isStopPlaylist()) {
            return;
        }
        playNextAds();
    }

    void release() {
        if (isViewAttached()) {
            getView().releaseSurface();
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mState = STATE_DESTROYED;
    }

    private void reset() {
        if (mediaPlayer != null && isPlayingOrPreparing()) {
            mediaPlayer.stop();
            mediaPlayer.reset();

            Timber.d("Resetting Media Player");
        }
        mState = STATE_IDLE;
    }

    private synchronized boolean setDataSource(String videoPath) {
        try {
            getMediaPlayer().setDataSource(mContext, Uri.parse(videoPath));
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

    private MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnErrorListener((mp, i, i1) -> {
                Timber.d("MediaPlayer:" + mp.toString());
                Timber.d("MediaPlayer error:" + i + " what: " + i1);
                Timber.d("State:" + mState);

                onLoadError();
                release();
                return false;
            });
            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> onComplete());
        }
        return mediaPlayer;
    }

    private boolean isPlayingOrPreparing() {
        return mState == STATE_PREPARING || mState == STATE_PLAYING;
    }

    public void showCallToAction() {
        EBus.post(new EventCallToAction(mVideoManager.getCurrentAds()));
        mAnalyticsAgent.reportCTAClick(mVideoManager.getCurrentAds().getServerId());
        mCATTimerSub.unsubscribe();
        continueToNextAds();
    }
}
