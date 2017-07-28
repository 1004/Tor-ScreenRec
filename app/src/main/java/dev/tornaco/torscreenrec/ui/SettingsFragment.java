package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.VideoResTile;

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

        categories.add(video);
    }
}
