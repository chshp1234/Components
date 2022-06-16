package com.common.base.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.common.base.R;

import java.lang.reflect.Field;
import java.util.Arrays;


/**
 * @author csp
 * @date 2017/9/20
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";

    protected final String    TAG = getClass().getSimpleName();
    protected       ImageView mToolbarBack;
    protected       TextView  mToolbarTitle;
    protected       TextView  mToolbarSubTitle;
    protected       ImageView mToolbarSubTitleImage;
    protected       Toolbar   toolBar;

    private int          idToolBarSubImage      = 0;
    private int          idTollBarBackImage     = 0;
    private int          idToolBarColor         = 0;
    private int          idToolBarSubTitleColor = 0;
    private int          idToolBarTitleColor    = 0;
    private CharSequence titleToolBarSub        = null;
    private CharSequence titleToolBar           = null;

    protected boolean isShowSubTitle      = false;
    protected boolean isShowSubTitleImage = false;
    protected boolean isShowTitle         = true;
    protected boolean isShowBack          = true;
    protected boolean isShowToolBar       = true;

    private ProgressDialog mProgress;
    private CharSequence   progressMsg = null;

    private InputMethodManager imm;

    /** 回调函数 */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // 去除半透明状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 一般配合fitsSystemWindows()使用,
        // 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
        //        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);

        setContent();
        //        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        if (isInitStateBarEnabled()) {
            initStateBar();
        }

        //        initDialog();

        initData();
        initView();

        initToolBar();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.imm = null;

        /*
         在退出使用InputMethodManager的Activity时，调用fixFocusedViewLeak方法解决Android InputMethodManager
         导致的内存泄露
        */
        //        FixInputMethodManagerLeaksUtils.fixFocusedViewLeak(getApplication());
        fixInputMethodManagerLeak(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SCREEN_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    ToastUtils.showShort("授权截屏失败");
                    return;
                }
                ScreenShotUtils.getInstance().init(data);

                break;
            case REQUEST_ACCESSIBILITY_CODE:
                if (!AccessibilityCheckUtils.isAccessibilityEnable(
                        getApplicationContext(),
                        AppUtils.getAppPackageName()
                                + "/"
                                + AutoScriptService.class.getCanonicalName())) {
                    ToastUtils.showShort("授权无障碍服务失败");
                }
                break;
            case REQUEST_FLOATING_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        ToastUtils.showShort("授权悬浮窗失败");
                    } else {
                        ToastUtils.showShort("授权悬浮窗成功");
                        startService(new Intent(this, FloatWindowService.class));
                    }
                }
                break;
            default:
                break;
        }
    }*/

    /**
     * 防止inputMethodManager造成的内存泄漏
     *
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm =
                (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field    f;
        Object   objGet;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                objGet = f.get(imm);
                if (objGet != null && objGet instanceof View) {
                    View vGet = (View) objGet;
                    // 被InputMethodManager持有引用的context是想要目标销毁的
                    if (vGet.getContext() == destContext) {
                        // 置空，破坏掉path to gc节点
                        f.set(imm, null);
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
//                t.printStackTrace();
            }
        }
    }

    public ProgressDialog getProgress() {
        if (mProgress == null) {
            initProgressDialog();
        }
        return mProgress;
    }

    protected void initStateBar() {
        // 在BaseActivity里初始化
        //        mImmersionBar = ImmersionBar.with(this);
        //        mImmersionBar.statusBarColor(R.color.white).statusBarDarkFont(true, 0.2f).init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
            //            BarUtils.setStatusBarColor(this, ContextCompat.getColor(this,
            // R.color.white), 0, true);
        } else {
            setStatusBarColor(
                    this, ContextCompat.getColor(this, R.color.translucence_white), false);
        }
    }

    public static View setStatusBarColor(@NonNull final Activity activity,
                                         @ColorInt final int color,
                                         final boolean isDecor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return null;
        transparentStatusBar(activity);
        return applyStatusBarColor(activity, color, isDecor);
    }

    private static View applyStatusBarColor(@NonNull final Activity activity,
                                            final int color,
                                            boolean isDecor) {
        return applyStatusBarColor(activity.getWindow(), color, isDecor);
    }

    private static View applyStatusBarColor(@NonNull final Window window,
                                            final int color,
                                            boolean isDecor) {
        ViewGroup parent = isDecor ?
                (ViewGroup) window.getDecorView() :
                (ViewGroup) window.findViewById(android.R.id.content);
        View fakeStatusBarView = parent.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            fakeStatusBarView = createStatusBarView(window.getContext(), color);
            parent.addView(fakeStatusBarView);
        }
        return fakeStatusBarView;
    }

    private static View createStatusBarView(@NonNull final Context context,
                                            final int color) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context)));
        statusBarView.setBackgroundColor(color);
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources  = context.getResources();
        int       resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static void transparentStatusBar(@NonNull final Activity activity) {
        transparentStatusBar(activity.getWindow());
    }

    public static void transparentStatusBar(@NonNull final Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            int vis    = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(option | vis);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Set the status bar's light mode.
     *
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    public void setStatusBarLightMode(final boolean isLightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int  vis       = decorView.getSystemUiVisibility();
            if (isLightMode) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    /**
     * 是否可以使用沉浸式 Is bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isInitStateBarEnabled() {
        return true;
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    protected void setContent() {
        Object layout = getLayout();
        if (layout instanceof Integer) {
            setContentView((Integer) layout);
        } else if (layout instanceof View) {
            setContentView((View) layout);
        }
    }

    void initProgressDialog() {
        mProgress = new ProgressDialog(this, R.style.customProgressBarAlert);
        if (!TextUtils.isEmpty(progressMsg)) {
            mProgress.setMessage(progressMsg);
        }
        mProgress.setCanceledOnTouchOutside(true);
    }

    void initToolBar() {
        toolBar = findViewById(R.id.toolbar);
        mToolbarBack = findViewById(R.id.toolbar_back);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        mToolbarSubTitleImage = findViewById(R.id.toolbar_subtitle_image);
        setSupportActionBar(toolBar);
        if (mToolbarTitle != null) {
            // getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            // 设置默认的标题不显示
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        showToolBar();
    }

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getToolbarTitle() {
        return mToolbarTitle;
    }

    /**
     * 获取头部子标题的TextView
     *
     * @return
     */
    public TextView getSubTitle() {
        return mToolbarSubTitle;
    }

    /**
     * 设置加载进度条文字
     *
     * @param msg
     */
    public void setProgressMsg(CharSequence msg) {
        progressMsg = msg;
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    public void setToolBarTitle(CharSequence title) {
        titleToolBar = title;
    }

    /**
     * 设置头部子标题
     *
     * @param title
     */
    public void setToolBarSubTitle(CharSequence title) {
        titleToolBarSub = title;
    }

    /**
     * 设置头部标题颜色
     *
     * @param color
     */
    public void setToolBarTitleColor(int color) {
        idToolBarTitleColor = color;
    }

    /**
     * 设置头部子标题颜色
     *
     * @param color
     */
    public void setToolBarSubTitleColor(int color) {
        idToolBarSubTitleColor = color;
    }

    public void setToolBarColor(int color) {
        idToolBarColor = color;
    }

    /**
     * 设置后退按钮图片
     *
     * @return
     */
    public void setTollBarBackImage(int resId) {
        idTollBarBackImage = resId;
    }

    /** 设置头部子标题图片 */
    public void setToolBarSubImage(int resources) {
        idToolBarSubImage = resources;
    }

    /** 设置显示toolbar */
    private void showToolBar() {
        if (null != toolBar) {
            if (isShowToolBar()) {
                toolBar.setVisibility(View.VISIBLE);

                if (idTollBarBackImage != 0) {
                    mToolbarBack.setImageResource(idTollBarBackImage);
                }

                if (idToolBarSubImage != 0) {
                    mToolbarSubTitleImage.setImageResource(idToolBarSubImage);
                }

                if (idToolBarColor != 0) {
                    toolBar.setBackgroundColor(idToolBarColor);
                }

                if (idToolBarSubTitleColor != 0) {
                    mToolbarSubTitle.setTextColor(idToolBarSubTitleColor);
                }

                if (idToolBarTitleColor != 0) {
                    mToolbarTitle.setTextColor(idToolBarTitleColor);
                }

                if (!TextUtils.isEmpty(titleToolBar)) {
                    mToolbarTitle.setText(titleToolBar);
                }

                if (!TextUtils.isEmpty(titleToolBarSub)) {
                    mToolbarSubTitle.setText(titleToolBarSub);
                }

                showBack();
                showTitle();
                showSubTitle();
                showSubTitleImage();
            } else {
                toolBar.setVisibility(View.GONE);
            }
        } else {

        }
    }

    /** 显示头部子标题 */
    private void showSubTitle() {
        if (mToolbarSubTitle != null) {
            if (isShowSubTitle()) {
                mToolbarSubTitle.setVisibility(View.VISIBLE);
            } else {
                mToolbarSubTitle.setVisibility(View.GONE);
            }
        }
    }

    /** 显示头部子标题图片按钮 */
    private void showSubTitleImage() {
        if (mToolbarSubTitleImage != null) {
            if (isShowSubTitleImage()) {
                mToolbarSubTitleImage.setVisibility(View.VISIBLE);
            } else {
                mToolbarSubTitleImage.setVisibility(View.GONE);
            }
        }
    }

    /** 显示后退按钮 */
    private void showBack() {
        if (mToolbarBack != null) {
            if (isShowBack()) {
                mToolbarBack.setVisibility(View.VISIBLE);
                mToolbarBack.setOnClickListener(view -> onBackPressed());

            } else {
                mToolbarBack.setVisibility(View.GONE);
            }
        }
    }

    /** 显示主标题 */
    private void showTitle() {
        if (mToolbarTitle != null) {
            if (isShowTitle()) {
                mToolbarTitle.setVisibility(View.VISIBLE);
            } else {
                mToolbarTitle.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 是否显示toolbar
     *
     * @return
     */
    private boolean isShowToolBar() {
        return isShowToolBar;
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    private boolean isShowBack() {
        return isShowBack;
    }

    /**
     * 是否显示头部子标题
     *
     * @return
     */
    protected boolean isShowSubTitle() {
        return isShowSubTitle;
    }

    /**
     * 是否显示头部子标题图片按钮
     *
     * @return
     */
    protected boolean isShowSubTitleImage() {
        return isShowSubTitleImage;
    }

    /**
     * 是否显示标题
     *
     * @return
     */
    protected boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowSubTitle(boolean showSubTitle) {
        isShowSubTitle = showSubTitle;
    }

    public void setShowSubTitleImage(boolean showSubTitleImage) {
        isShowSubTitleImage = showSubTitleImage;
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    public void setShowBack(boolean showBack) {
        isShowBack = showBack;
    }

    public void setShowToolBar(boolean showToolBar) {
        isShowToolBar = showToolBar;
    }

    protected void registerBroadcastReceiver(
            BroadcastReceiver receiver, String permission, String... actions) {
        IntentFilter filter = new IntentFilter();
        if (actions != null && actions.length > 0) {
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        registerReceiver(receiver, filter, permission, null);
    }

    protected void unregisterBroadcastReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    protected void registerLocalBroadcastReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        if (actions != null && actions.length > 0) {
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    protected void unregisterLocalBroadcastReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    /** 初始化数据 */
    protected void initData() {}

    /** 初始化控件 */
    protected abstract void initView();

    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract Object getLayout();
}
