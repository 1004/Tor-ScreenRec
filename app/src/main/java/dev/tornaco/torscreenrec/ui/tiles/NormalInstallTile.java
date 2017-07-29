package dev.tornaco.torscreenrec.ui.tiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.bridge.Installer;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class NormalInstallTile extends QuickTile {

    public NormalInstallTile(final Context context) {
        super(context);

        this.titleRes = R.string.title_normal_install;
        this.summaryRes = R.string.summary_normal_install;
        this.iconRes = R.drawable.ic_mood_bad_black_24dp;

        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                final ProgressDialog p = new ProgressDialog(context);
                p.setIndeterminate(true);
                p.setMessage(context.getString(R.string.installing));
                p.setCancelable(false);
                p.show();

                Installer.installWithIntentAsync(getContext(), new Installer.Callback() {
                    @Override
                    public void onSuccess() {
                        p.dismiss();
                        Snackbar.make(getTileView(), R.string.install_success, Snackbar.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onFailure(Throwable throwable, String errTitle) {
                        p.dismiss();
                        Snackbar.make(getTileView(), context.getString(R.string.install_fail, errTitle),
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
