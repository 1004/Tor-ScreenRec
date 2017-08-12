package dev.tornaco.torscreenrec;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.common.collect.Lists;
import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.nick.library.IWatcher;
import dev.nick.library.RecBridgeServiceProxy;
import dev.nick.library.WatcherAdapter;
import dev.tornaco.torscreenrec.bridge.Installer;
import dev.tornaco.torscreenrec.common.Collections;
import dev.tornaco.torscreenrec.common.Consumer;
import dev.tornaco.torscreenrec.control.FloatingControllerServiceProxy;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.util.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public class TorScreenRecApp extends Application {

    @Delegate
    private WatcherProxy watcherProxy;

    @Delegate
    private LifeCycleHandler lifeCycleHandler;

    private FloatViewHandler floatViewHandler;

    @Getter
    private static TorScreenRecApp App;

    @Override
    public void onCreate() {
        super.onCreate();
        App = this;
        Logger.config(Settings.builder().tag("TorScreenRec").logLevel(Logger.LogLevel.ALL).build());
        SettingsProvider.init(getApplicationContext());
        watcherProxy = new WatcherProxy();
        lifeCycleHandler = new LifeCycleHandler();
        checkForUpdate();
    }


    // FIXME Impl is too ugly!!!!!!!!
    private void checkForUpdate() {
        new Handler().
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean prebuiltUpdateAvailable = Installer.checkForNewVersionFromPrebuilt(getApplicationContext());
                        if (prebuiltUpdateAvailable)
                            onPrebuiltUpdateAvailable(Installer.prebuiltVersionName());
                    }
                }, 3000);
    }

    private void onPrebuiltUpdateAvailable(String versionName) {
        if (getTopActivity() == null) return;
        AlertDialog alertDialog = new AlertDialog.Builder(getTopActivity())
                .setTitle(R.string.update_available)
                .setMessage(getString(R.string.update_available_message, versionName))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onRequestUpdate();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    private void onRequestUpdate() {
        final ProgressDialog p = new ProgressDialog(getTopActivity());
        p.setIndeterminate(true);
        p.setMessage(getString(R.string.installing));
        p.setCancelable(false);
        p.show();
        Installer.unInstallAsync(getApplicationContext(), new Installer.Callback() {
            @Override
            public void onSuccess() {
                Installer.installWithRootAsync(getApplicationContext(), new Installer.Callback() {
                    @Override
                    public void onSuccess() {
                        p.dismiss();
                        Snackbar.make(getTopActivity().getWindow().getDecorView(),
                                R.string.install_success, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.restart, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        RootTools.restartAndroid();
                                    }
                                }).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable, String errTitle) {
                        p.dismiss();
                        Snackbar.make(getTopActivity().getWindow().getDecorView(), getString(R.string.install_fail, errTitle),
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.report, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable, String errTitle) {
                Snackbar.make(getTopActivity().getWindow().getDecorView(),
                        R.string.uninstall_fail, Snackbar.LENGTH_INDEFINITE).show();
                p.dismiss();
            }
        });
    }

    private synchronized void setupFloatView() {
        if (floatViewHandler == null) {
            floatViewHandler = new FloatViewHandler();
            floatViewHandler.listen();
        }
    }

    private class FloatViewHandler {

        void listen() {
            if (SettingsProvider.get().getBoolean(SettingsProvider.Key.FLOAT_WINDOW)) {
                new FloatingControllerServiceProxy(getApplicationContext())
                        .start(getApplicationContext());
            }

            SettingsProvider.get().addObserver(new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    if (o == SettingsProvider.Key.FLOAT_WINDOW) {
                        boolean show = SettingsProvider.get().getBoolean(SettingsProvider.Key.FLOAT_WINDOW);
                        if (show) {
                            new FloatingControllerServiceProxy(getApplicationContext()).start(getApplicationContext());
                        } else {
                            new FloatingControllerServiceProxy(getApplicationContext()).stop(getApplicationContext());
                        }
                    }
                }
            });
        }
    }

    private class LifeCycleHandler {

        @Getter
        @Setter
        Activity topActivity;

        LifeCycleHandler() {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    setTopActivity(activity);
                    setupFloatView();
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    SettingsProvider.get().putBoolean(SettingsProvider.Key.FIRST_RUN, false);
                }
            });
        }
    }

    private class WatcherProxy extends WatcherAdapter {

        final List<IWatcher> watchers = Lists.newArrayList();

        AtomicBoolean ready;

        @Getter
        AtomicBoolean recording = new AtomicBoolean(false);

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

            // Send sticky event.
            if (recording.get()) {
                try {
                    watcher.onStart();
                } catch (RemoteException e) {
                    Logger.e(e, "WatcherProxy: Error call onStart");
                }
            } else {
                try {
                    watcher.onStop();
                } catch (RemoteException e) {
                    Logger.e(e, "WatcherProxy: Error call onStop");
                }
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

            recording.set(true);

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

            recording.set(false);

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
