package dev.tornaco.torscreenrec.control;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import dev.tornaco.torscreenrec.TorScreenRecApp;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.ui.widget.FloatView;
import ezy.assist.compat.SettingsCompat;

/**
 * Created by Nick on 2017/6/28 14:43
 */

public class FloatingControlService extends Service implements FloatingController {

    private FloatView floatView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Stub();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatView = new FloatView((TorScreenRecApp) getApplication());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TorScreenRecApp app = (TorScreenRecApp) getApplication();

        if (SettingsCompat.canDrawOverlays(app.getTopActivity())) {
            show();
        } else {
            try {
                SettingsCompat.manageDrawOverlays(app.getTopActivity());
            } catch (Throwable e) {
                Toast.makeText(getApplicationContext(), Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            }

            SettingsProvider.get().putBoolean(SettingsProvider.Key.FLOAT_WINDOW, false);
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hide();
    }

    @Override
    public void show() {
        floatView.attach();
    }

    @Override
    public void hide() {
        floatView.detach();
    }

    @Override
    public boolean isShowing() {
        return floatView.isAttachedToWindow();
    }

    private class Stub extends Binder implements FloatingController {

        @Override
        public void show() {
            FloatingControlService.this.show();
        }

        @Override
        public void hide() {
            FloatingControlService.this.hide();
        }

        @Override
        public boolean isShowing() {
            return FloatingControlService.this.isShowing();
        }
    }
}
