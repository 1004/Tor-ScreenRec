package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.widget.RelativeLayout;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class FlowViewTile extends QuickTile {

    public FlowViewTile(Context context) {
        super(context);

        this.titleRes = R.string.title_float_view;
        this.iconRes = R.drawable.ic_bubble_chart;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.get().getBoolean(SettingsProvider.Key.FLOAT_WINDOW));
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.get().putBoolean(SettingsProvider.Key.FLOAT_WINDOW, checked);
            }
        };
    }
}
