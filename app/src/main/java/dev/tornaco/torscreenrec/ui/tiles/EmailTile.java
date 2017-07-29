package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class EmailTile extends QuickTile {
    public EmailTile(Context context) {
        super(context);

        this.iconRes = R.drawable.ic_mail_outline_black_24dp;
        this.titleRes = R.string.title_email;
        this.summary = "tornaco@163.com";
        this.tileView = new QuickTileView(context, this);
    }
}
