package com.metapatrol.jserialport.util;

import java.util.Random;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StringUtil {
    private static final String ELEMENTS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static Random random = new Random();

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ELEMENTS.charAt(random.nextInt(ELEMENTS.length())));
        }
        return sb.toString();
    }
}