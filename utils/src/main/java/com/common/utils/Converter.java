package com.common.utils;

import android.app.Application;
import android.content.Context;
import android.util.TypedValue;

public class Converter {
    private static Converter sConverter;

    private Context mContext;

    private Converter(Context context) {
        if (context instanceof Application) {
            mContext = context;
        } else {
            mContext = context.getApplicationContext();
        }
    }

    public static void INITIALIZE(Context context) {
        if (sConverter == null)
            sConverter = new Converter(context);
    }

    public static int dip2px(float dp) {
        final float scale = sConverter.mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(float pxValue) {
        float scale = sConverter.mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, sConverter.mContext.getResources().getDisplayMetrics());
    }
}
