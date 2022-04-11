package com.common.accessibility.viewid;

/**
 * 微信各个控件Id
 * Created by Administrator on 2018/4/28.
 */

public class WeChatWidgetId {

    /**
     * 二维码菜单按钮ID
     */
    public static final String MORE = WeChatIdConfig.MORE;

    /**
     * 进度条ID
     */
    public static final String PROGRESS = WeChatIdConfig.PROGRESS;

    /**
     * 主页底部4个按钮
     */
    public static final String BOTTOM_TAB = WeChatIdConfig.BOTTOM_TAB;

    /**
     * 返回按钮，大部分
     */
    public static final String RETURN = WeChatIdConfig.RETURN;

    /**
     * 返回按钮，特例
     */
    public static final String RETURN_SPECIAL = WeChatIdConfig.RETURN_SPECIAL;

    /**
     * 弹窗，(右边按钮，确认)
     */
    public static final String DIALOG_POSITIVE = WeChatIdConfig.DIALOG_POSITIVE;

    /**
     * 弹窗，(左边按钮，取消)
     */
    public static final String DIALOG_NEGATIVE = WeChatIdConfig.DIALOG_NEGATIVE;


    /**
     * 弹窗，内容
     */
    public static final String DIALOG_MSG = WeChatIdConfig.DIALOG_MSG;

    /**
     * 网页弹窗授权登录（右边按钮，确认）
     */
    public static final String WEB_DIALOG_POSITIVE = WeChatIdConfig.WEB_DIALOG_POSITIVE;

    /**
     * 网页弹窗授权登录（左边按钮，拒绝）
     */
    public static final String WEB_DIALOG_NEGATIVE = WeChatIdConfig.WEB_DIALOG_NEGATIVE;

    /**
     * 分享链接网页，更多按钮
     */
    public static final String MORE_FUNCTION = WeChatIdConfig.MORE_FUNCTION;

    /**
     * 网页展开更多时的按钮组件列表——“分享到朋友圈”按钮
     */
    public static final String SHARE_FRIEND = WeChatIdConfig.SHARE_FRIEND;

    /**
     * 朋友圈背景图
     */
    public static final String SNS_BACKGROUND = WeChatIdConfig.SNS_BACKGROUND;

    /**
     * 朋友圈“更换相册封面”按钮
     */
    public static final String SNS_CHANGE_BACK = WeChatIdConfig.SNS_CHANGE_BACK;

    /**
     * 朋友圈“发送”按钮
     */
    public static final String SEND_BTN = WeChatIdConfig.SEND_BTN;

    /**
     * 朋友圈“编辑”按钮
     */
    public static final String SHARE_TEXT = WeChatIdConfig.SHARE_TEXT;

    /**
     * 朋友圈列表
     */
    public static final String SNS_LIST = WeChatIdConfig.SNS_LIST;

    /**
     * 朋友圈“评论”展开按钮
     */
    public static final String SNS_COMMENT_COMPONENT = WeChatIdConfig.SNS_COMMENT_COMPONENT;

    /**
     * 朋友圈“赞”按钮
     */
    public static final String SNS_FAVOUR = WeChatIdConfig.SNS_FAVOUR;

    /**
     * 朋友圈“评论”按钮
     */
    public static final String SNS_COMMENT = WeChatIdConfig.SNS_COMMENT;

    /**
     * 评论编辑控件
     */
    public static final String SNS_COMMENT_EDIT = WeChatIdConfig.SNS_COMMENT_EDIT;

    /**
     * 评论发送控件
     */
    public static final String SNS_COMMENT_SEND = WeChatIdConfig.SNS_COMMENT_SEND;

    /**
     * 微信全局+按钮
     */
    public static final String ADD_MENU = WeChatIdConfig.ADD_MENU;

    /**
     * 搜索控件
     */
    public static final String FIND_BUTTON = WeChatIdConfig.FIND_BUTTON;

    /**
     * 搜索输入控件
     */
    public static final String NAME_INPUT = WeChatIdConfig.NAME_INPUT;

    /**
     * "添加到通讯录"控件
     */
    public static final String ADD_CONTACT = WeChatIdConfig.ADD_CONTACT;

    /**
     * “验证消息”控件
     */
    public static final String HELLO_INPUT = WeChatIdConfig.HELLO_INPUT;

    /**
     * 发送按钮
     */
    public static final String SEND = WeChatIdConfig.SEND;

    /**
     * 扫码“登录”按钮
     */
    public static final String WB_LOGIN = WeChatIdConfig.WB_LOGIN;

    /**
     * 扫码登录警告
     */
    public static final String WB_WARNING = WeChatIdConfig.WB_WARNING;

    /**
     * 顶部状态栏
     */
    public static final String TOP_BAR = WeChatIdConfig.TOP_BAR;

    /**
     * 其他方式登录后的提示框
     */
    public static final String OTHER_LOGIN = WeChatIdConfig.OTHER_LOGIN;

    /**
     * 其他方式登录后的内容
     */
    public static final String OTHER_LOGIN_STATE = WeChatIdConfig.OTHER_LOGIN_STATE;

    /**
     * 微信控件ID配置
     */
    public static class WeChatIdConfig {
        private static final String WECHAT_PACKAGE = "com.tencent.mm:id/";
        private static String MORE = "";
        private static String PROGRESS = "";
        private static String BOTTOM_TAB = "";
        private static String RETURN = "";
        private static String RETURN_SPECIAL = "";
        private static String DIALOG_POSITIVE = "";
        private static String DIALOG_NEGATIVE = "";
        private static String DIALOG_MSG = "";
        private static String WEB_DIALOG_POSITIVE = "";
        private static String WEB_DIALOG_NEGATIVE = "";
        private static String MORE_FUNCTION = "";
        private static String SHARE_FRIEND = "";
        private static String SNS_BACKGROUND = "";
        private static String SNS_CHANGE_BACK = "";
        private static String SEND_BTN = "";
        private static String SHARE_TEXT = "";
        private static String SNS_LIST = "";
        private static String SNS_COMMENT_COMPONENT = "";
        private static String SNS_FAVOUR = "";
        private static String SNS_COMMENT = "";
        private static String SNS_COMMENT_EDIT = "";
        private static String SNS_COMMENT_SEND = "";
        private static String ADD_MENU = "";
        private static String FIND_BUTTON = "";
        private static String NAME_INPUT = "";
        private static String ADD_CONTACT = "";
        private static String HELLO_INPUT = "";
        private static String SEND = "";
        private static String WB_LOGIN = "";
        private static String WB_WARNING = "";
        private static String TOP_BAR = "";
        private static String OTHER_LOGIN = "";
        private static String OTHER_LOGIN_STATE = "";

        public static void initConfig(String weChatVersion) {
            switch (weChatVersion) {
                case "6.6.3":
                    MORE = WECHAT_PACKAGE + "he";
                    PROGRESS = WECHAT_PACKAGE + "xd";
                    BOTTOM_TAB = WECHAT_PACKAGE + "c_z";
                    RETURN = WECHAT_PACKAGE + "hy";
                    RETURN_SPECIAL = WECHAT_PACKAGE + "hi";
                    DIALOG_POSITIVE = WECHAT_PACKAGE + "all";
                    DIALOG_NEGATIVE = WECHAT_PACKAGE + "alk";
                    DIALOG_MSG = WECHAT_PACKAGE + "c_l";
                    WEB_DIALOG_POSITIVE = WECHAT_PACKAGE + "kb";
                    WEB_DIALOG_NEGATIVE = WECHAT_PACKAGE + "ka";
                    MORE_FUNCTION = WECHAT_PACKAGE + "he";
                    SHARE_FRIEND = WECHAT_PACKAGE + "ga";
                    SNS_BACKGROUND = WECHAT_PACKAGE + "dcp";
                    SNS_CHANGE_BACK = WECHAT_PACKAGE + "ga";
                    SEND_BTN = WECHAT_PACKAGE + "hd";
                    SHARE_TEXT = WECHAT_PACKAGE + "der";
                    SNS_LIST = WECHAT_PACKAGE + "dei";
                    SNS_COMMENT_COMPONENT = WECHAT_PACKAGE + "dbk";
                    SNS_FAVOUR = WECHAT_PACKAGE + "daj";
                    SNS_COMMENT = WECHAT_PACKAGE + "dam";
                    SNS_COMMENT_EDIT = WECHAT_PACKAGE + "dbt";
                    SNS_COMMENT_SEND = WECHAT_PACKAGE + "dbv";
                    ADD_MENU = WECHAT_PACKAGE + "g_";
                    FIND_BUTTON = WECHAT_PACKAGE + "b9";
                    NAME_INPUT = WECHAT_PACKAGE + "ht";
                    ADD_CONTACT = WECHAT_PACKAGE + "an_";
                    HELLO_INPUT = WECHAT_PACKAGE + "d0c";
                    SEND = WECHAT_PACKAGE + "hd";
                    WB_LOGIN = WECHAT_PACKAGE + "as_";
                    WB_WARNING = WECHAT_PACKAGE + "as9";
                    TOP_BAR = WECHAT_PACKAGE + "hz";
                    OTHER_LOGIN = WECHAT_PACKAGE + "";
                    OTHER_LOGIN_STATE = WECHAT_PACKAGE + "";
                    break;
                case "6.6.7":
                    MORE = WECHAT_PACKAGE + "hh";
                    PROGRESS = WECHAT_PACKAGE + "xw";
                    BOTTOM_TAB = WECHAT_PACKAGE + "cdj";
                    RETURN = WECHAT_PACKAGE + "ht";
                    RETURN_SPECIAL = WECHAT_PACKAGE + "hl";
                    DIALOG_POSITIVE = WECHAT_PACKAGE + "an3";
                    DIALOG_NEGATIVE = WECHAT_PACKAGE + "an2";
                    DIALOG_MSG = WECHAT_PACKAGE + "cd6";
                    WEB_DIALOG_POSITIVE = WECHAT_PACKAGE + "la";
                    WEB_DIALOG_NEGATIVE = WECHAT_PACKAGE + "l_";
                    MORE_FUNCTION = WECHAT_PACKAGE + "hh";
                    SHARE_FRIEND = WECHAT_PACKAGE + "ge";
                    SNS_BACKGROUND = WECHAT_PACKAGE + "dhc";
                    SNS_CHANGE_BACK = WECHAT_PACKAGE + "ge";
                    SEND_BTN = WECHAT_PACKAGE + "hg";
                    SHARE_TEXT = WECHAT_PACKAGE + "djk";
                    SNS_LIST = WECHAT_PACKAGE + "dja";
                    SNS_COMMENT_COMPONENT = WECHAT_PACKAGE + "dg4";
                    SNS_FAVOUR = WECHAT_PACKAGE + "df6";
                    SNS_COMMENT = WECHAT_PACKAGE + "df9";
                    SNS_COMMENT_EDIT = WECHAT_PACKAGE + "dgc";
                    SNS_COMMENT_SEND = WECHAT_PACKAGE + "dge";
                    ADD_MENU = WECHAT_PACKAGE + "gd";
                    FIND_BUTTON = WECHAT_PACKAGE + "bc";
                    NAME_INPUT = WECHAT_PACKAGE + "hz";
                    ADD_CONTACT = WECHAT_PACKAGE + "ap0";
                    HELLO_INPUT = WECHAT_PACKAGE + "d4h";
                    SEND = WECHAT_PACKAGE + "hg";
                    WB_LOGIN = WECHAT_PACKAGE + "aun";
                    WB_WARNING = WECHAT_PACKAGE + "aum";
                    TOP_BAR = WECHAT_PACKAGE + "i3";
                    OTHER_LOGIN = WECHAT_PACKAGE + "cjt";
                    OTHER_LOGIN_STATE = WECHAT_PACKAGE + "cjx";
                    break;
                default:
                    break;
            }
        }
    }
}
