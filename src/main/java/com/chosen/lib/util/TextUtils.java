package com.chosen.lib.util;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.text.Style;
import net.minecraft.text.ClickEvent;

import java.util.regex.Pattern;

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

    /**
     * Creates a clickable text that opens a URL when clicked.
     * @param label The text to display.
     * @param url The URL to open.
     * @return A Text object with a click event.
     */
    public static Text clickable(String label, String url) {
        return Text.literal(label)
            .setStyle(Style.EMPTY.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Truncates a string to a maximum length, adding ellipsis if needed.
     * @param str The string to truncate.
     * @param maxLength The maximum length.
     * @return The truncated string.
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Converts a string to title case (first letter of each word capitalized).
     * @param str The string to convert.
     * @return The title-cased string.
     */
    public static String toTitleCase(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            result.append(capitalize(words[i].toLowerCase()));
        }
        return result.toString();
    }

    /**
     * Checks if a string contains only alphanumeric characters.
     * @param str The string to check.
     * @return True if alphanumeric only, false otherwise.
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) return false;
        return str.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Checks if a string is a valid email address.
     * @param email The email to validate.
     * @return True if valid email, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    /**
     * Removes all non-alphanumeric characters from a string.
     * @param str The string to clean.
     * @return The cleaned string.
     */
    public static String removeSpecialChars(String str) {
        if (str == null) return "";
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Counts the number of words in a string.
     * @param str The string to count words in.
     * @return The number of words.
     */
    public static int wordCount(String str) {
        if (str == null || str.trim().isEmpty()) return 0;
        return str.trim().split("\\s+").length;
    }

    /**
     * Reverses a string.
     * @param str The string to reverse.
     * @return The reversed string.
     */
    public static String reverse(String str) {
        if (str == null) return "";
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Checks if a string is a palindrome.
     * @param str The string to check.
     * @return True if palindrome, false otherwise.
     */
    public static boolean isPalindrome(String str) {
        if (str == null) return false;
        String cleaned = str.toLowerCase().replaceAll("[^a-z0-9]", "");
        return cleaned.equals(reverse(cleaned));
    }

    /**
     * Creates a progress bar as text.
     * @param current The current value.
     * @param max The maximum value.
     * @param length The length of the progress bar.
     * @return A text progress bar.
     */
    public static Text progressBar(int current, int max, int length) {
        if (max <= 0 || length <= 0) return Text.literal("");
        
        int filled = (int) Math.round((double) current / max * length);
        int empty = length - filled;
        
        StringBuilder bar = new StringBuilder();
        bar.append("§a"); // Green for filled
        bar.append("█".repeat(filled));
        bar.append("§7"); // Gray for empty
        bar.append("█".repeat(empty));
        
        return Text.literal(bar.toString());
    }

    /**
     * Creates a centered text with padding.
     * @param text The text to center.
     * @param width The total width.
     * @return The centered text.
     */
    public static String centerText(String text, int width) {
        if (text == null || text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    /**
     * Creates a bordered text box.
     * @param text The text to put in the box.
     * @param width The width of the box.
     * @return The bordered text.
     */
    public static String createTextBox(String text, int width) {
        if (text == null) text = "";
        String border = "─".repeat(width - 2);
        String centeredText = centerText(text, width - 2);
        
        return "┌" + border + "┐\n" +
               "│" + centeredText + "│\n" +
               "└" + border + "┘";
    }
}