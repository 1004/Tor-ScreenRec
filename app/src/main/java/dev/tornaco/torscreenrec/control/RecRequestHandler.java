package dev.tornaco.torscreenrec.control;

import android.content.Context;
import android.os.RemoteException;

import org.newstand.logger.Logger;

import dev.nick.library.IParam;
import dev.nick.library.RecBridgeServiceProxy;
import dev.nick.library.TokenAdapter;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.pref.StorageManager;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class RecRequestHandler {

    public static boolean start(final Context context) {
        SettingsProvider settingsProvider = SettingsProvider.get();
        try {
            RecBridgeServiceProxy.from(context)
                    .start(IParam.builder()
                                    .audioSource(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE))
                                    .frameRate(settingsProvider.getInt(SettingsProvider.Key.FAME_RATE))
                                    .orientation(settingsProvider.getInt(SettingsProvider.Key.ORIENTATION))
                                    .resolution(settingsProvider.getString(SettingsProvider.Key.RESOLUTION))
                                    .stopOnScreenOff(settingsProvider.getBoolean(SettingsProvider.Key.SCREEN_OFF_STOP))
                                    .useMediaProjection(settingsProvider.getBoolean(SettingsProvider.Key.USER_PROJECTION))
                                    .stopOnShake(settingsProvider.getBoolean(SettingsProvider.Key.SHAKE_STOP))
                                    .shutterSound(settingsProvider.getBoolean(SettingsProvider.Key.SHUTTER_SOUND))
                                    .path(StorageManager.getInstance().createVideoFilePath())
                                    .showNotification(true)
                                    .build(),

                            new TokenAdapter() {
                                @Override
                                public String getDescription() throws RemoteException {
                                    return context.getString(R.string.recorder_description);
                                }
                            });
        } catch (RemoteException e) {
            Logger.e(e, "Fail start");
            return false;
        }
        return true;
    }

    public static boolean stop(Context context){
        try {
            RecBridgeServiceProxy.from(context).stop();
        } catch (RemoteException e) {
            Logger.e(e, "Fail stop");
            return false;
        }
        return true;
    }
}
