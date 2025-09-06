package com.chosen.lib.util;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.text.Style;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Utility class for text-related operations in Minecraft mods.
 * Provides helpers for text formatting, validation, and manipulation.
 * Includes performance optimizations and caching for frequently used operations.
 */
public class TextUtils {
    
    // Performance optimization: Cache compiled regex patterns
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    // Common formatting styles cache
    private static final Map<Formatting, Style> STYLE_CACHE = new ConcurrentHashMap<>();
    
    static {
        // Pre-populate common styles
        for (Formatting formatting : Formatting.values()) {
            STYLE_CACHE.put(formatting, Style.EMPTY.withFormatting(formatting));
        }
    }
    
    /**
     * Formats a message with a single formatting option using cached styles.
     * @param message The message to format.
     * @param formatting The formatting option.
     * @return The formatted Text.
     */
    public static Text format(String message, Formatting formatting) {
        if (message == null) return Text.empty();
        return Text.literal(message).setStyle(STYLE_CACHE.get(formatting));
    }

    /**
     * Creates success text (green).
     * @param message The message.
     * @return Green formatted text.
     */
    public static Text success(String message) {
        return format(message, Formatting.GREEN);
    }

    /**
     * Creates error text (red).
     * @param message The message.
     * @return Red formatted text.
     */
    public static Text error(String message) {
        return format(message, Formatting.RED);
    }

    /**
     * Creates warning text (yellow).
     * @param message The message.
     * @return Yellow formatted text.
     */
    public static Text warning(String message) {
        return format(message, Formatting.YELLOW);
    }

    /**
     * Creates info text (aqua).
     * @param message The message.
     * @return Aqua formatted text.
     */
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
     * Checks if a string is null, empty, or only whitespace.
     * @param str The string to check.
     * @return True if null, empty, or only whitespace.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
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
     * Joins a list of strings with a separator.
     * @param separator The separator string.
     * @param elements The list of strings to join.
     * @return The joined string.
     */
    public static String join(String separator, List<String> elements) {
        if (elements == null) return "";
        return String.join(separator, elements);
    }

    /**
     * Combines multiple Text objects into one.
     * @param texts The Text objects to combine.
     * @return A single Text object containing all inputs.
     */
    public static Text combine(Text... texts) {
        if (texts == null || texts.length == 0) return Text.empty();
        
        MutableText result = Text.empty();
        for (Text text : texts) {
            if (text != null) {
                result.append(text);
            }
        }
        return result;
    }

    /**
     * Strips formatting from a Text object, returning the plain string.
     * @param text The Text object.
     * @return The plain string without formatting.
     */
    public static String stripFormatting(Text text) {
        if (text == null) return "";
        return text.getString();
    }

    /**
     * Formats a message with multiple Formatting options.
     * @param message The message to format.
     * @param formattings The formatting options.
     * @return The formatted Text.
     */
    public static Text format(String message, Formatting... formattings) {
        if (message == null) return Text.empty();
        if (formattings == null || formattings.length == 0) return Text.literal(message);
        
        MutableText text = Text.literal(message);
        for (Formatting formatting : formattings) {
            if (formatting != null) {
                text = text.formatted(formatting);
            }
        }
        return text;
    }

    /**
     * Creates a clickable text that opens a URL when clicked.
     * @param label The text to display.
     * @param url The URL to open.
     * @return A Text object with a click event.
     */
    public static Text clickable(String label, String url) {
        if (label == null) label = "";
        if (url == null) url = "";
        
        return Text.literal(label)
            .setStyle(Style.EMPTY.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }
    
    /**
     * Creates a clickable text that runs a command when clicked.
     * @param label The text to display.
     * @param command The command to run.
     * @return A Text object with a click event.
     */
    public static Text clickableCommand(String label, String command) {
        if (label == null) label = "";
        if (command == null) command = "";
        
        return Text.literal(label)
            .setStyle(Style.EMPTY.withFormatting(Formatting.GREEN, Formatting.UNDERLINE)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }
    
    /**
     * Creates a text with hover tooltip.
     * @param label The text to display.
     * @param tooltip The tooltip text.
     * @return A Text object with a hover event.
     */
    public static Text withTooltip(String label, String tooltip) {
        if (label == null) label = "";
        if (tooltip == null) tooltip = "";
        
        return Text.literal(label)
            .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip))));
    }
    
    /**
     * Creates a text with hover tooltip and click action.
     * @param label The text to display.
     * @param tooltip The tooltip text.
     * @param url The URL to open when clicked.
     * @return A Text object with hover and click events.
     */
    public static Text interactiveText(String label, String tooltip, String url) {
        if (label == null) label = "";
        if (tooltip == null) tooltip = "";
        if (url == null) url = "";
        
        return Text.literal(label)
            .setStyle(Style.EMPTY.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip)))
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Truncates a string to a maximum length, adding ellipsis if needed.
     * @param str The string to truncate.
     * @param maxLength The maximum length.
     * @return The truncated string.
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || maxLength < 0) return str;
        if (str.length() <= maxLength) return str;
        if (maxLength < 3) return str.substring(0, maxLength);
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
            if (!words[i].isEmpty()) {
                result.append(capitalize(words[i].toLowerCase()));
            }
        }
        
        return result.toString();
    }

    /**
     * Checks if a string contains only alphanumeric characters using cached pattern.
     * @param str The string to check.
     * @return True if alphanumeric only, false otherwise.
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) return false;
        return ALPHANUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * Checks if a string is a valid email address using cached pattern.
     * @param email The email to validate.
     * @return True if valid email, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates a string against a regex pattern with caching.
     * @param str The string to validate.
     * @param regex The regex pattern.
     * @return True if the string matches the pattern.
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) return false;
        
        Pattern pattern = PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
        return pattern.matcher(str).matches();
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
     * Removes all non-alphanumeric characters except spaces from a string.
     * @param str The string to clean.
     * @return The cleaned string.
     */
    public static String removeSpecialCharsKeepSpaces(String str) {
        if (str == null) return "";
        return str.replaceAll("[^a-zA-Z0-9\\s]", "");
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
        
        double percentage = Math.max(0.0, Math.min(1.0, (double) current / max));
        int filled = (int) Math.round(percentage * length);
        int empty = length - filled;
        
        MutableText bar = Text.empty();
        
        if (filled > 0) {
            bar.append(Text.literal("█".repeat(filled)).formatted(Formatting.GREEN));
        }
        if (empty > 0) {
            bar.append(Text.literal("█".repeat(empty)).formatted(Formatting.GRAY));
        }
        
        return bar;
    }
    
    /**
     * Creates a percentage-based progress bar.
     * @param percentage The percentage (0.0 to 1.0).
     * @param length The length of the progress bar.
     * @return A text progress bar.
     */
    public static Text progressBar(double percentage, int length) {
        return progressBar((int) (percentage * 100), 100, length);
    }

    /**
     * Creates a centered text with padding.
     * @param text The text to center.
     * @param width The total width.
     * @return The centered text.
     */
    public static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        
        int padding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - padding;
        
        return " ".repeat(padding) + text + " ".repeat(rightPadding);
    }

    /**
     * Creates a bordered text box.
     * @param text The text to put in the box.
     * @param width The width of the box.
     * @return The bordered text.
     */
    public static String createTextBox(String text, int width) {
        if (text == null) text = "";
        if (width < 3) return text; // Minimum width for borders
        
        String border = "─".repeat(width - 2);
        String centeredText = centerText(text, width - 2);
        
        return "┌" + border + "┐\n" +
               "│" + centeredText + "│\n" +
               "└" + border + "┘";
    }
    
    /**
     * Creates a multi-line text box.
     * @param lines The lines of text.
     * @param width The width of the box.
     * @return The bordered text box.
     */
    public static String createMultiLineTextBox(List<String> lines, int width) {
        if (lines == null || lines.isEmpty()) return createTextBox("", width);
        if (width < 3) return String.join("\n", lines);
        
        StringBuilder result = new StringBuilder();
        String border = "─".repeat(width - 2);
        
        result.append("┌").append(border).append("┐\n");
        
        for (String line : lines) {
            String centeredLine = centerText(line != null ? line : "", width - 2);
            result.append("│").append(centeredLine).append("│\n");
        }
        
        result.append("└").append(border).append("┘");
        
        return result.toString();
    }
    
    /**
     * Wraps text to fit within a specified width.
     * @param text The text to wrap.
     * @param width The maximum width per line.
     * @return A list of wrapped lines.
     */
    public static List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || width <= 0) return lines;
        
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > width) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                
                // Handle words longer than width
                if (word.length() > width) {
                    while (word.length() > width) {
                        lines.add(word.substring(0, width));
                        word = word.substring(width);
                    }
                    if (!word.isEmpty()) {
                        currentLine.append(word);
                    }
                } else {
                    currentLine.append(word);
                }
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
    
    /**
     * Formats a number with thousand separators.
     * @param number The number to format.
     * @return The formatted number string.
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }
    
    /**
     * Formats a decimal number with specified decimal places.
     * @param number The number to format.
     * @param decimalPlaces The number of decimal places.
     * @return The formatted number string.
     */
    public static String formatDecimal(double number, int decimalPlaces) {
        return String.format("%." + Math.max(0, decimalPlaces) + "f", number);
    }
    
    /**
     * Converts milliseconds to a human-readable duration string.
     * @param milliseconds The duration in milliseconds.
     * @return A formatted duration string.
     */
    public static String formatDuration(long milliseconds) {
        if (milliseconds < 0) return "0ms";
        
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours % 24, minutes % 60, seconds % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else if (seconds > 0) {
            return String.format("%ds", seconds);
        } else {
            return String.format("%dms", milliseconds);
        }
    }
    
    /**
     * Clears all cached patterns and styles.
     */
    public static void clearCache() {
        PATTERN_CACHE.clear();
        // Don't clear STYLE_CACHE as it contains pre-computed common styles
    }
    
    /**
     * Gets cache statistics for debugging.
     * @return A map containing cache statistics.
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        stats.put("patternCacheSize", PATTERN_CACHE.size());
        stats.put("styleCacheSize", STYLE_CACHE.size());
        return stats;
    }
}