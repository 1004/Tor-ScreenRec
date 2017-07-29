package dev.tornaco.torscreenrec.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.nick.library.BridgeManager;
import dev.nick.library.IWatcher;
import dev.nick.library.WatcherAdapter;
import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.DrawerNavigatorActivity;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.TorScreenRecApp;
import dev.tornaco.torscreenrec.bridge.Installer;
import dev.tornaco.torscreenrec.common.SharedExecutor;
import dev.tornaco.torscreenrec.control.RecRequestHandler;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.ui.tiles.AdTile;
import dev.tornaco.torscreenrec.ui.tiles.AudioSourceTile;
import dev.tornaco.torscreenrec.ui.tiles.FlowViewTile;
import dev.tornaco.torscreenrec.ui.tiles.MoreSettingsTile;
import dev.tornaco.torscreenrec.ui.tiles.RecordingBrowserTile;
import dev.tornaco.torscreenrec.ui.tiles.WithCameraTile;
import dev.tornaco.torscreenrec.ui.widget.RecordingButton;
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    private Context appContext;

    private RecordingButton floatingActionButton;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        setAppContext(context.getApplicationContext());
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
            refreshFabState();
            Logger.i("onStart");
        }

        @Override
        public void onStop() throws RemoteException {
            if (isRecording == null) {
                isRecording = new AtomicBoolean(false);
            } else {
                isRecording.set(false);
            }
            refreshFabState();
            Logger.i("onStop");
        }

        @Override
        public void onElapsedTimeChange(String s) throws RemoteException {

        }
    };

    private void setupStatus() {

        settingsProvider = SettingsProvider.get();

        TorScreenRecApp app = (TorScreenRecApp) getActivity().getApplication();
        app.watch(watcher);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TorScreenRecApp app = (TorScreenRecApp) getActivity().getApplication();
        app.unWatch(watcher);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);
        Category quickFunc = new Category();
        quickFunc.titleRes = R.string.quick_function;
        quickFunc.addTile(new RecordingBrowserTile(getContext()));

        Category ad = new Category();
        if (!SettingsProvider.get().getBoolean(SettingsProvider.Key.PAID)) {
            ad.titleRes = R.string.title_ad_area;
            ad.addTile(new AdTile(getContext()));
        }

        Category quickSettings = new Category();
        quickSettings.titleRes = R.string.quick_settings;
        quickSettings.addTile(new AudioSourceTile(getContext()));
        quickSettings.addTile(new FlowViewTile(getContext()));
        quickSettings.addTile(new WithCameraTile(getContext()));

        Category moreSettings = new Category();
        moreSettings.titleRes = R.string.category_others;
        moreSettings.addTile(new MoreSettingsTile(getContext()));

        categories.add(quickFunc);
        categories.add(ad);
        categories.add(quickSettings);
        categories.add(moreSettings);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int idRes) {
        return (T) getRootView().findViewById(idRes);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(View root, @IdRes int idRes) {
        return (T) root.findViewById(idRes);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupView();
            }
        });
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
        statusView.setImageResource(installed ? R.drawable.ic_check_circle_black_24dp
                : R.drawable.ic_remove_circle_black_24dp);

        DrawerNavigatorActivity drawerNavigatorActivity = (DrawerNavigatorActivity) getActivity();
        floatingActionButton = drawerNavigatorActivity.getFloatingActionButton();

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


        // Retrieve version.
        if (installed) {
            SharedExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String name = BridgeManager.getInstance().getVersionName(getContext());
                    if (name == null)
                        name = getAppContext().getString(R.string.version_name_unknown);

                    boolean isPlatform = BridgeManager.getInstance().isInstalledInSystem(getContext());

                    final String versionNameMessage =
                            isPlatform
                                    ? getAppContext().getString(R.string.installed_version_name, name + "-Root")
                                    : getAppContext().getString(R.string.installed_version_name, name);

                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCardStatus(versionNameMessage);
                        }
                    });
                }
            });
        } else {
            updateCardStatus(getAppContext().getString(R.string.bridge_not_installed));
        }
    }


    private void updateCardStatus(String title) {
        final TextView textView = findView(R.id.bridge_status);
        textView.setText(title);
    }

    private void onRequestStart() { //FIXME
        RecRequestHandler.start(getActivity().getApplicationContext());
    }

    private void onRequestStop() {
        RecRequestHandler.stop(getActivity().getApplicationContext()); //FIXME
    }

    private void refreshFabState() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRecording.get()) {
                    floatingActionButton.setImageResource(R.drawable.stop);
                    floatingActionButton.onRecording();
                } else {
                    floatingActionButton.setImageResource(R.drawable.record);
                    floatingActionButton.onStopRecording();
                }
            }
        });
    }

    private void onRequestInstall(boolean install, final View view) {
        final ProgressDialog p = new ProgressDialog(getActivity());
        p.setIndeterminate(true);
        if (install) {
            startActivity(ContainerHostActivity.getIntent(getContext(), InstallFragment.class));
        } else {
            p.setMessage(getString(R.string.uninstalling));
            p.setCancelable(false);
            p.show();

            Installer.unInstallAsync(getContext(), new Installer.Callback() {
                @Override
                public void onSuccess() {
                    p.dismiss();
                    Snackbar.make(view, R.string.uninstall_success, Snackbar.LENGTH_INDEFINITE)
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
                    Snackbar.make(view, getString(R.string.uninstall_fail, errTitle),
                            Snackbar.LENGTH_LONG)
                            .setAction(R.string.report, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                }
            });
        }
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
