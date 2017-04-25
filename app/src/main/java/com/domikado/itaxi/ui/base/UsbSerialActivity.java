package com.domikado.itaxi.ui.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.domikado.itaxi.Constant;
import com.domikado.itaxi.data.taximeter.Taximeter;
import com.domikado.itaxi.data.taximeter.UsbService;

import java.lang.ref.WeakReference;
import java.util.Set;

public class UsbSerialActivity extends BaseActivity {

    private UsbService usbService;
    private UsbSerialHandler mHandler;
    private Taximeter taximeter;

    private final ServiceConnection usbConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.ACTION_USB_READY:
                    Toast.makeText(context, "USB ready", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.ACTION_NO_USB:
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.ACTION_USB_DISCONNECTED:
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.ACTION_USB_NOT_SUPPORTED:
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.ACTION_USB_DEVICE_NOT_WORKING:
                    Toast.makeText(context, "USB device not working", Toast.LENGTH_SHORT).show();
                    break;
                case Constant.ACTION_USB_SERIAL_READ:
                    byte[] data = intent.getByteArrayExtra(Constant.EXTRA_PARAM_USB_SERIAL_READ);
                    taximeter.process(data);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        mHandler = new UsbSerialHandler(this);
        taximeter = new Taximeter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(UsbService.class, usbConnection, null);
        registerReceiver(mUsbReceiver);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
        super.onPause();
    }

    private void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_USB_READY);
        filter.addAction(Constant.ACTION_NO_USB);
        filter.addAction(Constant.ACTION_USB_DISCONNECTED);
        filter.addAction(Constant.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(Constant.ACTION_USB_SERIAL_READ);

        registerReceiver(receiver, filter);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private static class UsbSerialHandler extends Handler {
        private final WeakReference<UsbSerialActivity> mActivity;

        UsbSerialHandler(UsbSerialActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    byte[] data = (byte[]) msg.obj;
                    Intent intent = new Intent(Constant.ACTION_USB_SERIAL_READ);
                    intent.putExtra(Constant.EXTRA_PARAM_USB_SERIAL_READ, data);
                    mActivity.get().sendBroadcast(intent);
                    break;
            }
        }
    }
}
