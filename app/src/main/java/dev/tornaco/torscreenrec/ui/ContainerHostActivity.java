package dev.tornaco.torscreenrec.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.nononsenseapps.filepicker.Utils;

import org.newstand.logger.Logger;

import java.io.File;
import java.util.List;

import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.pref.SettingsProvider;

/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class ContainerHostActivity extends TransitionSafeActivity {

    public static final String EXTRA_FRAGMENT_CLZ = "extra.fr.clz";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_with_appbar_template);
        setupToolbar();
        showHomeAsUp();
        replaceV4(R.id.container, onCreateFragment(), null, false);
    }

    public static Intent getIntent(Context context, Class<? extends Fragment> clz) {
        Intent i = new Intent(context, ContainerHostActivity.class);
        i.putExtra(ContainerHostActivity.EXTRA_FRAGMENT_CLZ, clz.getName());
        return i;
    }

    Fragment onCreateFragment() {
        Intent intent = getIntent();
        String clz = intent.getStringExtra(EXTRA_FRAGMENT_CLZ);
        Logger.i("Extra clz:%s", clz);
        if (AudioSourceFragment.class.getName().equals(clz)) {
            return new AudioSourceFragment();
        }
        if (RecordingBrowserFragment.class.getName().equals(clz)) {
            return new RecordingBrowserFragment();
        }
        if (SettingsFragment.class.getName().equals(clz)) {
            return new SettingsFragment();
        }
        if (InstallFragment.class.getName().equals(clz)) {
            return new InstallFragment();
        }
        if (ShopFragment.class.getName().equals(clz)) {
            return new ShopFragment();
        }
        if (PayListBrowserFragment.class.getName().equals(clz)) {
            return new PayListBrowserFragment();
        }
        if (AboutFragment.class.getName().equals(clz)) {
            return new AboutFragment();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsProvider.REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            File file = Utils.getFileForUri(files.get(0));
            // Do something with the result...
            onStorageDirPick(file);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onStorageDirPick(File dir) {
        Logger.d("onStorageDirPick:" + dir);
        SettingsProvider.get().putString(SettingsProvider.Key.VIDEO_ROOT_PATH, dir.getPath());
    }
}
