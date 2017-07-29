package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class ShopWechatCodeTile extends QuickTile {

    public ShopWechatCodeTile(final Context context) {
        super(context);

        this.iconRes = R.drawable.ic_wechat;
        this.titleRes = R.string.title_wechat;
        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                PaymentDialog.show(context, R.string.title_wechat, R.drawable.qr_wechat);
            }

            @Override
            protected boolean useStaticTintColor() {
                return false;
            }
        };
    }
}
