package dev.tornaco.torscreenrec.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import dev.tornaco.torscreenrec.R;

public class RecordingButton extends FloatingActionButton {

    public RecordingButton(Context context) {
        super(context);
    }

    public RecordingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onRecording() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        animation.setInterpolator(getContext(), android.R.anim.linear_interpolator);
        startAnimation(animation);
    }

    public void onStopRecording() {
        clearAnimation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
