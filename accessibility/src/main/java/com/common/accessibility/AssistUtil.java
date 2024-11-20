package com.common.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Created by Administrator on 2018/5/28. */
public class AssistUtil {
    /** The constant assistService. */
    public static AccessibilityService assistService;

    /** The constant handler. */
    public static Handler handler =
            new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Log.i(TAG, "handleMessage: Thread is " + Thread.currentThread().getName());
                    super.handleMessage(msg);
                }
            };

    private static final String TAG        = "AssistUtil";
    private static       long   ONE_SECOND = 1000L;

    /**
     获取根节点 Gets root node.

     @return the root node
     */
    public static AccessibilityNodeInfo getRootNode() {
        if (assistService != null) {
            AccessibilityNodeInfo rootInActiveWindow = assistService.getRootInActiveWindow();
            if (rootInActiveWindow != null) {
                //                rootInActiveWindow.refresh();
                return rootInActiveWindow;
            }
        }

        return null;
    }

    /**
     Gets window.

     @return the window
     */
    public static List<AccessibilityWindowInfo> getWindow() {
        if (assistService != null) {
            return assistService.getWindows();
        }
        return null;
    }

    /**
     Node perform info scroll forward boolean.

     @param nodeInfo the node info

     @return the boolean
     */
    public static boolean nodePerformInfoScrollForward(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
        return false;
    }

    /**
     Node perform info scroll backward boolean.

     @param nodeInfo the node info

     @return the boolean
     */
    public static boolean nodePerformInfoScrollBackward(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
        return false;
    }

    /**
     Node perform info click boolean.

     @param nodeInfo the node info

     @return the boolean
     */
    public static boolean nodePerformInfoClick(AccessibilityNodeInfo nodeInfo) {
        while (nodeInfo != null && !nodeInfo.isClickable()) {
            nodeInfo = nodeInfo.getParent();
        }
        if (nodeInfo != null) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    /**
     Node perform info long click boolean.

     @param nodeInfo the node info

     @return the boolean
     */
    public static boolean nodePerformInfoLongClick(AccessibilityNodeInfo nodeInfo) {
        while (nodeInfo != null && !nodeInfo.isLongClickable()) {
            nodeInfo = nodeInfo.getParent();
        }
        if (nodeInfo != null) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoClick(
            AccessibilityNodeInfo accessibilityNodeInfo) {
        if (accessibilityNodeInfo != null) {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoClick(x, y);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoClick(Rect rect) {
        if (rect != null) {
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoClick(x, y);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoLongClick(
            AccessibilityNodeInfo accessibilityNodeInfo, long duration) {
        if (accessibilityNodeInfo != null) {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            return nodeDispatchGestureInfoLongClick(rect, duration);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoLongClick(Rect rect, long duration) {
        if (rect != null) {
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoLongClick(x, y, duration);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoClick(float x, float y) {
        if (assistService == null) {
            return false;
        } else {
            return nodeDispatchGestureInfoLongClick(x, y, 50);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoLongClick(float x, float y, long duration) {
        if (assistService == null) {
            return false;
        } else {
            GestureDescription.Builder gdb = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x, y);
            gdb.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
            return assistService.dispatchGesture(gdb.build(), null, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoScrollForward(
            AccessibilityNodeInfo accessibilityNodeInfo, long during) {
        if (accessibilityNodeInfo == null) {
            return false;
        } else {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoScroll(
                    x, y + rect.height() / 4, x, y - rect.height() / 4, during);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoScrollBackward(
            AccessibilityNodeInfo accessibilityNodeInfo, long during) {
        if (accessibilityNodeInfo == null) {
            return false;
        } else {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoScroll(
                    x, y - rect.height() / 4, x, y + rect.height() / 4, during);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoScrollLeft(
            AccessibilityNodeInfo accessibilityNodeInfo, long during) {
        if (accessibilityNodeInfo == null) {
            return false;
        } else {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoScroll(
                    x + rect.width() / 4, y, x - rect.width() / 4, y, during);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoScrollRight(
            AccessibilityNodeInfo accessibilityNodeInfo, long during) {
        if (accessibilityNodeInfo == null) {
            return false;
        } else {
            Rect rect = new Rect();
            accessibilityNodeInfo.getBoundsInScreen(rect);
            float x = rect.left + rect.width() / 2;
            float y = rect.top + rect.height() / 2;
            if (x < 0 || y < 0) {
                return false;
            }
            return nodeDispatchGestureInfoScroll(
                    x - rect.width() / 4, y, x + rect.width() / 4, y, during);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean nodeDispatchGestureInfoScroll(
            float fromX, float fromY, float toX, float toY, long during) {
        if (assistService == null) {
            return false;
        } else {
            GestureDescription.Builder gdb = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(fromX, fromY);
            path.lineTo(toX, toY);
            gdb.addStroke(new GestureDescription.StrokeDescription(path, 0, during));
            return assistService.dispatchGesture(gdb.build(), null, null);
        }
    }

    /**
     Sets edit text.

     @param editNodeInfo the edit node info
     @param text         the text

     @return the edit text
     */
    public static boolean setEditText(AccessibilityNodeInfo editNodeInfo, String text) {
        if (editNodeInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            return editNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else {
            return false;
        }
    }

    /* 通过粘贴板输入文字 */
    public static void sendTextForEditText(AccessibilityNodeInfo editNode, String text) {
        if (editNode != null && assistService != null) {
            ClipboardManager clipboard =
                    (ClipboardManager) assistService.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", text);
            clipboard.setPrimaryClip(clip);
            // 获得焦点
            editNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            // 粘贴内容
            editNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    /**
     Gets first node info by view id.

     @param viewId the view id

     @return the first node info by view id
     */
    public static AccessibilityNodeInfo getFirstNodeInfoByViewId(String viewId) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || TextUtils.isEmpty(viewId)) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByViewId :  getRootNode is null ,viewId :" + viewId);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        AccessibilityNodeInfo accessibilityNodeInfo =
                nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
        if (accessibilityNodeInfo == null) {
        }
        return accessibilityNodeInfo;
    }

    /**
     Gets first node info by text.

     @param name the name

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getFirstNodeInfoByText(String name) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || name == null) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByText :  getRootNode is null ,name :" + name);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(name);
        AccessibilityNodeInfo accessibilityNodeInfo =
                nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
        if (accessibilityNodeInfo == null) {
        }
        return accessibilityNodeInfo;
    }

    /**
     Gets first node info by view id and text.

     @param viewId the view id

     @return the first node info by view id
     */
    public static AccessibilityNodeInfo getFirstNodeInfoByIdAndText(String viewId, String text) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || TextUtils.isEmpty(viewId)) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByViewId :  getRootNode is null ,viewId :" + viewId);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        for (int i = 0, l = nodeInfos.size(); i < l; i++) {
            if (getNodeInfoTextByNode(nodeInfos.get(i)).equals(text)) {
                return nodeInfos.get(i);
            }
        }
        return null;
    }

    /**
     Gets first node info by text.

     @param name the name

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getFirstExactNodeInfoByText(String name) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByText :  getRootNode is null ,name :" + name);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(name);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            for (AccessibilityNodeInfo textNode : nodeInfos) {
                if (getNodeInfoTextByNode(textNode).equals(name)) {
                    return textNode;
                }
            }
        }
        return null;
    }

    /**
     Gets first node info by text.

     @param name the name

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getFirstExactNodeInfoByText(
            String name, AccessibilityNodeInfo parent) {
        if (parent == null) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByText :  getRootNode is null ,name :" + name);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = parent.findAccessibilityNodeInfosByText(name);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            for (AccessibilityNodeInfo textNode : nodeInfos) {
                if (getNodeInfoTextByNode(textNode).equals(name)) {
                    return textNode;
                }
            }
        }
        return null;
    }

    /**
     Gets first node info by view id.

     @param viewId the view id
     @param parent the parent

     @return the first node info by view id
     */
    public static AccessibilityNodeInfo getFirstNodeInfoByViewId(
            String viewId, @NonNull AccessibilityNodeInfo parent) {
        if (parent == null || TextUtils.isEmpty(viewId)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = parent.findAccessibilityNodeInfosByViewId(viewId);
        return nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
    }

    /**
     Gets first node info by text.

     @param name   the name
     @param parent the parent

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getFirstNodeInfoByText(
            String name, AccessibilityNodeInfo parent) {
        if (parent == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = parent.findAccessibilityNodeInfosByText(name);
        return nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
    }

    /**
     Gets first node info by view id.

     @param viewId the view id

     @return the first node info by view id
     */
    public static AccessibilityNodeInfo getEndNodeInfoByViewId(String viewId) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || TextUtils.isEmpty(viewId)) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByViewId :  getRootNode is null ,viewId :" + viewId);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        AccessibilityNodeInfo accessibilityNodeInfo =
                nodeInfos.size() > 0 ? nodeInfos.get(nodeInfos.size() - 1) : null;
        if (accessibilityNodeInfo == null) {
        }
        return accessibilityNodeInfo;
    }

    /**
     Gets first node info by text.

     @param name the name

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getEndNodeInfoByText(String name) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByText :  getRootNode is null ,name :" + name);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(name);
        AccessibilityNodeInfo accessibilityNodeInfo =
                nodeInfos.size() > 0 ? nodeInfos.get(nodeInfos.size() - 1) : null;
        if (accessibilityNodeInfo == null) {
        }
        return accessibilityNodeInfo;
    }

    /**
     Gets first node info by view id.

     @param viewId the view id
     @param parent the parent

     @return the first node info by view id
     */
    public static AccessibilityNodeInfo getEndNodeInfoByViewId(
            String viewId, @NonNull AccessibilityNodeInfo parent) {
        if (parent == null || TextUtils.isEmpty(viewId)) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = parent.findAccessibilityNodeInfosByViewId(viewId);
        return nodeInfos.size() > 0 ? nodeInfos.get(nodeInfos.size() - 1) : null;
    }

    /**
     Gets first node info by text.

     @param name   the name
     @param parent the parent

     @return the first node info by text
     */
    public static AccessibilityNodeInfo getEndNodeInfoByText(
            String name, @NonNull AccessibilityNodeInfo parent) {
        if (parent == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = parent.findAccessibilityNodeInfosByText(name);
        return nodeInfos.size() > 0 ? nodeInfos.get(nodeInfos.size() - 1) : null;
    }

    public static AccessibilityNodeInfo getAfterNodeInfoByViewId(String viewId, int index) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null || TextUtils.isEmpty(viewId)) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d("getFirstNodeInfoByViewId :  getRootNode is null ,viewId :" + viewId);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            if (index <= 0) {
                return nodeInfos.get(0);
            } else if (nodeInfos.size() > index) {
                return nodeInfos.get(index);
            } else {
                return nodeInfos.get(nodeInfos.size() - 1);
            }
        } else {
            return null;
        }
    }

    public static AccessibilityNodeInfo getAfterNodeInfoByViewText(String viewText, int index) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
            /*sleep(ONE_SECOND);
            getFocus();
            sleep(ONE_SECOND);
            rootNode = getRootNode();
            if (rootNode == null) {
                LogUtils.d(
                        "getFirstNodeInfoByViewId :  getRootNode is null ,viewText :" + viewText);
                return null;
            }*/
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(viewText);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            if (index <= 0) {
                return nodeInfos.get(0);
            } else if (nodeInfos.size() > index) {
                return nodeInfos.get(index);
            } else {
                return nodeInfos.get(nodeInfos.size() - 1);
            }
        } else {
            return null;
        }
    }

    /**
     Gets node infos by view id.

     @param viewId the view id

     @return the node infos by view id
     */
    public static List<AccessibilityNodeInfo> getNodeInfosByViewId(String viewId) {
        AccessibilityNodeInfo root = getRootNode();
        if (root == null || TextUtils.isEmpty(viewId)) {
            return null;
        }
        return root.findAccessibilityNodeInfosByViewId(viewId);
    }

    /**
     Gets node infos by text.

     @param text the text

     @return the node infos by text
     */
    public static List<AccessibilityNodeInfo> getNodeInfosByText(String text) {
        AccessibilityNodeInfo root = getRootNode();
        if (root == null) {
            return null;
        }
        return root.findAccessibilityNodeInfosByText(text);
    }

    /**
     Gets node infos by view id.

     @param viewId the view id
     @param parent the parent

     @return the node infos by view id
     */
    public static List<AccessibilityNodeInfo> getNodeInfosByViewId(
            String viewId, AccessibilityNodeInfo parent) {
        if (parent == null || TextUtils.isEmpty(viewId)) {
            return null;
        }
        return parent.findAccessibilityNodeInfosByViewId(viewId);
    }

    /**
     Gets node infos by text.

     @param text   the text
     @param parent the parent

     @return the node infos by text
     */
    public static List<AccessibilityNodeInfo> getNodeInfosByText(
            String text, AccessibilityNodeInfo parent) {
        if (parent == null) {
            return null;
        }
        return parent.findAccessibilityNodeInfosByText(text);
    }

    /**
     Gets node info by parent and child.

     @param currentNode the current node
     @param parentDepth the parent depth
     @param child       the child

     @return the node info by parent and child
     */
    public static AccessibilityNodeInfo getNodeInfoByParentAndChild(
            AccessibilityNodeInfo currentNode, @IntRange(from = 0) int parentDepth, int... child) {
        AccessibilityNodeInfo accessibilityNodeInfo;
        if (currentNode == null) {
            return null;
        } else if (getRootNode() == null) {
            return null;
        } else {
            accessibilityNodeInfo = currentNode;
            if (parentDepth > 0) {
                for (int i = 0; i < parentDepth; i++) {
                    if (accessibilityNodeInfo == null) {
                        return null;
                    }
                    accessibilityNodeInfo = accessibilityNodeInfo.getParent();
                }
            }
            if (child != null && child.length > 0) {
                for (int i = 0, l = child.length; i < l; i++) {
                    if (accessibilityNodeInfo == null) {
                        return null;
                    }

                    if (child[i] < accessibilityNodeInfo.getChildCount()) {

                        try {
                            accessibilityNodeInfo = accessibilityNodeInfo.getChild(child[i]);
                        } catch (Exception e) {
                            accessibilityNodeInfo = null;
                        }

                    } else {
                        break;
                    }
                }
            }
        }
        return accessibilityNodeInfo;
    }

    /**
     在某个节点中（父节点）寻找最后一个子（孙）节点

     @param parent the parent

     @return the accessibility node info
     */
    public static AccessibilityNodeInfo findLastNodeInGivenParent(AccessibilityNodeInfo parent) {
        if (parent == null) {
            return null;
        }
        if (parent.getChildCount() == 0) {
            return parent;
        }
        return findLastNodeInGivenParent(parent.getChild(parent.getChildCount() - 1));
    }

    /** 在指定节点下寻找相应控件名字的节点. */
    public static AccessibilityNodeInfo getNodeByWidgetName(String widget) {
        return getNodeByWidgetName(getRootNode(), widget, null);
    }

    /** 在指定节点下寻找相应控件名字的节点. */
    public static AccessibilityNodeInfo getNodeByWidgetName(String widget, String text) {
        return getNodeByWidgetName(getRootNode(), widget, text);
    }

    /** 在指定节点下寻找相应控件名字的节点. */
    public static AccessibilityNodeInfo getNodeByWidgetName(
            AccessibilityNodeInfo info, String widget) {
        return getNodeByWidgetName(info, widget, null);
    }

    /**
     在指定节点下寻找相应控件名字的节点.

     @param parent the info
     */
    public static AccessibilityNodeInfo getNodeByWidgetName(
            AccessibilityNodeInfo parent, String widget, String text) {
        AccessibilityNodeInfo nodeInfo;
        if (parent == null || TextUtils.isEmpty(widget)) {
            return null;
        }
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (parent.getChild(i) == null) {
                    continue;
                }
                /*todo 优化获取*/
                CharSequence className = parent.getChild(i).getClassName();
                String name;
                if (className != null) {
                    name = className.toString();
                } else {
                    name = "View(UNKNOWN)";
                }
                String[] split = name.split("\\.");
                name = split[split.length - 1];
                if (name.equals(widget)) {
                    if (!TextUtils.isEmpty(text)) {
                        if (getNodeInfoTextByNode(parent.getChild(i)).equals(text)) {
                            return parent.getChild(i);
                        }
                    } else {
                        return parent.getChild(i);
                    }
                } else {
                    nodeInfo = getNodeByWidgetName(parent.getChild(i), widget, text);
                    if (nodeInfo != null) {
                        return nodeInfo;
                    }
                }
            }
        }
        return null;
    }

    /** 在指定节点下寻找相应控件名字的一组节点. */
    public static List<AccessibilityNodeInfo> getNodeListByWidgetName(String widget) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        getNodeListByWidgetName(getRootNode(), nodeInfoList, widget);
        return nodeInfoList;
    }

    /** 在指定节点下寻找相应控件名字的一组节点. */
    public static List<AccessibilityNodeInfo> getNodeListByWidgetName(
            AccessibilityNodeInfo parentNode, String widget) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        getNodeListByWidgetName(parentNode, nodeInfoList, widget);
        return nodeInfoList;
    }

    /**
     在指定节点下寻找相应控件名字的一组节点.

     @param parent the info
     */
    private static void getNodeListByWidgetName(
            AccessibilityNodeInfo parent, List<AccessibilityNodeInfo> resultList, String widget) {
        if (parent == null || TextUtils.isEmpty(widget)) {
            return;
        }
        int count = parent.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (parent.getChild(i) == null) {
                    continue;
                }
                /*todo 优化获取*/
                CharSequence className = parent.getChild(i).getClassName();
                String name;
                if (className != null) {
                    name = className.toString();
                } else {
                    name = "View(UNKNOWN)";
                }
                String[] split = name.split("\\.");
                name = split[split.length - 1];
                if (name.equals(widget)) {
                    resultList.add(parent.getChild(i));
                }
                getNodeListByWidgetName(parent.getChild(i), resultList, widget);
            }
        }
    }

    /**
     递归（仔细）寻找某个节点（系统自带方法失效）

     @param viewId the name

     @return the node recursive by text
     */
    public static AccessibilityNodeInfo getNodeRecursiveById(String viewId) {
        return getNodeRecursiveById(viewId, getRootNode());
    }

    /**
     递归（仔细）寻找某个节点（系统自带方法失效）

     @param viewId the name
     @param parent the parent

     @return the node recursive by text
     */
    public static AccessibilityNodeInfo getNodeRecursiveById(
            String viewId, AccessibilityNodeInfo parent) {
        if (TextUtils.isEmpty(viewId)) {
            return null;
        }
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
        }
        if (parent == null) {
            return null;
        }

        int childCount = parent.getChildCount();
        AccessibilityNodeInfo nodeRecursiveByText;

        for (int i = 0; i < childCount; i++) {
            if (viewId.equals(parent.getChild(i).getViewIdResourceName())) {
                return parent.getChild(i);
            }
            nodeRecursiveByText = getNodeRecursiveByText(viewId, parent.getChild(i));
            if (nodeRecursiveByText != null) {
                return nodeRecursiveByText;
            }
        }
        return null;
    }

    /**
     递归（仔细）寻找某个节点（系统自带方法失效）

     @param name the name

     @return the node recursive by text
     */
    public static AccessibilityNodeInfo getNodeRecursiveByText(String name) {
        return getNodeRecursiveByText(name, getRootNode());
    }

    /**
     递归（仔细）寻找某个节点（系统自带方法失效）

     @param name   the name
     @param parent the parent

     @return the node recursive by text
     */
    public static AccessibilityNodeInfo getNodeRecursiveByText(
            String name, AccessibilityNodeInfo parent) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
        }

        if (parent == null) {
            return null;
        }

        int childCount = parent.getChildCount();
        AccessibilityNodeInfo nodeRecursiveByText;

        for (int i = 0; i < childCount; i++) {
            if (getNodeInfoTextByNode(parent.getChild(i)).equals(name)) {
                return parent.getChild(i);
            }
            nodeRecursiveByText = getNodeRecursiveByText(name, parent.getChild(i));
            if (nodeRecursiveByText != null) {
                return nodeRecursiveByText;
            }
        }
        return null;
    }

    public static String getNodeInfoTextById(String id) {
        AccessibilityNodeInfo nodeInfo = AssistUtil.getFirstNodeInfoByViewId(id);
        if (nodeInfo != null && nodeInfo.getText() != null) {
            return nodeInfo.getText().toString();
        }
        return getNodeInfoContentDescById(id);
    }

    public static String getNodeInfoTextByNode(AccessibilityNodeInfo node) {
        if (node != null
                && node.getText() != null
                && !TextUtils.isEmpty(node.getText().toString())) {
            return node.getText().toString();
        }
        return getNodeInfoContentDescByNode(node);
    }

    public static String getNodeInfoContentDescById(String id) {
        AccessibilityNodeInfo nodeInfo = AssistUtil.getFirstNodeInfoByViewId(id);
        if (nodeInfo != null && nodeInfo.getContentDescription() != null) {
            return nodeInfo.getContentDescription().toString();
        }
        return "";
    }

    public static String getNodeInfoContentDescByNode(AccessibilityNodeInfo node) {
        if (node != null && node.getContentDescription() != null) {
            return node.getContentDescription().toString();
        }
        return "";
    }

    /**
     获取改节点下所有子节点数量（包括子节点的子节点...）

     @param nodeInfo the node info

     @return the node child count
     */
    public static int getNodeChildCount(AccessibilityNodeInfo nodeInfo) {
        int count = 0;
        if (nodeInfo == null || nodeInfo.getChildCount() == 0) {
            return 0;
        }
        count = +nodeInfo.getChildCount();
        for (int i = 0, l = nodeInfo.getChildCount(); i < l; i++) {
            count += getNodeChildCount(nodeInfo.getChild(i));
        }
        return count;
    }

    public static boolean performReturnBack() {
        return assistService != null
                && assistService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public static boolean performReturnHome() {
        return assistService != null
                && assistService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     Perform global action boolean.

     @param globalActionBack the global action back

     @return the boolean
     */
    public static boolean performGlobalAction(int globalActionBack) {
        return assistService != null && assistService.performGlobalAction(globalActionBack);
    }

    /**
     跳转到目标Activity

     @param pkg The name of the package that the component exists in. Can not be null

     @return the boolean
     */
    public static boolean gotoTargetActivity(String pkg) {
        if (assistService != null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (ResolveInfo rInfo :
                    assistService.getPackageManager().queryIntentActivities(intent, 0)) {
                if (rInfo.activityInfo.packageName.equals(pkg)) {
                    return gotoTargetActivity(
                            pkg,
                            rInfo.activityInfo.name,
                            Intent.ACTION_MAIN,
                            Intent.CATEGORY_LAUNCHER);
                }
            }
        }
        return false;
    }

    /**
     跳转到目标Activity

     @param pkg      The name of the package that the component exists in. Can not be null
     @param cls      the cls
     @param action   An action name, such as ACTION_VIEW. Application-specific actions should be
     prefixed with the vendor's package name
     @param category The desired category. This can be either one of the predefined Intent
     categories, or a custom category in your own namespace

     @return the boolean
     */
    public static boolean gotoTargetActivity(
            String pkg, String cls, String action, String... category) {

        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(pkg, cls);
        if (action != null) {
            intent.setAction(action);
        }

        if (category != null && category.length > 0) {
            for (String cat : category) {
                intent.addCategory(cat);
            }
        }
        intent.setComponent(cmp);
        return gotoTargetActivity(intent);
    }

    /**
     Goto target activity boolean.

     @param intent the intent

     @return the boolean
     */
    public static boolean gotoTargetActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (assistService != null) {
            assistService.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     打开应用市场

     @param pkgName the pkg name

     @return the boolean
     */
    public static boolean startMarket(Context context, String pkgName) {
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(marketIntent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 返回桌面 @param context the context */
    public static void startHome(Context context) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(homeIntent);
    }

    /** 获取焦点操作 */
    public static void getFocus() throws InterruptedException {
        if (assistService != null) {
            assistService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
            sleep(ONE_SECOND << 1);
            performReturnBack();
            sleep(ONE_SECOND << 1);
        }
    }

    /**
     Sleep.

     @param milli the milli
     */
    public static void sleepSafely(long milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long milli) throws InterruptedException {
        Thread.sleep(milli);
    }

    /**
     Gets current activity.

     @return the current activity
     */
    public static String getCurrentAPPPackageName() {

        if (assistService != null && assistService.getRootInActiveWindow() != null) {
            return assistService.getRootInActiveWindow().getPackageName().toString();
        }
        return null;
    }

    /**
     打印此时的界面状况,便于分析

     @return the string
     */
    public static String analysisPacketInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        analysisPacketInfo(getRootNode(), stringBuilder);
        return stringBuilder.toString();
    }

    public static String analysisPacketInfo(AccessibilityNodeInfo parent) {
        StringBuilder stringBuilder = new StringBuilder();
        analysisPacketInfo(parent, stringBuilder);
        return stringBuilder.toString();
    }

    /**
     打印此时的界面状况,便于分析

     @param info the info
     @param sb   the sb
     @param ints the ints
     */
    public static void analysisPacketInfo(
            AccessibilityNodeInfo info, StringBuilder sb, int... ints) {

        if (info == null || sb == null) {
            return;
        }
        /*if (tabcount > 0) {
            for (int i = 0; i < tabcount; i++) {
                sb.append("\t");
            }
        } else {
            sb.append("--------------------" + "\n");
        }*/
        if (ints != null && ints.length > 0) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < ints.length; j++) {
                s.append(ints[j]).append(".");
            }
            sb.append(s).append("----");
        }
        CharSequence className = info.getClassName();
        String name;
        if (className != null) {
            name = className.toString();
        } else {
            name = "View(UNKNOWN)";
        }
        String[] split = name.split("\\.");
        name = split[split.length - 1];
        sb.append(name).append(":");
        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        sb.append(rect.toShortString()).append("  ");
        sb.append("text:").append(info.getText()).append("  ");
        sb.append("contentDesc:").append(info.getContentDescription()).append("  ");
        sb.append("id:").append(info.getViewIdResourceName()).append("  ");
        sb.append("clickable:").append(info.isClickable()).append("  ");
        sb.append("longClickable:").append(info.isLongClickable()).append("  ");
        sb.append("isVisibleToUser:").append(info.isVisibleToUser()).append("  ");

        int count = info.getChildCount();
        sb.append("(" + count + ")" + "\n");
        if (count > 0) {
            //            tabcount++;
            int len = ints.length + 1;
            int[] newInts = Arrays.copyOf(ints, len);

            for (int i = 0; i < count; i++) {
                newInts[len - 1] = i;
                analysisPacketInfo(info.getChild(i), sb, newInts);
            }
            //            tabcount--;
        }
    }
}
