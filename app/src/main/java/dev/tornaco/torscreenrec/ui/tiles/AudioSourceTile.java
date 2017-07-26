package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.widget.RelativeLayout;

import com.google.common.collect.Lists;

import org.newstand.logger.Logger;

import java.util.List;

import dev.nick.library.AudioSource;
import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.QuickTile;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

/**
 * Created by Tornaco on 2017/7/26.
 * Licensed with Apache.
 */

public class AudioSourceTile extends QuickTile {


    public AudioSourceTile(final Context context) {
        super(context);

        final SettingsProvider settingsProvider = new SettingsProvider(context);

        this.titleRes = R.string.title_audio_source;
        this.iconRes = R.drawable.ic_speaker_black_24dp;

        final List<String> descList = getDesc(context);

        this.summary = descList.get(toPosition(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE)));

        this.tileView = new DropDownTileView(context) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setSelectedItem(toPosition(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE)), true);
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return descList;
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                int source = toSource(position);
                Logger.i("Selected source:%s", source);
                settingsProvider.putInt(SettingsProvider.Key.AUDIO_SOURCE, source);
                getSummaryTextView().setText(descList.get(position));
            }
        };
    }

    private List<String> getDesc(Context context) {
        return Lists.newArrayList(
                context.getString(R.string.audio_source_noop),
                context.getString(R.string.audio_source_mic),
                context.getString(R.string.audio_source_submix)

        );
    }

    private int toPosition(int source) {
        switch (source) {
            case AudioSource.NOOP:
                return 0;
            case AudioSource.MIC:
                return 1;
            case AudioSource.R_SUBMIX:
                return 2;
        }
        throw new IllegalArgumentException("Bad source");
    }

    private int toSource(int position) {
        switch (position) {
            case 0:
                return AudioSource.NOOP;
            case 1:
                return AudioSource.MIC;
            case 2:
                return AudioSource.R_SUBMIX;
        }
        throw new IllegalArgumentException("Bad source");
    }
}
