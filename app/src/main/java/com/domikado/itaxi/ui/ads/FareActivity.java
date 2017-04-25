package com.domikado.itaxi.ui.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import com.domikado.itaxi.BuildConfig;
import com.domikado.itaxi.R;
import com.domikado.itaxi.data.SPSession;
import com.domikado.itaxi.data.repository.AnalyticsRepository;
import com.domikado.itaxi.data.repository.SessionRepository;
import com.domikado.itaxi.events.EventHire;
import com.domikado.itaxi.events.EventVacant;
import com.domikado.itaxi.ui.base.KioskActivity;
import com.domikado.itaxi.utils.EBus;
import com.domikado.itaxi.utils.StringUtil;
import org.greenrobot.eventbus.Subscribe;
import java.math.BigDecimal;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FareActivity extends KioskActivity {

    private static final String INTENT_FARE_PARAM = "com.domikado.itaxi.INTENT_FARE_PARAM";

    Context mContext;

    @BindView(R.id.tv_fare)
    TextView mTvFare;

    @BindView(R.id.tv_mode)
    TextView mTvMode;

    @BindView(R.id.ratingBar)
    RatingBar mRatingbar;

    public static void start(Context context, BigDecimal param) {
        Intent intent = new Intent(context, FareActivity.class);
        intent.putExtra(INTENT_FARE_PARAM, param);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fare);
        ButterKnife.bind(this);

        if(BuildConfig.SHOW_METER_CONTROL){
            mTvMode.setVisibility(View.VISIBLE);
        }

        BigDecimal mFareAmount = (BigDecimal) getIntent().getSerializableExtra(INTENT_FARE_PARAM);
        mTvFare.setText(StringUtil.rupiah(mFareAmount));

        SessionRepository.endSession(SPSession.getSessionId(getApplicationContext()), mFareAmount.intValue());
        logScreen();

        mRatingbar.setOnRatingBarChangeListener(this::sendRatingBar);

    }

    private void sendRatingBar(RatingBar ratingBar, float rating, boolean b) {
        if(mRatingbar.isClickable()){
            mRatingbar.setFocusable(false);
        }else {
            AnalyticsRepository.recordRating(this, String.valueOf(rating));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EBus.register(this);
    }

    @Override
    protected void onPause() {
        EBus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onEvent(EventVacant e) {
        navigateToVacant();
    }

    @Subscribe
    public void onEvent(EventHire e) {
        navigateToMain();
    }

    @OnClick(R.id.tv_mode)
    public void onTvModeClick() {
        navigateToVacant();
    }
}
