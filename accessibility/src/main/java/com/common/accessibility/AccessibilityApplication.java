package com.common.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;

import com.common.accessibility.viewid.TaoBWidgetId;
import com.common.accessibility.viewid.WeChatWidgetId;
import com.common.accessibility.viewtext.TaoBWidgetText;

/**
 * Created by Administrator on 2018/8/9.
 */

public class AccessibilityApplication {

    public static void register(AccessibilityService appContext) {
        AssistUtil.assistService = appContext;

    }

    public static void unRegister() {
        AssistUtil.assistService = null;

    }

}
