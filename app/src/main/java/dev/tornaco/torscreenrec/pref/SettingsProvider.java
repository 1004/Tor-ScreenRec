package dev.tornaco.torscreenrec.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;

import java.io.File;
import java.util.Observable;

import dev.nick.library.AudioSource;
import dev.nick.library.Orientations;
import dev.nick.library.ValidResolutions;
import dev.tornaco.torscreenrec.camera.PreviewSize;
import dev.tornaco.torscreenrec.common.DateUtils;
import dev.tornaco.torscreenrec.control.FloatControlTheme;
import lombok.Getter;

/**
 * Created by Tornaco on 2017/7/25.
 * Licensed with Apache.
 */
public class SettingsProvider extends Observable {

    private static SettingsProvider sMe;

    private static final String PREF_NAME = "rec_app_settings";

    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;

    public enum Key {
        USR_NAME("Fake.Name"),
        PAID(false),
        FIRST_RUN(true),
        VIDEO_ROOT_PATH(getDefaultVideoRootPath()),
        AUDIO_SOURCE(AudioSource.NOOP),
        WITH_AUDIO(false),
        SHUTTER_SOUND(false),
        SHAKE_STOP(false),
        VOLUME_STOP(false),
        SCREEN_OFF_STOP(false),
        SHOW_TOUCH(false),
        FLOAT_WINDOW(false),
        FLOAT_WINDOW_ALPHA(50),
        FLOAT_WINDOW_THEME(FloatControlTheme.DefaultDark.name()),
        FAME_RATE(30),
        RESOLUTION(ValidResolutions.DESC[ValidResolutions.INDEX_MASK_AUTO]),
        ORIENTATION(Orientations.AUTO),
        USER_PROJECTION(false),
        CAMERA(false),
        CAMERA_SIZE(PreviewSize.SMALL),
        PREFERRED_CAMERA(CAMERA_FACING_FRONT);

        @Getter
        Object defValue;

        Key(Object defValue) {
            this.defValue = defValue;
        }
    }

    @Getter
    private SharedPreferences pref;

    public static SettingsProvider get() {
        return sMe;
    }

    private SettingsProvider(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        sMe = new SettingsProvider(context);
    }

    public String toPrefKey(Key key) {
        return key.name().toLowerCase();
    }

    public boolean getBoolean(Key key) {
        return getPref().getBoolean(toPrefKey(key), (Boolean) key.getDefValue());
    }

    public void putBoolean(Key key, boolean value) {
        getPref().edit().putBoolean(toPrefKey(key), value).apply();
        setChanged();
        notifyObservers(key);
    }

    public int getInt(Key key) {
        return getPref().getInt(toPrefKey(key), (Integer) key.getDefValue());
    }

    public void putInt(Key key, int value) {
        getPref().edit().putInt(toPrefKey(key), value).apply();
        setChanged();
        notifyObservers(key);
    }

    public String getString(Key key) {
        return getPref().getString(toPrefKey(key), (String) key.getDefValue());
    }

    public void putString(Key key, String value) {
        getPref().edit().putString(toPrefKey(key), value).apply();
        setChanged();
        notifyObservers(key);
    }

    public static String getDefaultVideoRootPath() {
        return Environment.getExternalStorageDirectory().getPath()
                + File.separator + "ScreenRecorder";
    }

    public String createVideoFilePath() {
        return getString(Key.VIDEO_ROOT_PATH)
                + File.separator
                + DateUtils.formatForFileName(System.currentTimeMillis()) + ".mp4";
    }

    public static final int REQUEST_CODE_FILE_PICKER = 0x100;
}
