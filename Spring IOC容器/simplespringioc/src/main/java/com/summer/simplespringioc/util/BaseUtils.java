package com.summer.simplespringioc.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 基础工具类
 *
 * @author summer
 * @version $Id: BaseUtils.java, v 0.1 2022年02月27日 5:29 PM summer Exp $
 */
public class BaseUtils {

    /**
     * 首字符大写
     *
     * @param s 字符串
     * @return
     */
    public final static String upperFirstChar(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }

        char chars[] = s.toCharArray();
        chars[0] = Character.toUpperCase(s.charAt(0));
        return new String(chars);
    }

    /**
     * 将字符串类型的值转换为特定基础类型的值
     *
     * @param s
     * @return
     */
    public final static Object convertBaseDataType(String s, String type) {
        if (StringUtils.equals("java.lang.Integer", type)) {
            return Integer.valueOf(s);
        } else if (StringUtils.equals("java.lang.Long", type)) {
            return Long.valueOf(s);
        } else if (StringUtils.equals("java.lang.Double", type)) {
            return Double.valueOf(s);
        } else if (StringUtils.equals("java.lang.String", type)) {
            return s;
        }

        return null;
    }
}