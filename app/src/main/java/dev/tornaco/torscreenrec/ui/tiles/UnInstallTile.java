package dev.tornaco.torscreenrec.ui.tiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.stericson.rootools.RootTools;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.bridge.Installer;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class UnInstallTile extends QuickTile {

    public UnInstallTile(final Context context) {
        super(context);

        this.titleRes = R.string.title_uninstall;
        this.iconRes = R.drawable.ic_remove_circle_black_24dp;

        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(final View v) {
                super.onClick(v);
                final ProgressDialog p = new ProgressDialog(context);
                p.setMessage(context.getString(R.string.uninstalling));
                p.setCancelable(false);
                p.show();

                Installer.unInstallAsync(getContext(), new Installer.Callback() {
                    @Override
                    public void onSuccess() {
                        p.dismiss();
                        Snackbar.make(v, R.string.uninstall_success, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.restart, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        RootTools.restartAndroid();
                                    }
                                }).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable, String errTitle) {
                        p.dismiss();
                        Snackbar.make(v, context.getString(R.string.uninstall_fail, errTitle),
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.report, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                    }
                });
            }
        };
    }
}
