package dev.tornaco.torscreenrec.ui;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.AudioSourceTile;

/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class AudioSourceFragment extends DashboardFragment {
    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);
        Category category = new Category();
        category.titleRes = R.string.title_audio_source;
        category.addTile(new AudioSourceTile(getContext()));
        categories.add(category);
    }
}
