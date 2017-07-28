package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.List;

import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.QuickTile;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class OrientationTile extends QuickTile {

    private String[] mOriDesc;

    public  OrientationTile(@NonNull Context context) {

        super(context);
        this.iconRes = R.drawable.ic_phone_android_black_24dp;
        this.titleRes = R.string.title_orientation;

        mOriDesc = context.getResources().getStringArray(R.array.orientations);
        this.summary = mOriDesc[SettingsProvider.get().getInt(SettingsProvider.Key.ORIENTATION)];

        this.tileView = new DropDownTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setSelectedItem(SettingsProvider.get().getInt(SettingsProvider.Key.ORIENTATION), false);
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return Arrays.asList(mOriDesc);
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                SettingsProvider.get().putInt(SettingsProvider.Key.ORIENTATION, position);
                getSummaryTextView().setText(mOriDesc[position]);
            }
        };
    }
}