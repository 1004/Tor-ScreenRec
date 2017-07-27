package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.ContainerHostActivity;
import dev.tornaco.torscreenrec.ui.RecordingBrowserFragment;

/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class RecordingBrowserTile extends QuickTile {

    public RecordingBrowserTile(final Context context) {
        super(context);

        this.titleRes = R.string.title_recording_browser;
        this.iconRes = R.drawable.ic_movie_black_24dp;

        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                context.startActivity(ContainerHostActivity.getIntent(
                        context, RecordingBrowserFragment.class
                ));
            }
        };
    }
}
