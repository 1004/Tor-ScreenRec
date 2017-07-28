package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.ContainerHostActivity;
import dev.tornaco.torscreenrec.ui.SettingsFragment;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class MoreSettingsTile extends QuickTile {

    public MoreSettingsTile(final Context context) {
        super(context);

        this.titleRes = R.string.title_more_settings;
        this.iconRes = R.drawable.ic_settings_white_24dp;


        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                context.startActivity(ContainerHostActivity.getIntent(
                        context, SettingsFragment.class
                ));
            }
        };
    }
}
