package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.List;

import dev.nick.library.ValidResolutions;
import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.QuickTile;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class VideoResTile extends QuickTile {

    private String[] mResDescs;

    public VideoResTile(@NonNull Context context) {

        super(context);
        this.iconRes = R.drawable.ic_play_circle_filled_black_24dp;
        this.titleRes = R.string.title_high_res;

        mResDescs = ValidResolutions.DESC;

        final String current = SettingsProvider.get().getString(SettingsProvider.Key.RESOLUTION);

        mResDescs[ValidResolutions.INDEX_MASK_AUTO] = context.getString(R.string.summary_res_auto);

        this.summary = current;

        this.tileView = new DropDownTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                int index = current.trim().equals("AUTO") ? 0 : ValidResolutions.indexOf(current);
                setSelectedItem(index, false);
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return Arrays.asList(mResDescs);
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                SettingsProvider.get().putString(SettingsProvider.Key.RESOLUTION,
                        ValidResolutions.DESC[position]);
                getSummaryTextView().setText(mResDescs[position]);
            }
        };
    }
}
