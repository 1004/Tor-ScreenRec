package dev.tornaco.torscreenrec.ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import dev.nick.library.WatcherAdapter;
import dev.tornaco.torscreenrec.R;
import dev.tornaco.torscreenrec.TorScreenRecApp;
import dev.tornaco.torscreenrec.control.FloatControlTheme;
import dev.tornaco.torscreenrec.control.RecRequestHandler;
import dev.tornaco.torscreenrec.pref.SettingsProvider;
import dev.tornaco.torscreenrec.util.ThreadUtil;

public class FloatView extends FrameLayout {

    private Rect mRect = new Rect();
    private WindowManager mWm;
    private WindowManager.LayoutParams mLp = new WindowManager.LayoutParams();

    private TextView mTextView;
    private View mContainerView;
    private ImageView mImageView;

    private TorScreenRecApp mApp;

    int mTouchSlop;
    float density = getResources().getDisplayMetrics().density;

    private boolean isRecording;

    private WatcherAdapter watcher = new WatcherAdapter() {
        @Override
        public void onStart() throws RemoteException {
            super.onStart();
            mImageView.setImageResource(R.drawable.ic_stop);
            isRecording = true;
        }

        @Override
        public void onStop() throws RemoteException {
            super.onStop();
            mImageView.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mTextView.setText(R.string.elapse_time_zero);
            isRecording = false;
        }

        @Override
        public void onElapsedTimeChange(String formatedTime) {
            mTextView.setText(formatedTime);
        }
    };

    private Observer observer = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            if (arg == SettingsProvider.Key.FLOAT_WINDOW_ALPHA) {
                ThreadUtil.getMainThreadHandler()
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                mContainerView.setAlpha(getAlphaSettings());
                            }
                        });
            }
        }
    };

    private float getAlphaSettings() {
        int alpha = SettingsProvider.get()
                .getInt(SettingsProvider.Key.FLOAT_WINDOW_ALPHA);
        return (float) alpha / 100f;
    }

    public FloatView(final TorScreenRecApp context) {
        super(context);

        mApp = context;

        View rootView = LayoutInflater.from(context).inflate(getLayoutId(), this);
        mContainerView = rootView.findViewById(R.id.container);
        mContainerView.setAlpha(getAlphaSettings());

        mTextView = rootView.findViewById(R.id.text);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtil.newThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRecording) {
                            RecRequestHandler.stop(getContext());
                        } else {
                            RecRequestHandler.start(getContext());
                        }
                    }
                }).start();
            }
        };

        mImageView = rootView.findViewById(R.id.image);
        mImageView.setOnClickListener(clickListener);

        getWindowVisibleDisplayFrame(mRect);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTouchSlop = mTouchSlop * mTouchSlop;

        mWm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mLp.gravity = Gravity.START | Gravity.TOP;
        mLp.format = PixelFormat.RGBA_8888;
        mLp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        OnTouchListener touchListener = new OnTouchListener() {
            private float touchX;
            private float touchY;
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchX = event.getX() + getLeft();
                        touchY = event.getY() + getTop();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        isDragging = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - startX);
                        int dy = (int) (event.getRawY() - startY);
                        if ((dx * dx + dy * dy) > mTouchSlop) {
                            isDragging = true;
                            mLp.x = (int) (event.getRawX() - touchX);
                            mLp.y = (int) (event.getRawY() - touchY);
                            mWm.updateViewLayout(FloatView.this, mLp);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchX = touchY = 0.0F;
                        if (isDragging) {
                            reposition();
                            isDragging = false;
                            return true;
                        }
                }
                return false;
            }
        };
        setOnTouchListener(touchListener);

        context.watch(watcher);
        SettingsProvider.get().addObserver(observer);
    }

    protected int getLayoutId() {
        return FloatControlTheme.valueOf(
                SettingsProvider.get().getString(SettingsProvider.Key.FLOAT_WINDOW_THEME))
                .getLayoutRes();
    }

    public void attach() {
        if (getParent() == null) {
            mWm.addView(this, mLp);
        }
        mWm.updateViewLayout(this, mLp);
        getWindowVisibleDisplayFrame(mRect);
        mRect.top += dp2px(50);
        mLp.y = dp2px(150);
        mLp.x = mRect.width() - dp2px(55);
        reposition();
    }

    public void detach() {
        try {
            mWm.removeViewImmediate(this);
        } catch (Exception ignored) {

        } finally {
            mApp.unWatch(watcher);
            SettingsProvider.get().deleteObserver(observer);
        }
    }

    private boolean isDragging;


    private int dp2px(int dp) {
        return (int) (dp * density);
    }

    private void reposition() {
        if (mLp.x < (mRect.width() - getWidth()) / 2) {
            mLp.x = dp2px(5);
        } else {
            mLp.x = mRect.width() - dp2px(55);
        }
        if (mLp.y < mRect.top) {
            mLp.y = mRect.top;
        }
        mWm.updateViewLayout(this, mLp);
    }
}