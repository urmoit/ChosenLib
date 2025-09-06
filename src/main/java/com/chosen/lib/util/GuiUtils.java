package com.chosen.lib.util;

import net.minecraft.util.math.ColorHelper;

/**
 * Utility class for GUI-related operations in Minecraft mods.
 * Provides helpers for colors, positioning, and common GUI calculations.
 * This class is server-side compatible and contains only non-client specific utilities.
 */
public class GuiUtils {
    
    /**
     * Centers a value within a range.
     * @param value The value to center.
     * @param range The total range.
     * @return The centered position.
     */
    public static int center(int value, int range) {
        return (range - value) / 2;
    }
    
    /**
     * Centers a value horizontally within a given width.
     * @param elementWidth The width of the element.
     * @param containerWidth The width of the container.
     * @return The centered X position.
     */
    public static int centerX(int elementWidth, int containerWidth) {
        return center(elementWidth, containerWidth);
    }
    
    /**
     * Centers a value vertically within a given height.
     * @param elementHeight The height of the element.
     * @param containerHeight The height of the container.
     * @return The centered Y position.
     */
    public static int centerY(int elementHeight, int containerHeight) {
        return center(elementHeight, containerHeight);
    }
    
    /**
     * Converts RGBA values to an integer color.
     * @param r Red component (0-255).
     * @param g Green component (0-255).
     * @param b Blue component (0-255).
     * @param a Alpha component (0-255).
     * @return The color as an integer.
     */
    public static int rgba(int r, int g, int b, int a) {
        return ColorHelper.Argb.getArgb(a, r, g, b);
    }
    
    /**
     * Converts RGB values to an integer color (fully opaque).
     * @param r Red component (0-255).
     * @param g Green component (0-255).
     * @param b Blue component (0-255).
     * @return The color as an integer.
     */
    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }
    
    /**
     * Creates a color with custom alpha from an RGB color.
     * @param rgb The RGB color.
     * @param alpha The alpha value (0-255).
     * @return The color with custom alpha.
     */
    public static int withAlpha(int rgb, int alpha) {
        return (rgb & 0x00FFFFFF) | (alpha << 24);
    }
    
    /**
     * Extracts the red component from a color.
     * @param color The color.
     * @return The red component (0-255).
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Extracts the green component from a color.
     * @param color The color.
     * @return The green component (0-255).
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Extracts the blue component from a color.
     * @param color The color.
     * @return The blue component (0-255).
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    /**
     * Extracts the alpha component from a color.
     * @param color The color.
     * @return The alpha component (0-255).
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    
    /**
     * Checks if a point is within a rectangle.
     * @param x The point X.
     * @param y The point Y.
     * @param rectX The rectangle X.
     * @param rectY The rectangle Y.
     * @param rectWidth The rectangle width.
     * @param rectHeight The rectangle height.
     * @return True if the point is within the rectangle.
     */
    public static boolean isPointInRect(int x, int y, int rectX, int rectY, int rectWidth, int rectHeight) {
        return x >= rectX && x < rectX + rectWidth && y >= rectY && y < rectY + rectHeight;
    }
    
    /**
     * Calculates progress bar width based on progress value.
     * @param totalWidth The total width of the progress bar.
     * @param progress The progress (0.0 to 1.0).
     * @return The width of the progress portion.
     */
    public static int calculateProgressWidth(int totalWidth, double progress) {
        return (int) (totalWidth * Math.max(0.0, Math.min(1.0, progress)));
    }
    
    /**
     * Converts a color from HSV to RGB.
     * @param h Hue (0-360).
     * @param s Saturation (0-1).
     * @param v Value (0-1).
     * @return The RGB color.
     */
    public static int hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;
        
        float r, g, b;
        if (h < 60) {
            r = c; g = x; b = 0;
        } else if (h < 120) {
            r = x; g = c; b = 0;
        } else if (h < 180) {
            r = 0; g = c; b = x;
        } else if (h < 240) {
            r = 0; g = x; b = c;
        } else if (h < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }
        
        return rgb((int) ((r + m) * 255), (int) ((g + m) * 255), (int) ((b + m) * 255));
    }
    
    /**
     * Creates a color with brightness adjustment.
     * @param color The base color.
     * @param brightness The brightness multiplier (0.0 to 2.0).
     * @return The adjusted color.
     */
    public static int adjustBrightness(int color, float brightness) {
        int r = getRed(color);
        int g = getGreen(color);
        int b = getBlue(color);
        int a = getAlpha(color);
        
        r = (int) Math.min(255, r * brightness);
        g = (int) Math.min(255, g * brightness);
        b = (int) Math.min(255, b * brightness);
        
        return rgba(r, g, b, a);
    }
    
    /**
     * Interpolates between two colors.
     * @param color1 The first color.
     * @param color2 The second color.
     * @param factor The interpolation factor (0.0 to 1.0).
     * @return The interpolated color.
     */
    public static int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        
        int r1 = getRed(color1);
        int g1 = getGreen(color1);
        int b1 = getBlue(color1);
        int a1 = getAlpha(color1);
        
        int r2 = getRed(color2);
        int g2 = getGreen(color2);
        int b2 = getBlue(color2);
        int a2 = getAlpha(color2);
        
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        int a = (int) (a1 + (a2 - a1) * factor);
        
        return rgba(r, g, b, a);
    }
    
    /**
     * Clamps a value between min and max.
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamps a value between min and max.
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamps a value between min and max.
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Calculates the distance between two points.
     * @param x1 The first point's X coordinate.
     * @param y1 The first point's Y coordinate.
     * @param x2 The second point's X coordinate.
     * @param y2 The second point's Y coordinate.
     * @return The distance between the points.
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Checks if two rectangles overlap.
     * @param x1 The first rectangle's X position.
     * @param y1 The first rectangle's Y position.
     * @param w1 The first rectangle's width.
     * @param h1 The first rectangle's height.
     * @param x2 The second rectangle's X position.
     * @param y2 The second rectangle's Y position.
     * @param w2 The second rectangle's width.
     * @param h2 The second rectangle's height.
     * @return True if the rectangles overlap.
     */
    public static boolean rectanglesOverlap(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }
}