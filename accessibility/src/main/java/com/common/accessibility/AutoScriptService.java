package com.common.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Region;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.common.accessibility.viewid.SystemWidgetId;

import java.util.concurrent.atomic.AtomicReference;

public class AutoScriptService extends AccessibilityService {
    public static AtomicReference<String> CURRENT_ACTIVITY = new AtomicReference<>("");
    public static AtomicReference<String> CURRENT_PACKAGE  = new AtomicReference<>("");

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.d("onServiceConnected");
        setScreenOn();
        AccessibilityApplication.register(this);
//        AccessibilityApplication.initWX(AppUtils.getAppVersionName("com.tencent.mm"));
//        AccessibilityApplication.initTB(AppUtils.getAppVersionCode("com.taobao.taobao"));


        // 放大控制器
        getMagnificationController()
                .addListener(
                        new MagnificationController
                                .OnMagnificationChangedListener() {
                            @Override
                            public void onMagnificationChanged(
                                    @NonNull
                                            MagnificationController controller,
                                    @NonNull Region region,
                                    float scale,
                                    float centerX,
                                    float centerY) {

                                /*region.set(
                                8,
                                8,
                                ScreenUtils.getAppScreenWidth(),
                                ScreenUtils.getAppScreenHeight() >> 2);*/

                                LogUtils.d(
                                        "onMagnificationChanged:\n scale="
                                        + scale
                                        + "\n"
                                        + "centerX="
                                        + centerX
                                        + "\n"
                                        + "centerY="
                                        + centerY
                                        + "\n"
                                        + "region="
                                        + region.toString());
                            }
                        });

        /*AssistUtil.assistService
                .getMagnificationController()
                .setScale(2, true);

        sleep(4000);

        LogUtils.d(
                "onReceive: setCenter="
                        + AssistUtil.assistService
                        .getMagnificationController()
                        .setCenter(
                                ScreenUtils.getAppScreenWidth()
                                        >> 2,
                                ScreenUtils.getAppScreenHeight()
                                        >> 2,
                                true));

        sleep(4000);

        AssistUtil.assistService
                .getMagnificationController()
                .reset(true);*/
    }

    /*void switchEventType(int type){
        switch (type){
            case
        }
    }*/

    @Override
    protected boolean onGesture(int gestureId) {
        LogUtils.i("onGesture: " + gestureId);
        return true;
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        LogUtils.i("onKeyEvent: " + event.toString());
        return false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //        LogUtils.d("getEventType: "+event.getEventType());

        //                LogUtils.d(event.toString());
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
            //        if(AccessibilityEvent.TYPE_WINDOWS_CHANGED == event.getEventType()){  //这个坑B

            // 可以检索到getRootInActiveWindow检索不到的内容信息
            /*StringBuilder stringBuilder = new StringBuilder();
            AssistUtil.analysisPacketInfo(event.getSource(), stringBuilder);
            LogUtils.d("getSource: " + stringBuilder.toString());*/

            CURRENT_ACTIVITY.getAndSet(
                    event.getClassName() != null ? event.getClassName().toString().intern() : "");
            CURRENT_PACKAGE.getAndSet(
                    event.getPackageName() != null
                            ? event.getPackageName().toString().intern()
                            : "");

            LogUtils.i("当前页面包名：" + event.getPackageName());
            LogUtils.i("当前页面Activity：" + event.getClassName());

            // 跳转到获取悬浮窗授权页面
            if ("com.android.settings".equals(event.getPackageName().toString())
                && "com.android.settings.Settings$AppDrawOverlaySettingsActivity"
                        .equals(event.getClassName().toString())) {
                AccessibilityNodeInfo accessibilityNodeInfo =
                        AssistUtil.getFirstNodeInfoByViewId(
                                SystemWidgetId.SETTING_SUB_SWITCH_WIDGET);
                LogUtils.i(accessibilityNodeInfo);

            }
            // 获取截屏权限页面弹出
            else if ("com.android.systemui".equals(event.getPackageName().toString())
                     && "com.android.systemui.media.MediaProjectionPermissionActivity"
                             .equals(event.getClassName().toString())) {
                AccessibilityNodeInfo accessibilityNodeInfo =
                        AssistUtil.getFirstNodeInfoByViewId(SystemWidgetId.SCREEN_SHOT_NO_ASK);
                LogUtils.i(accessibilityNodeInfo);
            }
            // 获取动态权限弹窗页面
            else if ("com.android.packageinstaller".equals(event.getPackageName().toString())
                     && "com.android.packageinstaller.permission.ui.GrantPermissionsActivity"
                             .equals(event.getClassName().toString())) {
                AccessibilityNodeInfo accessibilityNodeInfo =
                        AssistUtil.getFirstNodeInfoByViewId(
                                SystemWidgetId.PERMISSION_DO_NOT_ASK_CHECKBOX);
                LogUtils.i(accessibilityNodeInfo);
            }
        }
    }

    @Override
    public void onInterrupt() {}

    private void setScreenOn() {
        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock =
                powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Vuctrl");
        wakeLock.acquire();
    }
}