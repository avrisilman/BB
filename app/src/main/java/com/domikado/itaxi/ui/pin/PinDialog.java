package com.domikado.itaxi.ui.pin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.domikado.itaxi.R;
import com.domikado.itaxi.TaxiApplication;
import com.domikado.itaxi.ui.base.BaseFragmentDialog;
import com.domikado.itaxi.ui.settings.SettingPanelActivity;
import com.domikado.itaxi.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PinDialog extends BaseFragmentDialog {

    @BindView(R.id.dialog_pin_input)
    EditText mCodeInput;

    private PinDialogListener mPinDialogListener;
    private Unbinder unbinder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PinDialogListener)
            mPinDialogListener = (PinDialogListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, getTheme());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getContentView());
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContentView().setOnSystemUiVisibilityChangeListener(visibility ->
            getContentView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        ));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mCodeInput.requestFocus();
        CommonUtils.hideSoftKeyboard(mCodeInput);
        super.onDismiss(dialog);
    }

    @SuppressLint("InflateParams")
    private View getContentView() {
        View view = mInflater.inflate(R.layout.dialog_pin, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.dialog_pin_submit)
    public void onSubmitClick() {
        if (mCodeInput.getText().toString().equalsIgnoreCase(TaxiApplication.getSettings().getLockPass())) {
            SettingPanelActivity.start(mContext);
            dismissAllowingStateLoss();

            if (mPinDialogListener != null)
                mPinDialogListener.onPinOpened();
        }
    }

    public static void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
            .add(new PinDialog(), PinDialog.class.getSimpleName())
            .commitAllowingStateLoss();
    }
}

