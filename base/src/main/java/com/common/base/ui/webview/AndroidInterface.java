//package com.common.base.ui.webview;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.webkit.JavascriptInterface;
//
//import androidx.annotation.Keep;
//
//import com.blankj.utilcode.util.LogUtils;
//import com.just.agentweb.AgentWeb;
//
//@Keep
//public class AndroidInterface {
//
//    private Handler deliver = new Handler(Looper.getMainLooper());
//    private AgentWeb agent;
//    private Activity context;
//    private HoldUpListener holdUpListener;
//
//    public AndroidInterface(AgentWeb agent, Activity context) {
//        this.agent = agent;
//        this.context = context;
//    }
//
//    public AndroidInterface(AgentWeb agent, Activity context, HoldUpListener holdUpListener) {
//        this.agent = agent;
//        this.context = context;
//        this.holdUpListener = holdUpListener;
//    }
//
//    @JavascriptInterface
//    public void callFinish() {
//
//        deliver.post(
//                () -> {
//                    if (holdUpListener != null) {
//                        holdUpListener.callFinish();
//                    } else {
//                        context.finish();
//                    }
//                    Log.i("callFinish", "finish");
//                });
//
//        Log.i("callFinish", "Thread:" + Thread.currentThread());
//    }
//
//    @JavascriptInterface
//    public void callBack() {
//        deliver.post(
//                () -> {
//                    if (agent != null && agent.getIEventHandler().back()) {
//                        Log.i("callBack", "go back");
//                    } else {
//                        Log.i("callBack", "finish");
//                        if (holdUpListener != null) {
//                            holdUpListener.callFinish();
//                        } else {
//                            context.finish();
//                        }
//                    }
//                });
//
//        Log.i("callBack", "Thread:" + Thread.currentThread());
//    }
//
//    @JavascriptInterface
//    public void gotoBrowse(String url) {
//        deliver.post(
//                () -> {
//                    try {
//                        LogUtils.d("url:" + url);
//                        Uri uri = Uri.parse(url);
//                        Intent intent = new Intent("android.intent.action.VIEW", uri);
//                        intent.addCategory("android.intent.category.BROWSABLE");
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    } catch (Exception e) {
//                        LogUtils.e(e);
//                    }
//                });
//
//        Log.i("gotoBrowse", "Thread:" + Thread.currentThread());
//    }
//
//    public interface HoldUpListener {
//        void callFinish();
//    }
//}
