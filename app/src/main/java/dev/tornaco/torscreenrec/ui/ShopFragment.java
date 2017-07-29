package dev.tornaco.torscreenrec.ui;

import android.os.Bundle;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.ui.tiles.PayListTile;
import dev.tornaco.torscreenrec.ui.tiles.PayStatusTile;
import dev.tornaco.torscreenrec.ui.tiles.ShopAliPayCodeTile;
import dev.tornaco.torscreenrec.ui.tiles.ShopIntroTile;
import dev.tornaco.torscreenrec.ui.tiles.ShopWechatCodeTile;

/**
 * Created by Tornaco on 2017/7/28.
 * Licensed with Apache.
 */

public class ShopFragment extends DashboardFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_buy);
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category about = new Category();
        about.addTile(new ShopIntroTile(getContext()));

        Category payment = new Category();
        payment.titleRes = R.string.title_pay_ment_type;
        payment.addTile(new ShopAliPayCodeTile(getActivity()));
        payment.addTile(new ShopWechatCodeTile(getActivity()));

        Category thanks = new Category();
        thanks.titleRes = R.string.title_thanks;
        thanks.addTile(new PayListTile(getActivity()));
        thanks.addTile(new PayStatusTile(getActivity()));

        categories.add(about);
        categories.add(payment);
        categories.add(thanks);
    }
}
