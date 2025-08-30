package com.chosen.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChosenLib implements ModInitializer {
    public static final String MOD_ID = "chosenlib";
    public static final String MOD_NAME = "ChosenLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static ChosenLib instance;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("{} v{} initialized", MOD_NAME, getVersion());
    }

    public static ChosenLib getInstance() {
        return instance;
    }

    public static String getVersion() {
        return ChosenLib.class.getPackage().getImplementationVersion();
    }

    /**
     * Returns a random integer between min (inclusive) and max (inclusive).
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random integer between min and max
     */
    public static int randomInt(int min, int max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * Clamps a value between min and max.
     * @param value The value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return The clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Logs a debug message if debug is enabled.
     * @param message The message to log
     */
    public static void debugLog(String message) {
        // You can add a debug flag here if needed
        LOGGER.debug("[DEBUG] {}", message);
    }

    /**
     * Linearly interpolates between a and b by t (0.0 - 1.0).
     * @param a Start value
     * @param b End value
     * @param t Interpolation factor (0.0 - 1.0)
     * @return Interpolated value
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    /**
     * Checks if a value is between min and max (inclusive).
     * @param value The value to check
     * @param min Minimum value
     * @param max Maximum value
     * @return True if value is between min and max, false otherwise
     */
    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Null-safe equality check for two objects.
     * @param a First object
     * @param b Second object
     * @return True if both are equal or both null, false otherwise
     */
    public static boolean safeEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}