package com.datavet.datavet.shared.infrastructure.util;

/**
 * Utility class for common string operations across the application.
 */
public final class StringUtils {
    
    private StringUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Checks if a string is null or empty after trimming.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty after trimming.
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * Safely trims a string, returning null if the input is null.
     */
    public static String safeTrim(String str) {
        return str != null ? str.trim() : null;
    }
    
    /**
     * Capitalizes the first letter of a string.
     */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}