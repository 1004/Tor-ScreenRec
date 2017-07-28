package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.camera.CameraPreviewServiceProxy;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class WithCameraTile extends QuickTile {

    public WithCameraTile(@NonNull Context context) {
        super(context);
        this.iconRes = R.drawable.ic_camera_alt_black_24dp;
        this.tileView = new SwitchTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.get().getBoolean(SettingsProvider.Key.CAMERA));
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.get().putBoolean(SettingsProvider.Key.CAMERA, checked);
                if (checked)
                    CameraPreviewServiceProxy.show(getContext(), SettingsProvider.get().getInt(SettingsProvider.Key.CAMERA_SIZE));
                else
                    CameraPreviewServiceProxy.hide(getContext());
            }
        };
        this.titleRes = R.string.title_with_camera;
    }
}
