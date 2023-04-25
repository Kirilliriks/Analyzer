package me.kirillirik.utils;

public final class NumericUtils {

    public static Integer parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
