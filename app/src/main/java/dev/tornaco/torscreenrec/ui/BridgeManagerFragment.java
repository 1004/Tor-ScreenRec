package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;

import java.util.List;

import dev.nick.library.BridgeManager;
import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.MagiskInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.NormalInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.RootInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.UnInstallTile;
import dev.tornaco.torscreenrec.ui.tiles.XposedInstallTile;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class BridgeManagerFragment extends DashboardFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_bridge_manager);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category unInstall = new Category();
        unInstall.titleRes = R.string.title_uninstall;
        unInstall.addTile(new UnInstallTile(getActivity()));

        Category category = new Category();
        category.titleRes = R.string.title_install;
        category.addTile(new NormalInstallTile(getContext()));
        Category categoryRoot = new Category();
        categoryRoot.titleRes = R.string.title_install;
        categoryRoot.addTile(new RootInstallTile(getActivity()));
        Category categoryXposed = new Category();
        categoryXposed.addTile(new XposedInstallTile(getContext()));
        Category categoryMagisk = new Category();
        categoryMagisk.addTile(new MagiskInstallTile(getContext()));

        if (BridgeManager.getInstance().isInstalled(getContext())) categories.add(unInstall);
        categories.add(categoryRoot);
        categories.add(categoryXposed);
        categories.add(categoryMagisk);
    }
}
