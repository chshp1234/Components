//package com.common.base.ui.webview;
//
//import static android.app.Activity.RESULT_OK;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.FrameLayout;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.blankj.utilcode.util.LogUtils;
//import com.blankj.utilcode.util.StringUtils;
//import com.blankj.utilcode.util.UriUtils;
//import com.just.agentweb.AgentWeb;
//import com.just.agentweb.AgentWebSettingsImpl;
//import com.just.agentweb.DefaultWebClient;
//import com.just.agentweb.IAgentWebSettings;
//import com.just.agentweb.WebViewClient;
//
//public class WebViewUi extends FrameLayout {
//    private static final int CHOOSE_FILE_SYSTEM = 10001;
//    private static final int CHOOSE_FILE_CAMERA = 10002;
//    private static final int CHOOSE_FILE_CROP = 10010;
//
//    private Activity mContext;
//    private AgentWeb mAgentWeb;
//
//    private OnWebManageListener onWebManageListener;
//
//    private ValueCallback<Uri[]> mUploadMessage;
//    public static final String IMAGE_UNSPECIFIED = "image/*"; // 随意图片类型
//
//    public WebViewUi(@NonNull Activity context) {
//        this((Context) context);
//    }
//
//    public WebViewUi(@NonNull Activity context, OnWebManageListener onWebManageListener) {
//        this((Context) context);
//        this.onWebManageListener = onWebManageListener;
//    }
//
//    private WebViewUi(@NonNull Context context) {
//        this(context, null);
//    }
//
//    private WebViewUi(@NonNull Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    private WebViewUi(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mContext = (Activity) context;
//        initWebView();
//    }
//
//    private void initWebView() {
//
//        AgentWeb.IndicatorBuilder indicatorBuilder =
//                AgentWeb.with(mContext)
//                        .setAgentWebParent(
//                                this,
//                                new LayoutParams(
//                                        ViewGroup.LayoutParams.MATCH_PARENT,
//                                        ViewGroup.LayoutParams.MATCH_PARENT));
//
//        AgentWeb.CommonBuilder commonBuilder = setIndicator(indicatorBuilder);
//
//        mAgentWeb = setCommonSetting(commonBuilder).createAgentWeb().ready().get();
//    }
//
//    /**
//     * 设置进度条
//     *
//     * @param indicatorBuilder the indicator builder
//     * @return the indicator
//     */
//    protected AgentWeb.CommonBuilder setIndicator(AgentWeb.IndicatorBuilder indicatorBuilder) {
//        return indicatorBuilder.useDefaultIndicator(-1, 3);
//    }
//
//    /**
//     * 设置普通选项
//     *
//     * @param commonBuilder the common builder
//     * @return the common setting
//     */
//    protected AgentWeb.CommonBuilder setCommonSetting(AgentWeb.CommonBuilder commonBuilder) {
//
//        WebViewClient webViewClient =
//                new MyWebViewClient() {
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        super.onPageFinished(view, url);
//                        if (onWebManageListener != null) {
//                            onWebManageListener.onPageFinished(view, url);
//                        }
//                    }
//
//                    @Override
//                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                        super.onPageStarted(view, url, favicon);
//                        if (onWebManageListener != null) {
//                            onWebManageListener.onPageStarted(view, url, favicon);
//                        }
//                    }
//
//                    @Override
//                    public boolean shouldOverrideUrlLoading(
//                            WebView view, WebResourceRequest request) {
//                        if (onWebManageListener != null) {
//                            onWebManageListener.shouldOverrideUrlLoading(view, request);
//                        }
//                        return super.shouldOverrideUrlLoading(view, request);
//                    }
//                };
//
//        if (errView() != -1 && retryBtn() != -1) {
//            commonBuilder.setMainFrameErrorView(errView(), retryBtn());
//        }
//
//        return commonBuilder
//                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//                .setOpenOtherPageWays(
//                        DefaultWebClient.OpenOtherPageWays.DERECT) // 打开其他应用时，弹窗咨询用户是否前往其他应用
//                .setWebViewClient(webViewClient)
//                .setWebChromeClient(new ChooseFileWebChromeClient())
//                .setAgentWebWebSettings(new MyAgentSetting());
//    }
//
//    public void addJavaObject(String k, Object v) {
//        mAgentWeb.getJsInterfaceHolder().addJavaObject(k, v);
//    }
//
//    /**
//     * 错误页面
//     *
//     * @return the int
//     */
//    protected int errView() {
//        return -1;
//    }
//
//    /**
//     * 重试按钮id
//     *
//     * @return the int
//     */
//    protected int retryBtn() {
//        return -1;
//    }
//
//    public void goUrl(String url) {
//        if (StringUtils.isTrimEmpty(url)) {
//            return;
//        }
//        mAgentWeb.getUrlLoader().loadUrl(url);
//    }
//
//    public void reLoad() {
//        mAgentWeb.getUrlLoader().reload();
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        switch (requestCode) {
//            case CHOOSE_FILE_SYSTEM:
//                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
//                if (result == null) {
//                    mUploadMessage.onReceiveValue(null);
//                    mUploadMessage = null;
//                    return;
//                }
//
//                String path = UriUtils.uri2File(result).getAbsolutePath();
//                if (TextUtils.isEmpty(path)) {
//                    mUploadMessage.onReceiveValue(null);
//                    mUploadMessage = null;
//                    return;
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                    mUploadMessage.onReceiveValue(new Uri[] {result});
//                }
//                mUploadMessage = null;
//                break;
//            case CHOOSE_FILE_CAMERA:
//                break;
//            case CHOOSE_FILE_CROP:
//                break;
//        }
//    }
//
//    public AgentWeb getmAgentWeb() {
//        return mAgentWeb;
//    }
//
//    public void setmAgentWeb(AgentWeb mAgentWeb) {
//        this.mAgentWeb = mAgentWeb;
//    }
//
//    public static class MyWebViewClient extends WebViewClient {
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public void onPageCommitVisible(WebView view, String url) {
//            super.onPageCommitVisible(view, url);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            LogUtils.d(
//                    "shouldOverrideUrlLoading: Url="
//                            + request.getUrl()
//                            + "\n"
//                            + "Method:"
//                            + request.getMethod()
//                            + "\n"
//                            + "RequestHeaders:"
//                            + request.getRequestHeaders());
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//    }
//
//    public class ChooseFileWebChromeClient extends WebChromeClient {
//
//        @Override
//        public boolean onShowFileChooser(
//                WebView webView,
//                ValueCallback<Uri[]> filePathCallback,
//                FileChooserParams fileChooserParams) {
//            if (mUploadMessage != null) {
//                mUploadMessage.onReceiveValue(null);
//            }
//            mUploadMessage = filePathCallback;
//            Intent intent = new Intent(Intent.ACTION_PICK, null);
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
//            mContext.startActivityForResult(intent, CHOOSE_FILE_SYSTEM);
//            return true;
//        }
//    }
//
//    public static class MyAgentSetting extends AgentWebSettingsImpl {
//
//        @Override
//        public IAgentWebSettings toSetting(WebView webView) {
//            super.toSetting(webView);
//            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//            //            webView.getSettings().setUserAgentString();
//            return this;
//        }
//    }
//
//    public interface OnWebManageListener {
//        void onPageFinished(WebView view, String url);
//
//        void onPageStarted(WebView view, String url, Bitmap favicon);
//
//        boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);
//    }
//}
