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

public class PreviewSizeDropdownTile extends QuickTile {

    private String[] mSizes = null;

    public PreviewSizeDropdownTile(@NonNull Context context) {
        super(context);
        this.mSizes = getContext().getResources().getStringArray(R.array.preview_sizes);

        this.iconRes = R.drawable.ic_photo_size_select_small_black_24dp;
        this.titleRes = R.string.title_preview_size;
        this.summary = mSizes[SettingsProvider.get().getInt(SettingsProvider.Key.CAMERA_SIZE)];

        this.tileView = new DropDownTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setSelectedItem(SettingsProvider.get().getInt(SettingsProvider.Key.CAMERA_SIZE), false);
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return Arrays.asList(mSizes);
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                SettingsProvider.get().putInt(SettingsProvider.Key.CAMERA_SIZE, position);
                getSummaryTextView().setText(mSizes[position]);
            }
        };
    }
}