package dev.tornaco.torscreenrec;

import android.app.Application;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.nick.library.IWatcher;
import dev.nick.library.RecBridgeServiceProxy;
import dev.nick.library.WatcherAdapter;
import dev.tornaco.torscreenrec.common.Collections;
import dev.tornaco.torscreenrec.common.Consumer;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.util.FFMpegInstaller;
import dev.tornaco.torscreenrec.util.ThreadUtil;
import lombok.experimental.Delegate;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public class TorScreenRecApp extends Application {

    @Delegate
    private WatcherProxy watcherProxy;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.config(Settings.builder().tag("TorScreenRec").logLevel(Logger.LogLevel.ALL).build());
        SettingsProvider.init(getApplicationContext());
        watcherProxy = new WatcherProxy();
        FFMpegInstaller.installAsync(getApplicationContext());
    }

    private class WatcherProxy extends WatcherAdapter {

        final List<IWatcher> watchers = Lists.newArrayList();

        AtomicBoolean ready;

        WatcherProxy() {
            try {
                RecBridgeServiceProxy.from(getApplicationContext())
                        .watch(this);
                ready = new AtomicBoolean(true);
            } catch (RemoteException e) {
                Logger.e(e, "Fail watch");
                ready = new AtomicBoolean(false);
            }
        }

        public void waitForReady() {
            while (ready == null || !ready.get()) {
                ThreadUtil.sleep(100);
            }
        }

        public void watch(IWatcher watcher) {
            synchronized (watchers) {
                watchers.remove(watcher);
                watchers.add(watcher);
            }
        }

        public void unWatch(IWatcher watcher) {
            synchronized (watchers) {
                watchers.remove(watcher);
            }
        }

        @Override
        public void onStart() throws RemoteException {
            super.onStart();

            synchronized (watchers) {
                Collections.consumeRemaining(Lists.newArrayList(watchers),
                        new Consumer<IWatcher>() {
                            @Override
                            public void accept(@NonNull final IWatcher iWatcher) {
                                ThreadUtil.getMainThreadHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            iWatcher.onStart();
                                        } catch (RemoteException e) {
                                            Logger.e(e, "WatcherProxy: Error call onStart");
                                        }
                                    }
                                });
                            }
                        });
            }
        }

        @Override
        public void onStop() throws RemoteException {
            super.onStop();

            synchronized (watchers) {
                Collections.consumeRemaining(Lists.newArrayList(watchers),
                        new Consumer<IWatcher>() {
                            @Override
                            public void accept(@NonNull final IWatcher iWatcher) {
                                ThreadUtil.getMainThreadHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            iWatcher.onStop();
                                        } catch (RemoteException e) {
                                            Logger.e(e, "WatcherProxy: Error call onStop");
                                        }
                                    }
                                });
                            }
                        });
            }
        }

        @Override
        public void onElapsedTimeChange(final String formatedTime) throws RemoteException {
            super.onElapsedTimeChange(formatedTime);

            Collections.consumeRemaining(Lists.newArrayList(watchers), new Consumer<IWatcher>() {
                @Override
                public void accept(@NonNull final IWatcher iWatcher) {
                    ThreadUtil.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                iWatcher.onElapsedTimeChange(formatedTime);
                            } catch (RemoteException e) {
                                Logger.e(e, "WatcherProxy: Error call onElapsedTimeChange");
                            }
                        }
                    });
                }
            });
        }
    }

}
