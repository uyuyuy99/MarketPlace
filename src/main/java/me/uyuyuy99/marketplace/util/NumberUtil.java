package me.uyuyuy99.marketplace.util;

import java.text.DecimalFormat;

public class NumberUtil {

    private static DecimalFormat longFormat = new DecimalFormat("#,###");

    public static String formatLong(long number) {
        return longFormat.format(number);
    }

    // Input must be Integer or Long. Returns the long value of input
    public static long longValue(Object number) {
        if (number instanceof Integer) {
            return (int) number;
        }
        return (long) number;
    }

}
