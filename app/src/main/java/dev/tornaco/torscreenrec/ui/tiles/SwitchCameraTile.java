package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.camera.CameraManager;
import dev.tornaco.torscreenrec.util.ThreadUtil;

public class SwitchCameraTile extends QuickTile {

    public SwitchCameraTile(@NonNull Context context) {
        super(context);
        this.titleRes = R.string.title_switch_camera;
        this.iconRes = R.drawable.ic_camera_front_black_24dp;
        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                ThreadUtil.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        CameraManager.get().swapCamera();
                    }
                });
            }
        };
    }
}
