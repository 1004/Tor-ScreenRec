package dev.tornaco.torscreenrec.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.loader.VideoProvider;
import dev.tornaco.torscreenrec.modle.Video;
import dev.tornaco.torscreenrec.util.MediaTools;
import dev.tornaco.torscreenrec.util.ThreadUtil;
import io.reactivex.functions.Consumer;


/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class RecordingBrowserFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_template, container, false);
        setupView(root);
        return root;
    }

    public void setupView(View root) {

        swipeRefreshLayout = root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));

        mRecyclerView = root.findViewById(R.id.recycler_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoading();
            }
        });

        setupAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_recording_browser);
        showRetention();
    }

    private void startLoading() {
        swipeRefreshLayout.setRefreshing(true);
        ThreadUtil.newThread(new Runnable() {
            @Override
            public void run() {
                final List<Video> videos = new VideoProvider(getContext()).getList();

                ThreadUtil.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.update(videos);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).run();
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
        mRecyclerView.setHasFixedSize(true);
        setupLayoutManager();
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);

    }

    protected void setupLayoutManager() {
        mRecyclerView.setLayoutManager(getLayoutManager());
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }


    class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView thumbnail;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            thumbnail = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }


    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {

        private final List<Video> data;

        public Adapter(List<Video> data) {
            this.data = data;
        }

        public Adapter() {
            this(new ArrayList<Video>());
        }

        public void update(List<Video> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public void remove(int position) {
            this.data.remove(position);
            notifyItemRemoved(position);
        }

        public void add(Video video, int position) {
            this.data.add(position, video);
            notifyItemInserted(position);
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            return new TwoLinesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, int position) {
            final Video item = data.get(position);
            holder.title.setText(item.getTitle());
            String descriptionText = item.getDuration();
            holder.description.setText(descriptionText);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), holder.description);
                    popupMenu.inflate(R.menu.actions);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_play:
                                    startActivity(MediaTools.buildOpenIntent(getContext(),
                                            new File(item.getPath())));
                                    break;
                                case R.id.action_remove:
                                    ThreadUtil.getWorkThreadHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            new File(item.getPath()).delete();
                                            remove(holder.getAdapterPosition());
                                        }
                                    });
                                    break;
                                case R.id.action_rename:
                                    ThreadUtil.getMainThreadHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showRenameDialog(item.getTitle(), item.getPath());
                                        }
                                    });
                                    break;
                                case R.id.action_share:
                                    startActivity(MediaTools.buildSharedIntent(getContext(),
                                            new File(item.getPath())));
                                    break;
                                case R.id.action_togif:
                                    String path = item.getPath();
                                    toGif(path, new File(path).getParent() + File.separator + getNameWithoutExtension(path) + ".gif");
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            Glide.with(getContext()).load(item.getPath()).into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        void showRenameDialog(String hint, final String fromPath) {
            View editTextContainer = LayoutInflater.from(getContext()).inflate(dev.nick.tiles.R.layout.dialog_edit_text, null, false);
            final EditText editText = (EditText) editTextContainer.findViewById(dev.nick.tiles.R.id.edit_text);
            editText.setHint(hint);
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setView(editTextContainer)
                    .setTitle(R.string.action_rename)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThreadUtil.getWorkThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    File parent = new File(fromPath).getParentFile();
                                    File to = new File(parent, editText.getText().toString() + ".mp4");
                                    new File(fromPath).renameTo(to);
                                    MediaScannerConnection.scanFile(getContext(),
                                            new String[]{to.getAbsolutePath()}, null,
                                            new MediaScannerConnection.OnScanCompletedListener() {
                                                public void onScanCompleted(String path, Uri uri) {
                                                    //FIXME.
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
            alertDialog.show();
        }

        public String getNameWithoutExtension(String file) {
            String fileName = new File(file).getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        }

        void toGif(String path, final String dest) {
            String command = String.format("-y -i %s -pix_fmt rgb24 -r 10 %s", path, dest);
            Logger.d("Command:" + command);
            String[] commands = command.split(" ");
            final ProgressDialog p = new ProgressDialog(getContext());
            p.setTitle(R.string.action_togif);
            p.setCancelable(false);
            p.setIndeterminate(true);

            try {
                FFmpeg.getInstance(getContext()).execute(commands,
                        new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                        Logger.d(message);
                        Snackbar.make(mRecyclerView, getString(R.string.result_to_gif_ok, dest),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onProgress(String message) {
                        p.setMessage(message);
                        Logger.d(message);
                    }

                    @Override
                    public void onFailure(final String message) {
                        Logger.d(message);
                        Snackbar.make(mRecyclerView, getString(R.string.result_to_gif_fail), Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(getContext())
                                                .setMessage(message)
                                                .setTitle(R.string.result_to_gif_fail)
                                                .setCancelable(false)
                                                .setPositiveButton(android.R.string.ok, null)
                                                .create()
                                                .show();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onStart() {
                        p.show();
                    }

                    @Override
                    public void onFinish() {
                        p.dismiss();
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                Toast.makeText(getContext(), "FFmpegCommandAlreadyRunningException", Toast.LENGTH_LONG).show();
            }
        }
    }
}
