package com.chosen.lib.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextUtils {
    public static Text format(String message, Formatting formatting) {
        return Text.literal(message).formatted(formatting);
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
}