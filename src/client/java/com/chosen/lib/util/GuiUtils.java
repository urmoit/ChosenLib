 package com.chosen.lib.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.MinecraftClient;

/**
 * Utility class for GUI-related operations in Minecraft mods.
 * Provides helpers for drawing, positioning, colors, and common GUI patterns.
 * This class is client-side only.
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
     * Draws a filled rectangle.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param color The color.
     */
    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }
    
    /**
     * Draws a bordered rectangle.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param fillColor The fill color.
     * @param borderColor The border color.
     * @param borderWidth The border width.
     */
    public static void drawBorderedRect(DrawContext context, int x, int y, int width, int height, 
                                      int fillColor, int borderColor, int borderWidth) {
        // Fill
        drawRect(context, x, y, width, height, fillColor);
        
        // Border
        drawRect(context, x, y, width, borderWidth, borderColor); // Top
        drawRect(context, x, y + height - borderWidth, width, borderWidth, borderColor); // Bottom
        drawRect(context, x, y, borderWidth, height, borderColor); // Left
        drawRect(context, x + width - borderWidth, y, borderWidth, height, borderColor); // Right
    }
    
    /**
     * Draws a gradient rectangle.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param startColor The start color.
     * @param endColor The end color.
     * @param horizontal Whether the gradient is horizontal (true) or vertical (false).
     */
    public static void drawGradient(DrawContext context, int x, int y, int width, int height, 
                                  int startColor, int endColor, boolean horizontal) {
        context.fillGradient(x, y, x + width, y + height, startColor, endColor);
    }
    
    /**
     * Draws a texture.
     * @param context The draw context.
     * @param texture The texture identifier.
     * @param x The X position.
     * @param y The Y position.
     * @param u The U texture coordinate.
     * @param v The V texture coordinate.
     * @param width The width.
     * @param height The height.
     * @param textureWidth The texture width.
     * @param textureHeight The texture height.
     */
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, 
                                 int u, int v, int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
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
     * Draws a progress bar.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param progress The progress (0.0 to 1.0).
     * @param backgroundColor The background color.
     * @param progressColor The progress color.
     */
    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height, 
                                     double progress, int backgroundColor, int progressColor) {
        // Background
        drawRect(context, x, y, width, height, backgroundColor);
        
        // Progress
        int progressWidth = (int) (width * progress);
        if (progressWidth > 0) {
            drawRect(context, x, y, progressWidth, height, progressColor);
        }
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
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        r = (int) Math.min(255, r * brightness);
        g = (int) Math.min(255, g * brightness);
        b = (int) Math.min(255, b * brightness);
        
        return rgb(r, g, b);
    }
}
