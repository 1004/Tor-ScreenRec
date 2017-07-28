package dev.tornaco.torscreenrec.control;

import dev.tornaco.torscreenrec.R;

/**
 * Created by Tornaco on 2017/7/21.
 * Licensed with Apache.
 */

public enum FloatControlTheme {

    Default(R.layout.float_controls, R.string.theme_def),
    DefaultDark(R.layout.float_controls_dark, R.string.theme_def_dark),
    DefaultCircle(R.layout.float_controls_circle, R.string.theme_def_circle);

    FloatControlTheme(int layoutRes, int stringRes) {
        this.layoutRes = layoutRes;
        this.stringRes = stringRes;
    }

    int layoutRes;
    int stringRes;

    public int getStringRes() {
        return stringRes;
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    public static FloatControlTheme from(int ord) {
        for (FloatControlTheme t : values()) {
            if (t.ordinal() == ord) return t;
        }
        return Default;
    }
}
