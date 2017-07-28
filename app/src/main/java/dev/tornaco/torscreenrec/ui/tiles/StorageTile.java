package dev.tornaco.torscreenrec.ui.tiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.Observable;
import java.util.Observer;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

public class StorageTile extends QuickTile {

    private Observer o = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            if (arg == SettingsProvider.Key.VIDEO_ROOT_PATH)
                getTileView().getSummaryTextView().setText(getContext().getString(R.string.summary_storage,
                        SettingsProvider.get().getString(SettingsProvider.Key.VIDEO_ROOT_PATH)));
        }
    };

    public StorageTile(@NonNull final Context context) {
        super(context);
        this.titleRes = R.string.title_storage;
        this.summary = context.getString(R.string.summary_storage,
                SettingsProvider.get().getString(SettingsProvider.Key.VIDEO_ROOT_PATH));
        this.iconRes = R.drawable.ic_folder_open_black_24dp;
        this.tileView = new QuickTileView(context, this) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                SettingsProvider.get().addObserver(o);
            }

            @Override
            public void onClick(View v) {
                super.onClick(v);
                pickSingleDir((Activity) context, SettingsProvider.REQUEST_CODE_FILE_PICKER);
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                SettingsProvider.get().deleteObserver(o);
            }
        };
    }

    private static void pickSingleDir(Activity activity, int code) {
        // This always works
        Intent i = new Intent(activity, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        activity.startActivityForResult(i, code);
    }
}
