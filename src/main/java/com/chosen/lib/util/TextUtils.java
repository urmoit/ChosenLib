package com.chosen.lib.util;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class TextUtils {
    public static Text format(String message, Formatting formatting) {
        return Text.literal(message).copy().formatted(formatting);
    }

    public static Text success(String message) {
        return format(message, Formatting.GREEN);
    }

    public static Text error(String message) {
        return format(message, Formatting.RED);
    }

    public static Text warning(String message) {
        return format(message, Formatting.YELLOW);
    }

    public static Text info(String message) {
        return format(message, Formatting.AQUA);
    }

    /**
     * Capitalizes the first letter of the given string.
     * @param input The string to capitalize.
     * @return The capitalized string, or the original if empty/null.
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Repeats the given string a specified number of times.
     * @param str The string to repeat.
     * @param times Number of times to repeat.
     * @return The repeated string.
     */
    public static String repeat(String str, int times) {
        if (str == null || times <= 0) return "";
        return str.repeat(times);
    }

    /**
     * Checks if a string is null or empty.
     * @param str The string to check.
     * @return True if null or empty, false otherwise.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Joins an array of strings with a separator.
     * @param separator The separator string.
     * @param elements The array of strings to join.
     * @return The joined string.
     */
    public static String join(String separator, String... elements) {
        if (elements == null) return "";
        return String.join(separator, elements);
    }

    /**
     * Combines multiple Text objects into one.
     * @param texts The Text objects to combine.
     * @return A single Text object containing all inputs.
     */
    public static Text combine(Text... texts) {
        Text result = Text.empty();
        for (Text t : texts) {
            result = result.copy().append(t);
        }
        return result;
    }

    /**
     * Strips formatting from a Text object, returning the plain string.
     * @param text The Text object.
     * @return The plain string without formatting.
     */
    public static String stripFormatting(Text text) {
        return text.getString();
    }

    /**
     * Formats a message with multiple Formatting options.
     * @param message The message to format.
     * @param formattings The formatting options.
     * @return The formatted Text.
     */
    public static Text format(String message, Formatting... formattings) {
        MutableText t = Text.literal(message);
        for (Formatting f : formattings) {
            t = t.formatted(f);
        }
        return t;
    }
}