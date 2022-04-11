package com.common.utils;

/**
 * @author csp
 * @date 2017/11/4
 */
public class ClickUtils {
    /** 两次点击按钮之间的点击间隔不能少于500毫秒 */
    private static final int MIN_CLICK_DELAY_TIME = 500;

    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
