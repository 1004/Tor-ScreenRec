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

public class OpenSourceTile extends QuickTile {
    public OpenSourceTile(Context context) {
        super(context);

        this.iconRes = R.drawable.ic_code_black_24dp;
        this.titleRes = R.string.open_source;
        this.summary = "https://github.com/Tornaco/Tor-ScreenRec";
        this.tileView = new QuickTileView(context, this) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                getSummaryTextView().setAutoLinkMask(Linkify.ALL);
            }
        };
    }
}
