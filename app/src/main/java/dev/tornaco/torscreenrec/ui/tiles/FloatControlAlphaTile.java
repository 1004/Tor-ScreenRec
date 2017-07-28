package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;

import java.util.Observable;
import java.util.Observer;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class FloatControlAlphaTile extends QuickTile {

    public FloatControlAlphaTile(@NonNull Context context) {
        super(context);
        this.titleRes = R.string.float_alpha;
        this.iconRes = R.drawable.ic_gradient_black_24dp;
        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                showAlphaSeeker();
            }
        };
        this.summary = String.valueOf(SettingsProvider.get().getInt(SettingsProvider.Key.FLOAT_WINDOW_ALPHA));
        SettingsProvider.get().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg == SettingsProvider.Key.FLOAT_WINDOW_ALPHA) {
                    getTileView().getSummaryTextView().setText(
                            String.valueOf(SettingsProvider.get().getInt(SettingsProvider.Key.FLOAT_WINDOW_ALPHA))
                    );
                }
            }
        });
    }

    private void showAlphaSeeker() {
        final SeekBar seekBar = new SeekBar(getContext());
        int alpha = SettingsProvider.get().getInt(SettingsProvider.Key.FLOAT_WINDOW_ALPHA);
        seekBar.setMax(100);
        seekBar.setProgress(alpha);
        new AlertDialog.Builder(getContext())
                .setView(seekBar)
                .setTitle(R.string.float_alpha)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int current = seekBar.getProgress();
                        SettingsProvider.get().putInt(SettingsProvider.Key.FLOAT_WINDOW_ALPHA, current);
                    }
                })
                .create()
                .show();
    }
}
