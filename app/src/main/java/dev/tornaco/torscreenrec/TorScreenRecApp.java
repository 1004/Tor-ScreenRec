package dev.tornaco.torscreenrec;

import android.app.Application;

import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.util.FFMpegInstaller;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public class TorScreenRecApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.config(Settings.builder().tag("TorScreenRec").logLevel(Logger.LogLevel.ALL).build());
        SettingsProvider.init(getApplicationContext());
        FFMpegInstaller.installAsync(getApplicationContext());
    }
}
