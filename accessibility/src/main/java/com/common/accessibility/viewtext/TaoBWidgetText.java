package com.common.accessibility.viewtext;

import com.blankj.utilcode.util.LogUtils;

/** */
public class TaoBWidgetText {
    /** “去购买”按钮 */
    public static String GO_BUY = "";
    /** 分享 */
    public static String SHARE = "";
    /** QQ */
    public static String QQ = "";
    /** 微信 */
    public static String WE_CHAT = "";
    /** 关注 */
    public static String FRIENDING = "";
    /** 搜索 */
    public static String SEARCH = "";
    /** 关注 */
    public static String ATTENTION = "";
    /** 淘宝APP名 */
    public static String TAOBAO = "";
    /** 收藏夹 */
    public static String FAVORITE = "";
    /** 管理 */
    public static String MANAGE = "";
    /** 删除 */
    public static String DELETE = "";

    /** 重新加载 */
    public static String RELOAD = "";

    /** 说点什么吧，我都想知道 */
    public static String SAY_SOMETHING_I_WANT_TO_KNOW;

    /** 说点什么吧，我都想听 */
    public static String SAY_SOMETHING_I_WANT_TO_HEAR;

    public static void initWidgetText(int version) {
        LogUtils.i("initWidgetText");
        switch (version) {
            case 220 | 223:
                GO_BUY = "去购买";
                SHARE = "分享";
                QQ = "QQ";
                WE_CHAT = "微信";
                FRIENDING = "关注";
                SEARCH = "搜索";
                ATTENTION = "关注";
                TAOBAO = "手机淘宝";
                FAVORITE = "收藏夹";
                MANAGE = "管理";
                DELETE = "删除";
                RELOAD = "重新加载";
                SAY_SOMETHING_I_WANT_TO_KNOW = "说点什么吧，我都想知道";
                SAY_SOMETHING_I_WANT_TO_HEAR = "说点什么吧，我都想听";
                break;
        }
    }
}
