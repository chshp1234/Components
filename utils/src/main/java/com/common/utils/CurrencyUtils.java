package com.common.utils;

import java.text.DecimalFormat;

/**
 * 货币工具类
 *
 * @author csp
 * @date 2017/11/7
 */
public class CurrencyUtils {

    /** 获取货币分单位 */
    public static String getCent(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String percentString = decimalFormat.format(price);
        return percentString.substring(percentString.length() - 2);
    }

    /** 格式化货币 */
    public static String format2CurrencyString(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(price);
    }
}
