package dev.tornaco.torscreenrec.bridge;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;

import com.google.common.io.Files;
import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import dev.nick.library.BridgeManager;
import dev.tornaco.torscreenrec.util.MediaTools;
import dev.tornaco.torscreenrec.util.ThreadUtil;

/**
 * Created by Tornaco on 2017/7/25.
 * Licensed with Apache.
 */

public class Installer {

    public static final String BRIDGE_PACKAGE_NAME = "dev.nick.systemrecapi";

    private static final String SRC_PATH_PLATFORM = "app-release-platform.apk";
    private static final String SRC_PATH_TORNACO = "app-release.apk";
    private static final String TMP_APK_NAME = "tmp.apk";
    private static final String DEST_PATH = "/system/app/RecBridge.apk";
    private static final String DEST_PATH_V2 = "/system/app/RecBridge/RecBridge.apk";

    public interface Callback {
        void onSuccess();

        void onFailure(Throwable throwable, String errTitle);
    }

    public static String prebuiltVersionName() {
        return PrebuiltConfig.VERSION_NAME;
    }

    public static boolean checkForNewVersionFromPrebuilt(Context context) {
        return BridgeManager.getInstance().isInstalled(context) &&
                PrebuiltConfig.VERSION_CODE > BridgeManager.getInstance().getVersionCode(context);
    }

    public static void installWithRootAsync(final Context context, final Callback call) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                installWithRoot(context, call);
            }
        }).start();
    }

    public static void installWithIntentAsync(final Context context, final Callback call) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                installWithIntent(context, call);
            }
        }).start();
    }

    public static void unInstallAsync(final Context context, final Callback call) {
        ThreadUtil.newThread(new Runnable() {
            @Override
            public void run() {
                boolean isPlatform = BridgeManager.getInstance().isInstalledInSystem(context);
                if (isPlatform) {
                    unInstallWithRoot(call);
                } else {
                    unInstallWithIntent(context);
                    call.onSuccess();
                }
            }
        }).start();
    }

    private static void unInstallWithIntent(Context context) {
        Uri packageURI = Uri.parse("package:" + BRIDGE_PACKAGE_NAME);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    private static void installWithRoot(final Context context, final Callback callback) {
        String from = extractFromAssets(context, SRC_PATH_PLATFORM);
        if (from == null) {
            callback.onFailure(new Throwable(), "Copy to tmp fail");
            return;
        }
        installWithRoot(context, from, DEST_PATH_V2, callback);
    }

    private static void installWithIntent(final Context context, Callback callback) {
        String from = extractFromAssets(context, SRC_PATH_TORNACO);
        if (from == null) {
            callback.onFailure(new Throwable(), "Copy to tmp fail");
            return;
        }
        installWithIntent(context, from, callback);
    }

    private static void installWithIntent(Context context, String from, Callback callback) {
        Logger.d("installWithIntent: %s", from);
        context.startActivity(MediaTools.buildInstallIntent(context, new File(from)));
        callback.onSuccess();
    }

    private static String extractFromAssets(Context context, String name) {
        String tmpPath = Files.createTempDir().getPath() + File.separator + TMP_APK_NAME;
        try {
            copy(context, name, tmpPath);
        } catch (IOException e) {
            Logger.e(e, "Copy tmp file fail");
            return null;
        }
        return tmpPath;
    }

    private static void installWithRoot(Context context, String from, String to, Callback callback) {

        if (!RootTools.isRootAvailable()) {
            callback.onFailure(new Throwable(), "Root not available");
            return;
        }

        // Try uninstall old version.
        unInstallWithRoot(DEST_PATH, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable, String errTitle) {

            }
        });

        // Create dir.
        if (!RootTools.mkdir(new File(DEST_PATH_V2).getParent(), true, 755)) {
            callback.onFailure(new Throwable(), "Fail mkdir in system");
            return;
        }

        if (!RootTools.copyFile(from, to, true, true)) {
            callback.onFailure(new Throwable(), "Fail copy to system");
            return;
        }
        callback.onSuccess();
    }

    private static void unInstallWithRoot(Callback callback) {
        unInstallWithRoot(DEST_PATH, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable, String errTitle) {

            }
        });
        unInstallWithRoot(DEST_PATH_V2, callback);
    }

    private static void unInstallWithRoot(String path, Callback callback) {
        boolean ok = RootTools.deleteFileOrDirectory(path, true);
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
