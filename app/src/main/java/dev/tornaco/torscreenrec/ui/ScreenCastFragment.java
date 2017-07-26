package dev.tornaco.torscreenrec.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.nick.library.BridgeManager;
import dev.nick.library.IParam;
import dev.nick.library.IWatcher;
import dev.nick.library.RecBridgeServiceProxy;
import dev.nick.library.TokenAdapter;
import dev.nick.library.WatcherAdapter;
import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.DrawerNavigatorActivity;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.bridge.Installer;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.pref.StorageManager;
import lombok.Getter;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public class ScreenCastFragment extends DashboardFragment {

    public static ScreenCastFragment create() {
        return new ScreenCastFragment();
    }

    @Getter
    private View rootView;

    @Getter
    private AtomicBoolean isRecording;

    @Getter
    private SettingsProvider settingsProvider;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_screen_cast;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStatus();
    }

    private IWatcher watcher = new WatcherAdapter()

    {
        @Override
        public void onStart() throws RemoteException {
            if (isRecording == null) {
                isRecording = new AtomicBoolean(true);
            } else {
                isRecording.set(true);
            }
            Logger.i("onStop");
        }

        @Override
        public void onStop() throws RemoteException {
            if (isRecording == null) {
                isRecording = new AtomicBoolean(false);
            } else {
                isRecording.set(false);
            }
            Logger.i("onStop");
        }

        @Override
        public void onElapsedTimeChange(String s) throws RemoteException {

        }
    };

    private void setupStatus() {

        settingsProvider = new SettingsProvider(getContext().getApplicationContext());

        try {
            RecBridgeServiceProxy.from(getContext())
                    .watch(watcher);
        } catch (RemoteException e) {
            onRemoteException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            RecBridgeServiceProxy.from(getContext())
                    .unWatch(watcher);
        } catch (RemoteException e) {
            onRemoteException(e);
        }
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int idRes) {
        return (T) getRootView().findViewById(idRes);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int idRes) {
        return (T) root.findViewById(idRes);
    }

    protected void setupView() {

        Button button = findView(R.id.button);

        // Read status.
        BridgeManager bridgeManager = BridgeManager.getInstance();
        final boolean installed = bridgeManager.isInstalled(getContext());
        button.setText(installed ? R.string.title_uninstall : R.string.title_install);

        ImageView statusView = findView(R.id.icon1);
        statusView.setColorFilter(ContextCompat.getColor(getContext(),
                installed ? R.color.green : R.color.red));
        statusView.setImageResource(installed ? R.drawable.ic_check_circle_black_24dp : R.drawable.ic_info_black_24dp);


        DrawerNavigatorActivity drawerNavigatorActivity = (DrawerNavigatorActivity) getActivity();
        FloatingActionButton floatingActionButton = drawerNavigatorActivity.getFloatingActionButton();

        if (installed) {
            floatingActionButton.show();
        } else {
            floatingActionButton.hide();
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording != null && isRecording.get()) {
                    onRequestStop();
                } else {
                    onRequestStart();
                }
            }
        });

        findView(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRequestInstall(!installed, view);
            }
        });
    }

    private void onRequestStart() {
        try {
            RecBridgeServiceProxy.from(getContext())
                    .start(IParam.builder()
                                    .audioSource(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE))
                                    .frameRate(settingsProvider.getInt(SettingsProvider.Key.FAME_RATE))
                                    .orientation(settingsProvider.getInt(SettingsProvider.Key.ORIENTATION))
                                    .resolution(settingsProvider.getString(SettingsProvider.Key.RESOLUTION))
                                    .stopOnScreenOff(settingsProvider.getBoolean(SettingsProvider.Key.SCREEN_OFF_STOP))
                                    .useMediaProjection(settingsProvider.getBoolean(SettingsProvider.Key.USER_PROJECTION))
                                    .stopOnShake(settingsProvider.getBoolean(SettingsProvider.Key.SHAKE_STOP))
                                    .shutterSound(settingsProvider.getBoolean(SettingsProvider.Key.SHUTTER_SOUND))
                                    .path(StorageManager.getInstance().getVideoRootPath())
                                    .build(),

                            new TokenAdapter() {
                                @Override
                                public String getDescription() throws RemoteException {
                                    if (isDead()) return "";
                                    return getString(R.string.recorder_description);
                                }
                            });
        } catch (RemoteException e) {
            onRemoteException(e);
        }
    }

    private void onRequestStop() {
        try {
            RecBridgeServiceProxy.from(getContext()).stop();
        } catch (RemoteException e) {
            onRemoteException(e);
        }
    }

    private void onRequestInstall(boolean install, final View view) {
        final ProgressDialog p = new ProgressDialog(getActivity());
        p.setIndeterminate(true);
        p.setMessage(getString(R.string.installing));
        p.setCancelable(false);
        p.show();
        Installer.installAsync(getContext(), new Installer.Callback() {
            @Override
            public void onSuccess() {
                p.dismiss();
                Snackbar.make(view, R.string.install_success, Snackbar.LENGTH_INDEFINITE)
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
                Snackbar.make(view, getString(R.string.install_fail, errTitle),
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.report, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
            }
        });
    }

    protected void onRemoteException(RemoteException e) {
        Logger.e(e, "onRemoteException");
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.remote_err)
                .setMessage(e.getLocalizedMessage())
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    protected boolean isDead() {
        return isDetached() || !isAdded() || getActivity().isDestroyed();
    }
}
