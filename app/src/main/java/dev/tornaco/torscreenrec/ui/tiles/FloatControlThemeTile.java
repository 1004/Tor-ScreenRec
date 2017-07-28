package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.List;

import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.QuickTile;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.control.FloatControlTheme;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class FloatControlThemeTile extends QuickTile {

    private String[] mSources = null;

    public FloatControlThemeTile(@NonNull Context context) {
        super(context);

        this.iconRes = R.drawable.ic_color_lens_black_24dp;

        this.mSources = new String[FloatControlTheme.values().length];
        for (int i = 0; i < mSources.length; i++) {
            mSources[i] = context.getString(FloatControlTheme.values()[i].getStringRes());
        }

        this.tileView = new DropDownTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                int index = FloatControlTheme.valueOf(SettingsProvider.get().getString(SettingsProvider.Key.FLOAT_WINDOW_THEME)).ordinal();
                setSelectedItem(index, false);
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return Arrays.asList(mSources);
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                SettingsProvider.get().putString(
                        SettingsProvider.Key.FLOAT_WINDOW_THEME, FloatControlTheme.from(position).name());
                getSummaryTextView().setText(mSources[position]);
            }
        };
        int index = FloatControlTheme.valueOf(SettingsProvider.get().getString(SettingsProvider.Key.FLOAT_WINDOW_THEME)).ordinal();
        this.titleRes = R.string.float_theme;
        this.summary = mSources[index];
    }
}
