package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.AuthorIntroTile;
import dev.tornaco.torscreenrec.ui.tiles.EmailTile;
import dev.tornaco.torscreenrec.ui.tiles.OpenSourceTile;
import dev.tornaco.torscreenrec.ui.tiles.ReleaseTile;
import dev.tornaco.torscreenrec.ui.tiles.VersionTile;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class AboutFragment extends DashboardFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_about);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category me = new Category();
        me.titleRes = R.string.me;
        me.addTile(new AuthorIntroTile(getContext()));
        me.addTile(new EmailTile(getContext()));

        Category app = new Category();
        app.titleRes = R.string.app;
        app.addTile(new VersionTile(getContext()));
        app.addTile(new OpenSourceTile(getContext()));
        app.addTile(new ReleaseTile(getContext()));

        categories.add(me);
        categories.add(app);
    }
}
