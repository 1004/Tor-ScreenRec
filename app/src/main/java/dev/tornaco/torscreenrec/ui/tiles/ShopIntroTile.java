package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class ShopIntroTile extends QuickTile {
    public ShopIntroTile(Context context) {
        super(context);

        this.iconRes=R.drawable.ic_payment_black_24dp;
        this.title = "++++++++++++++++++++++++++";
        this.summaryRes = R.string.summary_buy_intro;
        this.tileView = new QuickTileView(context, this);
    }
}
