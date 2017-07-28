package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.AudioSourceTile;
import dev.tornaco.torscreenrec.ui.tiles.FloatControlAlphaTile;
import dev.tornaco.torscreenrec.ui.tiles.FloatControlThemeTile;
import dev.tornaco.torscreenrec.ui.tiles.FlowViewTile;
import dev.tornaco.torscreenrec.ui.tiles.FrameRateTile;
import dev.tornaco.torscreenrec.ui.tiles.OrientationTile;
import dev.tornaco.torscreenrec.ui.tiles.PreviewSizeDropdownTile;
import dev.tornaco.torscreenrec.ui.tiles.ShakeTile;
import dev.tornaco.torscreenrec.ui.tiles.ShowTouchTile;
import dev.tornaco.torscreenrec.ui.tiles.SoundEffectTile;
import dev.tornaco.torscreenrec.ui.tiles.StopOnVolumeTile;
import dev.tornaco.torscreenrec.ui.tiles.StopWhenScreenOffTile;
import dev.tornaco.torscreenrec.ui.tiles.StorageTile;
import dev.tornaco.torscreenrec.ui.tiles.SwitchCameraTile;
import dev.tornaco.torscreenrec.ui.tiles.VideoResTile;
import dev.tornaco.torscreenrec.ui.tiles.WithCameraTile;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class SettingsFragment extends DashboardFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_more_settings);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category video = new Category();
        video.titleRes = R.string.category_video;
        video.addTile(new VideoResTile(getContext()));
        video.addTile(new OrientationTile(getContext()));
        video.addTile(new FrameRateTile(getContext()));

        Category audio = new Category();
        audio.titleRes = R.string.category_audio;
        audio.addTile(new AudioSourceTile(getContext()));

        Category access = new Category();
        access.titleRes = R.string.category_accessibility;
        access.addTile(new ShowTouchTile(getContext()));
        access.addTile(new SoundEffectTile(getContext()));
        access.addTile(new StopWhenScreenOffTile(getContext()));
        access.addTile(new ShakeTile(getContext()));
        access.addTile(new StopOnVolumeTile(getContext()));

        Category camera = new Category();
        camera.titleRes = R.string.summary_camera;
        camera.addTile(new WithCameraTile(getContext()));
        camera.addTile(new PreviewSizeDropdownTile(getContext()));
        camera.addTile(new SwitchCameraTile(getContext()));

        Category floatView = new Category();
        floatView.titleRes = R.string.title_float_window;
        floatView.addTile(new FlowViewTile(getContext()));
        floatView.addTile(new FloatControlAlphaTile(getContext()));
        floatView.addTile(new FloatControlThemeTile(getContext()));

        Category storage = new Category();
        storage.titleRes = R.string.category_storage;
        storage.addTile(new StorageTile(getActivity()));

        categories.add(video);
        categories.add(audio);
        categories.add(camera);
        categories.add(access);
        categories.add(floatView);
        categories.add(storage);

    }
}
