package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.MagiskInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.NormalInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.RootInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.XposedInstallTile;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class InstallFragment extends DashboardFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_install);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category category = new Category();
        category.titleRes = R.string.title_install_type;
        category.addTile(new NormalInstallTile(getContext()));
        Category categoryRoot = new Category();
        categoryRoot.titleRes = R.string.title_install_recommend;
        categoryRoot.addTile(new RootInstallTile(getActivity()));
        Category categoryXposed = new Category();
        categoryXposed.addTile(new XposedInstallTile(getContext()));
        Category categoryMagisk = new Category();
        categoryMagisk.addTile(new MagiskInstallTile(getContext()));

        categories.add(category);
        categories.add(categoryRoot);
        categories.add(categoryXposed);
        categories.add(categoryMagisk);
    }
}
