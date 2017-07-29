package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.ContainerHostActivity;
import dev.tornaco.torscreenrec.ui.PayListBrowserFragment;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class PayListTile extends QuickTile {

    public PayListTile(final Context context) {
        super(context);

        this.iconRes = R.drawable.ic_playlist_add_check_black_24dp;
        this.titleRes = R.string.title_pay_list;

        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                context.startActivity(ContainerHostActivity.getIntent(context, PayListBrowserFragment.class));
            }
        };


    }
}
