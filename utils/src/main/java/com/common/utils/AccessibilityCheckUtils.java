package com.common.utils;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AccessibilityCheckUtils {

    private static int getAccessibilityEnabled(Context context)
            throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
                                     );
    }

    private static String getEnabledAccessibilityServices(Context context) {
        return Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                                        );
    }

    /**
     * @param context context
     * @param str     context.getPackageName() + "/" + AccessibilityService.getCanonicalName();
     */
    public static boolean isAccessibilityEnable(Context context, String str) {
        int i;
        try {
            i = getAccessibilityEnabled(context);
        } catch (Settings.SettingNotFoundException e) {
            i = 0;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter =
                new TextUtils.SimpleStringSplitter(':');
        if (i != 1) {
            return false;
        }
        String string = getEnabledAccessibilityServices(context);
        //        LogUtils.d(string);
        if (string == null) {
            return false;
        }
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            if (simpleStringSplitter.next().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context context
     * @param str     context.getPackageName() + "/" + AccessibilityService.getCanonicalName();
     */
    public static void updateAccessibility(Context context, String str) {
//        LogUtils.d("app:" + str);
        Set componentNameSet = getComponentNameSet(context);
        Set<ComponentName> hashSet =
                componentNameSet == Collections.emptySet() ? new HashSet<>() : componentNameSet;
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        hashSet.add(unflattenFromString);
        StringBuilder stringBuilder = new StringBuilder();
        for (ComponentName unflattenFromString22 : hashSet) {
            stringBuilder.append(unflattenFromString22.flattenToString());
            stringBuilder.append(':');
        }
        int length = stringBuilder.length();
        if (length > 0) {
            stringBuilder.deleteCharAt(length - 1);
        }

        // java.lang.SecurityException: Permission denial:
        // writing to settings requires:android.permission.WRITE_SECURE_SETTINGS
        /*Settings.Secure.putString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                stringBuilder.toString());

        Settings.Secure.putInt(context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 1);*/

        String putAcc =
                "settings put secure enabled_accessibility_services " + stringBuilder.toString();
        String putEnable = "settings put secure accessibility_enabled " + 1;
        Log.d("ENABLED_ACCESSIBILITY", putAcc);
        Log.d("ENABLED_ACCESSIBILITY", putEnable);
        ShellUtils.execCmd(new String[]{putAcc, putEnable}, true, false);
    }

    private static Set<ComponentName> getComponentNameSet(Context context) {
        String string = getEnabledAccessibilityServices(context);
        if (string == null) {
            return Collections.emptySet();
        }
        Set<ComponentName> hashSet = new HashSet<>();
        TextUtils.SimpleStringSplitter simpleStringSplitter =
                new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName unflattenFromString =
                    ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (unflattenFromString != null) {
                hashSet.add(unflattenFromString);
            }
        }
        return hashSet;
    }
}
