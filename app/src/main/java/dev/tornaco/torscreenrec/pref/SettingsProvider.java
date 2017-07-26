package dev.tornaco.torscreenrec.pref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Observable;

import dev.nick.library.AudioSource;
import dev.nick.library.Orientations;
import dev.nick.library.ValidResolutions;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tornaco on 2017/7/25.
 * Licensed with Apache.
 */
@AllArgsConstructor
public class SettingsProvider extends Observable {

    private static final String PREF_NAME = "rec_app_settings";

    public enum Key {
        USR_NAME("Fake.Name"),
        AUDIO_SOURCE(AudioSource.NOOP),
        SHUTTER_SOUND(false),
        SHAKE_STOP(false),
        SCREEN_OFF_STOP(false),
        FAME_RATE(30),
        RESOLUTION(ValidResolutions.DESC[ValidResolutions.INDEX_MASK_AUTO]),
        ORIENTATION(Orientations.AUTO),
        USER_PROJECTION(false);

        @Getter
        Object defValue;

        Key(Object defValue) {
            this.defValue = defValue;
        }
    }

    @Getter
    private Context context;

    private SharedPreferences getPref() {
        return getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String toPrefKey(Key key) {
        return key.name().toLowerCase();
    }

    public boolean getBoolean(Key key) {
        return getPref().getBoolean(toPrefKey(key), (Boolean) key.getDefValue());
    }

    public void putBoolean(Key key, boolean value) {
        getPref().edit().putBoolean(toPrefKey(key), value).apply();
    }

    public int getInt(Key key) {
        return getPref().getInt(toPrefKey(key), (Integer) key.getDefValue());
    }

    public void putInt(Key key, int value) {
        getPref().edit().putInt(toPrefKey(key), value).apply();
    }

    public String getString(Key key) {
        return getPref().getString(toPrefKey(key), (String) key.getDefValue());
    }

    public void putBoolean(Key key, String value) {
        getPref().edit().putString(toPrefKey(key), value).apply();
    }
}
