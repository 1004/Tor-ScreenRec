package dev.tornaco.torscreenrec.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.loader.PayExtraLoader;
import dev.tornaco.torscreenrec.modle.PayExtra;
import io.reactivex.functions.Consumer;

/**
 * Created by Tornaco on 2017/7/29.
 * Licensed with Apache.
 */

public class PayListBrowserFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_template, container, false);
        setupView(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_pay_list);
        showRetention();
    }

    public void setupView(View root) {

        swipeRefreshLayout = root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));

        recyclerView = root.findViewById(R.id.recycler_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoading();
            }
        });

        setupAdapter();
    }

    private void startLoading() {
        swipeRefreshLayout.setRefreshing(true);
        new PayExtraLoader().loadAsync(getString(R.string.pay_list_url),
                new PayExtraLoader.Callback() {
                    @Override
                    public void onError(final Throwable e) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<PayExtra> extras) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.update(extras);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            onPermissionGrant();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }

    private void onPermissionNotGrant() {
        getActivity().finish();
    }

    private void onPermissionGrant() {
        startLoading();
    }

    private void showRetention() {
        boolean hasBasicPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasBasicPermission) {
            new MaterialStyledDialog.Builder(getActivity())
                    .setTitle(R.string.title_perm_require)
                    .setDescription(R.string.summary_perm_require)
                    .setIcon(R.drawable.ic_folder_white_24dp)
                    .withDarkerOverlay(false)
                    .setCancelable(false)
                    .setPositiveText(android.R.string.ok)
                    .setNegativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            requestPerms();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            getActivity().finish();
                        }
                    })
                    .show();
        } else {
            onPermissionGrant();
        }
    }

    protected void setupAdapter() {
        recyclerView.setHasFixedSize(true);
        setupLayoutManager();
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);

    }

    protected void setupLayoutManager() {
        recyclerView.setLayoutManager(getLayoutManager());
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }


    class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView thumbnail;

        TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.title);
            description = itemView.findViewById(android.R.id.text1);
            thumbnail = itemView.findViewById(R.id.avatar);
            thumbnail.setImageResource(R.drawable.ic_header_avatar);
        }
    }


    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {

        private final List<PayExtra> data;

        public Adapter(List<PayExtra> data) {
            this.data = data;
        }

        public Adapter() {
            this(new ArrayList<PayExtra>());
        }

        public void update(List<PayExtra> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public void remove(int position) {
            this.data.remove(position);
            notifyItemRemoved(position);
        }

        public void add(PayExtra PayExtra, int position) {
            this.data.add(position, PayExtra);
            notifyItemInserted(position);
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            return new TwoLinesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, int position) {
            final PayExtra item = data.get(position);
            holder.title.setText(item.getNick());
            String descriptionText = item.getAd();
            holder.description.setText(descriptionText);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }
}
