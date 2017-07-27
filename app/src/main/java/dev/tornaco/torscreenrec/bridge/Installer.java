package dev.tornaco.torscreenrec.bridge;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.common.io.Files;
import com.stericson.rootools.RootTools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tornaco on 2017/7/25.
 * Licensed with Apache.
 */

public class Installer {

    private static final String PATH = "app-debug-signed.apk";
    private static final String TMP_APK_NAME = "tmp.apk";
    private static final String DEST_PATH = "/system/app/RecBridge.apk";

    public interface Callback {
        void onSuccess();

        void onFailure(Throwable throwable, String errTitle);
    }

    public static void installAsync(final Context context, final Callback call) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                install(context, call);
            }
        }).start();
    }

    public static void unInstallAsync(final Callback call) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unInstall(call);
            }
        }).start();
    }

    public static void install(Context context, Callback callback) {

        if (!RootTools.isRootAvailable()) {
            callback.onFailure(new Throwable(), "Root not available");
            return;
        }

        String tmpPath = Files.createTempDir().getPath() + File.separator + TMP_APK_NAME;
        try {
            copy(context, PATH, tmpPath);
        } catch (IOException e) {
            callback.onFailure(e, "Copy tmp file fail");
            return;
        }

        if (!RootTools.copyFile(tmpPath, DEST_PATH, true, true)) {
            callback.onFailure(new Throwable(), "Fail copy to system");
            return;
        }
        callback.onSuccess();
    }

    public static void unInstall(Callback callback) {
        boolean ok = RootTools.deleteFileOrDirectory(DEST_PATH, true);
        if (ok) {
            callback.onSuccess();
        } else {
            callback.onFailure(new Throwable(), "Fail delete file");
        }
    }

    private static AssetManager openAssets(Context context) {
        return context.getAssets();
    }

    private static InputStream openInput(Context context, String path) throws IOException {
        return openAssets(context).open(path);
    }

    private static void copy(Context context, String from, String to) throws IOException {
        Files.createParentDirs(new File(to));
        if (Files.asByteSink(new File(to)).writeFrom(openInput(context, from)) <= 0) {
            throw new IOException("Copy assets file fail");
        }
    }
}
