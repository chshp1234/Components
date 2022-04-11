package com.common.accessibility.viewid;

public class SystemWidgetId {
    /** 底部弹窗“始终”按钮——先判断按钮存不存在 */
    public static final String BUTTON_ALWAYS = "android:id/button_always";

    /** 悬浮窗授权页面，“在其他应用上层显示选择框” */
    public static final String SETTING_SUB_SWITCH_WIDGET = "android:id/switch_widget";

    /** 授权弹窗“始终允许” */
    public static final String PERMISSION_ALLOW_BUTTON =
            "com.android.packageinstaller:id/permission_allow_button";
    /** 授权弹窗“禁止”按钮 */
    public static final String PERMISSION_DENY_BUTTON =
            "com.android.packageinstaller:id/permission_deny_button";
    /** 授权弹窗“禁止后不再询问”选择框 */
    public static final String PERMISSION_DO_NOT_ASK_CHECKBOX =
            "com.android.packageinstaller:id/do_not_ask_checkbox";

    /** 截屏授权弹窗“立即开始”——(安卓弹窗，确认按钮) */
    public static final String SCREEN_SHOT_BEGIN = "android:id/button1";
    /** 截屏授权弹窗“取消”按钮——(安卓弹窗，取消按钮) */
    public static final String SCREEN_SHOT_CANCEL = "android:id/button2";
    /** 截屏授权弹窗“不再显示”选择框 */
    public static final String SCREEN_SHOT_NO_ASK = "com.android.systemui:id/remember";
}
