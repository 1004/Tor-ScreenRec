package dev.tornaco.torscreenrec.control;

import android.content.Context;
import android.os.RemoteException;

import org.newstand.logger.Logger;

import dev.nick.library.BridgeManager;
import dev.nick.library.IParam;
import dev.nick.library.RecBridgeServiceProxy;
import dev.nick.library.TokenAdapter;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class RecRequestHandler {

    public static boolean start(final Context context) {
        boolean isPlatformBridge = BridgeManager.getInstance().isInstalledInSystem(context);
        SettingsProvider settingsProvider = SettingsProvider.get();
        try {
            RecBridgeServiceProxy.from(context)
                    .start(IParam.builder()
                                    .audioSource(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE))
                                    .frameRate(settingsProvider.getInt(SettingsProvider.Key.FAME_RATE))
                                    .orientation(settingsProvider.getInt(SettingsProvider.Key.ORIENTATION))
                                    .resolution(settingsProvider.getString(SettingsProvider.Key.RESOLUTION))
                                    .stopOnScreenOff(settingsProvider.getBoolean(SettingsProvider.Key.SCREEN_OFF_STOP))
                                    .useMediaProjection(!isPlatformBridge)
                                    .stopOnShake(settingsProvider.getBoolean(SettingsProvider.Key.SHAKE_STOP))
                                    .shutterSound(settingsProvider.getBoolean(SettingsProvider.Key.SHUTTER_SOUND))
                                    .path(SettingsProvider.get().createVideoFilePath())
                                    .showNotification(true)
                                    .build(),

                            new TokenAdapter() {
                                @Override
                                public String getDescription() throws RemoteException {
                                    return null; // No need?
                                }
                            });
        } catch (RemoteException e) {
            Logger.e(e, "Fail start");
            return false;
        }
        return true;
    }

    public static boolean stop(Context context) {
        try {
            RecBridgeServiceProxy.from(context).stop();
        } catch (RemoteException e) {
            Logger.e(e, "Fail stop");
            return false;
        }
        return true;
    }
}
