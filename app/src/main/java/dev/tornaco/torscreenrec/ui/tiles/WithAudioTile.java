package dev.tornaco.torscreenrec.ui.tiles;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import dev.nick.library.AudioSource;
import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.ui.AudioSourceFragment;
import dev.tornaco.torscreenrec.ui.ContainerHostActivity;

/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class WithAudioTile extends QuickTile {

    private Observer o = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            if (o == SettingsProvider.Key.AUDIO_SOURCE) {
                getTileView().getSummaryTextView().setText(
                        getContext().getString(R.string.summary_with_audio,
                                getDesc(getContext())
                                        .get(toPosition(SettingsProvider.get()
                                                .getInt(SettingsProvider.Key.AUDIO_SOURCE)))));
            }
        }
    };

    public WithAudioTile(final Context context) {
        super(context);

        final SettingsProvider settingsProvider = SettingsProvider.get();

        settingsProvider.addObserver(o);

        this.titleRes = R.string.title_with_audio;
        this.iconRes = R.drawable.ic_record_voice_over_black_24dp;

        final boolean withAudio = settingsProvider.getBoolean(SettingsProvider.Key.WITH_AUDIO);

        this.summary = context.getString(R.string.summary_with_audio,
                getDesc(context).get(toPosition(settingsProvider.getInt(SettingsProvider.Key.AUDIO_SOURCE))));

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(withAudio);
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                settingsProvider.putBoolean(SettingsProvider.Key.WITH_AUDIO, checked);
            }

            @Override
            public void onClick(View v) {
                context.startActivity(ContainerHostActivity.getIntent(context, AudioSourceFragment.class));
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                settingsProvider.deleteObserver(o);
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
}
