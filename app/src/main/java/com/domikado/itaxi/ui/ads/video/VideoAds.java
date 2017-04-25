package com.domikado.itaxi.ui.ads.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.data.entity.ads.Ads;
import com.domikado.itaxi.injection.component.UiComponent;
import com.domikado.itaxi.utils.video.ScalableType;
import com.domikado.itaxi.utils.video.ScaleManager;
import com.domikado.itaxi.utils.video.Size;
import com.hannesdorfmann.mosby.mvp.layout.MvpFrameLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoAds extends MvpFrameLayout<VideoAdsView, VideoAdsPresenter>
    implements TextureView.SurfaceTextureListener, VideoAdsView {

    public interface OnVideoCompleteListener {
        void onComplete();
    }

    @BindView(R.id.taxivid_textureview)
    TextureView mTextureView;

    @BindView(R.id.taxivid_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.taxivid_frame_background)
    View taxividFrameBackground;

    @BindView(R.id.taxivid_cat_prompt)
    View mCatPromptView;

    @BindView(R.id.taxivid_btn_calltoaction)
    ImageView mBtnCallToAction;

    @Inject
    VideoAdsPresenter mPresenter;

    private Surface mSurface;
    private VideoViewListener mVideoViewListener;
    private OnVideoCompleteListener mListener;
    private boolean isLoop = false;

    public VideoAds(Context context) {
        this(context, null);
    }
    public VideoAds(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public VideoAds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode())
            return;

        inflate(getContext(), R.layout.view_video_ads, this);

        ButterKnife.bind(this);
        mTextureView.setSurfaceTextureListener(this);

        UiComponent.Initializer
            .init(TaxiApplication.getComponent())
            .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPresenter.registerBus();
        mPresenter.playNextAds();
    }

    @Override
    protected void onDetachedFromWindow() {
        mPresenter.unregisterBus();
        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public VideoAdsPresenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void releaseSurface() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        releaseSurface();
        mSurface = new Surface(surfaceTexture);
        onVideoViewReady();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mPresenter.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}

    public void onVideoViewReady() {
        if (mVideoViewListener != null) {
            mVideoViewListener.onVideoViewReady(mSurface);
            mVideoViewListener = null;
        }
    }

    @Override
    public void onVideoComplete() {
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    @Override
    public void setVideoViewListener(VideoViewListener listener) {
        mVideoViewListener = listener;
        if (mSurface != null) {
            onVideoViewReady();
        }
    }

    public void setOnVideoCompleteListener(OnVideoCompleteListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void setAds(List<Ads> ads) {
        mPresenter.setAds(ads);
    }

    @Override
    public void showLoading(boolean isShow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.animate().setDuration(shortAnimTime)
                .alpha(isShow ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
                    }
                });

            taxividFrameBackground.setVisibility(View.VISIBLE);
            taxividFrameBackground.animate().setDuration(shortAnimTime)
                .alpha(isShow ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        taxividFrameBackground.setVisibility(isShow ? View.VISIBLE : View.GONE);
                    }
                });
        } else {
            mProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
            findViewById(R.id.taxivid_frame_background).setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void initLayout(int videoWidth, int videoHeight) {
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();

        Size viewSize = new Size(viewWidth, viewHeight);
        Size videoSize = new Size(videoWidth, videoHeight);

        ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
        mTextureView.setTransform(scaleManager.getScaleMatrix(ScalableType.FIT_CENTER));
    }

    @Override
    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        this.isLoop = loop;
    }

    @Override
    public void showCallToActionPrompt(int durationLeft) {
        if (durationLeft > 0) {
            mCatPromptView.setVisibility(View.VISIBLE);
        } else {
            mCatPromptView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCallToActionImage(String imagePath) {
        Glide.with(getContext())
            .load(imagePath)
            .into(mBtnCallToAction);
    }

    @OnClick(R.id.taxivid_btn_calltoaction)
    public void onCallToActionClick() {
        mPresenter.showCallToAction();
    }
}
