package dev.tornaco.torscreenrec.pref;

import android.os.Environment;

import java.io.File;

import dev.tornaco.torscreenrec.common.DateUtils;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public enum StorageManager {
    Instance;

    public static StorageManager getInstance() {
        return Instance;
    }

    public String getVideoRootPath() {
        return Environment.getExternalStorageDirectory().getPath()
                + File.separator + "ScreenRecorder";
    }

    public String createVideoFilePath() {
        return getVideoRootPath() + File.separator + DateUtils.formatForFileName(System.currentTimeMillis()) + ".mp4";
    }
}
