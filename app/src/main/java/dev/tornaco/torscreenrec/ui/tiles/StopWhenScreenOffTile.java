package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class StopWhenScreenOffTile extends QuickTile {

    public StopWhenScreenOffTile(@NonNull Context context) {
        super(context);
        this.iconRes = R.drawable.ic_screen_lock_portrait_black_24dp;
        this.tileView = new SwitchTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.get().getBoolean(SettingsProvider.Key.SCREEN_OFF_STOP));
            }

            @Override
            protected void onCheckChanged(final boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.get().putBoolean(SettingsProvider.Key.SCREEN_OFF_STOP, checked);
            }
        };
        this.titleRes = R.string.title_stop_when_screen_off;
    }
}
