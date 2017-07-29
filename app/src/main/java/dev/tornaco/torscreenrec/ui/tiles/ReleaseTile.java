package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.text.util.Linkify;
import android.widget.RelativeLayout;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class ReleaseTile extends QuickTile {
    public ReleaseTile(Context context) {
        super(context);

        this.iconRes = R.drawable.ic_new_releases_black_24dp;
        this.titleRes = R.string.release;
        this.summary = "https://github.com/Tornaco/Tor-ScreenRec/releases";
        this.tileView = new QuickTileView(context, this) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                getSummaryTextView().setAutoLinkMask(Linkify.ALL);
            }
        };
    }
}
