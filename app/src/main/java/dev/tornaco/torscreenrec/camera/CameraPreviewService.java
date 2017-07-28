package dev.tornaco.torscreenrec.camera;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.TorScreenRecApp;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import ezy.assist.compat.SettingsCompat;
import io.reactivex.functions.Consumer;

public class CameraPreviewService extends Service {

    private static final long WINDOW_FADE_TIME = 5 * 1000;

    private View mFloatView;
    private WindowManager mWindowManager;
    private LayoutParams mFloatContainerParams;
    private AutoFadeLayout mFloatViewContainer;

    private WindowSize mSize;

    private ServiceBinder mBinder;

    private OnTouchListener mFloatViewTouchListener = new OnTouchListener() {

        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = mFloatContainerParams.x;
                    initialY = mFloatContainerParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    mFloatViewContainer.stopFading();
                    return true;
                case MotionEvent.ACTION_UP:
                    mWindowManager.updateViewLayout(mFloatViewContainer,
                            mFloatContainerParams);
                    mFloatViewContainer.startFading(WINDOW_FADE_TIME);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int diffX = (int) (event.getRawX() - initialTouchX);
                    int diffY = (int) (event.getRawY() - initialTouchY);
                    mFloatContainerParams.x = initialX + diffX;
                    mFloatContainerParams.y = initialY + diffY;
                    mWindowManager.updateViewLayout(mFloatViewContainer,
                            mFloatContainerParams);
                    return true;
            }
            return false;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) mBinder = new ServiceBinder();
        return mBinder;
    }

    public void showPreview(WindowSize size) {
        if (isShowing()) {
            return;
        }

        // Check settings.
        TorScreenRecApp app = (TorScreenRecApp) getApplication();

        if (SettingsCompat.canDrawOverlays(app.getTopActivity())) {
            // EMPTY.
        } else {
            try {
                SettingsCompat.manageDrawOverlays(app.getTopActivity());
            } catch (Throwable e) {
                Toast.makeText(getApplicationContext(), Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            }

            SettingsProvider.get().putBoolean(SettingsProvider.Key.CAMERA, false);
            return;
        }

        mSize = size;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mFloatView = new SoftwareCameraPreview(this);
        mFloatContainerParams = new LayoutParams(
                mSize.w,
                mSize.h,
                LayoutParams.TYPE_SYSTEM_ALERT,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mFloatContainerParams.y = 0;
        mFloatContainerParams.x = 0;
        mFloatViewContainer = (AutoFadeLayout) LayoutInflater.from(this).inflate(R.layout.float_containor, null);
        mFloatViewContainer.setOnTouchListener(mFloatViewTouchListener);
        mFloatViewContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mWindowManager.addView(mFloatViewContainer, mFloatContainerParams);
        mFloatViewContainer.addView(mFloatView);
        mFloatViewContainer.startFading(WINDOW_FADE_TIME);
    }

    private void hidePreview() {
        if (isShowing()) {
            mWindowManager.removeView(mFloatViewContainer);
        }
    }

    private boolean isShowing() {
        return mFloatViewContainer != null && mFloatViewContainer.isAttachedToWindow() && mFloatView.isAttachedToWindow();
    }

    public void setSize(WindowSize size) {
        this.mSize = size;
        if (isShowing()) {
            mFloatContainerParams.width = size.w;
            mFloatContainerParams.height = size.h;
            mWindowManager.updateViewLayout(mFloatViewContainer,
                    mFloatContainerParams);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mFloatView != null) {
            mWindowManager.removeView(mFloatView);
        }
        super.onDestroy();
    }

    private static class WindowSize {

        static WindowSize DEFAULT = new WindowSize(320, 480);
        static WindowSize LARGE = new WindowSize(480, 720);
        static WindowSize SMALL = new WindowSize(240, 360);

        int w, h;

        WindowSize(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public String toString() {
            return "WindowSize{" +
                    "w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    private class ServiceBinder extends Binder implements ICameraPreviewService {

        @Override
        public void show(int sizeIndex) {
            WindowSize size;
            switch (sizeIndex) {
                case PreviewSize.LARGE:
                    size = WindowSize.LARGE;
                    break;
                case PreviewSize.SMALL:
                    size = WindowSize.SMALL;
                    break;
                case PreviewSize.NORMAL:
                    size = WindowSize.DEFAULT;
                    break;
                default:
                    throw new IllegalArgumentException("Bad size index:" + sizeIndex);
            }

            showPreviewChecked(size);
        }

        private void showPreviewChecked(final WindowSize size) {
            RxPermissions rxPermissions = new RxPermissions(TorScreenRecApp.getApp().getTopActivity());
            rxPermissions.request(Manifest.permission.CAMERA)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                CameraPreviewService.this.showPreview(size);
                            } else {
                                SettingsProvider.get().putBoolean(SettingsProvider.Key.CAMERA, false);
                            }
                        }
                    });
        }

        @Override
        public void hide() {
            CameraPreviewService.this.hidePreview();
        }

        @Override
        public boolean isShowing() {
            return CameraPreviewService.this.isShowing();
        }

        @Override
        public void setSize(int sizeIndex) {
            WindowSize size;
            switch (sizeIndex) {
                case PreviewSize.LARGE:
                    size = WindowSize.LARGE;
                    break;
                case PreviewSize.SMALL:
                    size = WindowSize.SMALL;
                    break;
                case PreviewSize.NORMAL:
                    size = WindowSize.DEFAULT;
                    break;
                default:
                    throw new IllegalArgumentException("Bad size index:" + sizeIndex);
            }
            CameraPreviewService.this.setSize(size);
        }
    }
}
