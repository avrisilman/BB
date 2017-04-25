package com.domikado.itaxi.ui.common;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.events.EventInvisible;
import com.domikado.itaxi.events.EventPopupQuizDisappear;
import com.domikado.itaxi.ui.base.BaseFragment;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.KioskUtils;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ControlFragmentPremium extends BaseFragment {

    public final static int FULL = 1;
    public final static int WITHOUT_DISPLAY_CONTROL = 2;

    private static final String MODE = "Extra.MODE";

    @BindView(R.id.seekBarVolume)
    SeekBar mSeekBarVolume;

    @BindView(R.id.seekBarBrightness)
    SeekBar mSeekBarBrightness;

    @BindView(R.id.imgContent)
    ImageView mImgContent;

    @BindView(R.id.btnVolumeDown)
    ImageView btnVolumeDown;

    @BindView(R.id.txtTitle)
    TextView mTxtCountDown;

    private Unbinder unbinder;

    public static ControlFragmentPremium newInstance(int mode) {
        Bundle bundle = new Bundle();
        bundle.putInt(MODE, mode);

        ControlFragmentPremium fragment = new ControlFragmentPremium();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_control_premium;
    }

    @Override
    protected void onSetupView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        initSeekbar();
        mSeekBarBrightness.setMax(10);
        mSeekBarBrightness.setProgress(KioskUtils.getCurrentBrightnessSetting() / 10);
        RxSeekBar.changes(mSeekBarBrightness)
                .compose(bindToLifecycle())
                .map(i -> i * 10)
                .subscribe(integer -> KioskUtils.setBrightness(getActivity().getWindow(), integer));
    }

    private void initSeekbar(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mSeekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mSeekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        RxSeekBar.changes(mSeekBarVolume)
                .compose(bindToLifecycle())
                .subscribe(i -> KioskUtils.setVolume(mContext, i));

        mSeekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(progress <= 1){
                    seekBar.setProgress(1);
                    btnVolumeDown.setEnabled(false);
                }else if(progress > 1){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    btnVolumeDown.setEnabled(true);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int mode = getArguments().getInt(MODE);
        if (mode == WITHOUT_DISPLAY_CONTROL) {
            mImgContent.setVisibility(View.GONE);
            mTxtCountDown.setVisibility(View.GONE);
        } else {
            int time = TaxiApplication.getSettings().getVisibilityOff();
            time = 0;
            startCountDown(time);
        }

    }

    private void startCountDown(int time) {
        if (time > 0) {
            mTxtCountDown.setText(String.valueOf(time));

            Observable.interval(1, TimeUnit.SECONDS)
                    .takeUntil(tick -> tick >= time)
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            aLong -> mTxtCountDown.setText(String.valueOf((int) (time - aLong))),
                            Timber::e,
                            () -> {
                                if (mTxtCountDown != null) {
                                    eyeIconVisible();
                                }
                            });
        } else {
            eyeIconVisible();
        }
    }

    private void eyeIconVisible(){
        ViewCompat.setAlpha(mImgContent, 0f);
        mImgContent.setVisibility(View.GONE);
        ViewCompat.animate(mTxtCountDown).alpha(0f);
        ViewCompat.animate(mImgContent).alpha(1f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.imgContent)
    public void onScreenOffClick() {
        EBus.post(new EventInvisible());
    }

    @OnLongClick(R.id.imgContent)
    public boolean onPopupQuiz() {
        EBus.post(new EventPopupQuizDisappear());
        return true;
    }

    @OnClick({R.id.btnVolumeUp, R.id.btnVolumeDown})
    public void onVolumeAdjust(View view) {
        int value = view.getId() == R.id.btnVolumeUp ? 1 : -1;
        mSeekBarVolume.setProgress(mSeekBarVolume.getProgress() + value);

        if (mSeekBarVolume.getProgress() <= 1) {
            btnVolumeDown.setEnabled(false);
            mSeekBarVolume.setProgress(1);
        } else if (mSeekBarVolume.getProgress() >= 1) {
            btnVolumeDown.setEnabled(true);
            mSeekBarVolume.setProgress(mSeekBarVolume.getProgress());
        }

    }

    @OnClick({R.id.btnBrightnessUp, R.id.btnBrightnessDown})
    public void onBrightnessAdjust(View view) {
        int value = view.getId() == R.id.btnBrightnessUp ? 1 : -1;
        mSeekBarBrightness.setProgress((mSeekBarBrightness.getProgress() + value));
    }

}
