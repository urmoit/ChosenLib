package com.chosen.lib.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;

/**
 * Client-side extension of GuiUtils with rendering capabilities.
 * Provides client-specific GUI operations that require rendering context.
 * Extends the server-side GuiUtils functionality.
 */
public class ClientGuiUtils extends GuiUtils {
    
    /**
     * Centers a value horizontally on screen.
     * @param width The width of the element.
     * @return The centered X position.
     */
    public static int centerXOnScreen(int width) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client != null ? client.getWindow().getScaledWidth() : 854;
        return GuiUtils.centerX(width, screenWidth);
    }
    
    /**
     * Centers a value vertically on screen.
     * @param height The height of the element.
     * @return The centered Y position.
     */
    public static int centerYOnScreen(int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenHeight = client != null ? client.getWindow().getScaledHeight() : 480;
        return GuiUtils.centerY(height, screenHeight);
    }
    
    /**
     * Gets the current screen width.
     * @return The screen width.
     */
    public static int getScreenWidth() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null ? client.getWindow().getScaledWidth() : 854;
    }
    
    /**
     * Gets the current screen height.
     * @return The screen height.
     */
    public static int getScreenHeight() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null ? client.getWindow().getScaledHeight() : 480;
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
     */
    public static void drawGradient(DrawContext context, int x, int y, int width, int height, 
                                  int startColor, int endColor) {
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
     * Draws a texture with default texture size (256x256).
     * @param context The draw context.
     * @param texture The texture identifier.
     * @param x The X position.
     * @param y The Y position.
     * @param u The U texture coordinate.
     * @param v The V texture coordinate.
     * @param width The width.
     * @param height The height.
     */
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, 
                                 int u, int v, int width, int height) {
        drawTexture(context, texture, x, y, u, v, width, height, 256, 256);
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
        int progressWidth = GuiUtils.calculateProgressWidth(width, progress);
        if (progressWidth > 0) {
            drawRect(context, x, y, progressWidth, height, progressColor);
        }
    }
    
    /**
     * Draws a progress bar with border.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param progress The progress (0.0 to 1.0).
     * @param backgroundColor The background color.
     * @param progressColor The progress color.
     * @param borderColor The border color.
     */
    public static void drawProgressBarWithBorder(DrawContext context, int x, int y, int width, int height, 
                                                double progress, int backgroundColor, int progressColor, int borderColor) {
        // Border
        drawRect(context, x, y, width, height, borderColor);
        
        // Background (inside border)
        drawRect(context, x + 1, y + 1, width - 2, height - 2, backgroundColor);
        
        // Progress (inside border)
        int progressWidth = GuiUtils.calculateProgressWidth(width - 2, progress);
        if (progressWidth > 0) {
            drawRect(context, x + 1, y + 1, progressWidth, height - 2, progressColor);
        }
    }
    
    /**
     * Draws text at a specific position.
     * @param context The draw context.
     * @param text The text to draw.
     * @param x The X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawText(DrawContext context, String text, int x, int y, int color) {
        if (text != null) {
            context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
        }
    }
    
    /**
     * Draws text at a specific position with shadow.
     * @param context The draw context.
     * @param text The text to draw.
     * @param x The X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawTextWithShadow(DrawContext context, String text, int x, int y, int color) {
        if (text != null) {
            context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, true);
        }
    }
    
    /**
     * Draws a Text component at a specific position.
     * @param context The draw context.
     * @param text The text component to draw.
     * @param x The X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawText(DrawContext context, Text text, int x, int y, int color) {
        if (text != null) {
            context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
        }
    }
    
    /**
     * Draws a Text component at a specific position with shadow.
     * @param context The draw context.
     * @param text The text component to draw.
     * @param x The X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawTextWithShadow(DrawContext context, Text text, int x, int y, int color) {
        if (text != null) {
            context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, true);
        }
    }
    
    /**
     * Draws centered text.
     * @param context The draw context.
     * @param text The text to draw.
     * @param centerX The center X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawCenteredText(DrawContext context, String text, int centerX, int y, int color) {
        if (text != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            int textWidth = client.textRenderer.getWidth(text);
            int x = centerX - textWidth / 2;
            drawText(context, text, x, y, color);
        }
    }
    
    /**
     * Draws centered text with shadow.
     * @param context The draw context.
     * @param text The text to draw.
     * @param centerX The center X position.
     * @param y The Y position.
     * @param color The text color.
     */
    public static void drawCenteredTextWithShadow(DrawContext context, String text, int centerX, int y, int color) {
        if (text != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            int textWidth = client.textRenderer.getWidth(text);
            int x = centerX - textWidth / 2;
            drawTextWithShadow(context, text, x, y, color);
        }
    }
    
    /**
     * Gets the width of a text string.
     * @param text The text.
     * @return The width in pixels.
     */
    public static int getTextWidth(String text) {
        if (text == null) return 0;
        MinecraftClient client = MinecraftClient.getInstance();
        return client.textRenderer.getWidth(text);
    }
    
    /**
     * Gets the width of a Text component.
     * @param text The text component.
     * @return The width in pixels.
     */
    public static int getTextWidth(Text text) {
        if (text == null) return 0;
        MinecraftClient client = MinecraftClient.getInstance();
        return client.textRenderer.getWidth(text);
    }
    
    /**
     * Gets the height of text (font height).
     * @return The height in pixels.
     */
    public static int getTextHeight() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.textRenderer.fontHeight;
    }
    
    /**
     * Draws a tooltip at the mouse position.
     * @param context The draw context.
     * @param text The tooltip text.
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     */
    public static void drawTooltip(DrawContext context, String text, int mouseX, int mouseY) {
        if (text != null && !text.isEmpty()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.literal(text), mouseX, mouseY);
        }
    }
    
    /**
     * Draws a tooltip at the mouse position.
     * @param context The draw context.
     * @param text The tooltip text component.
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     */
    public static void drawTooltip(DrawContext context, Text text, int mouseX, int mouseY) {
        if (text != null) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, text, mouseX, mouseY);
        }
    }
    
    /**
     * Draws a simple button-like rectangle.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param pressed Whether the button appears pressed.
     */
    public static void drawButton(DrawContext context, int x, int y, int width, int height, boolean pressed) {
        int lightColor = rgb(255, 255, 255);
        int darkColor = rgb(128, 128, 128);
        int fillColor = rgb(200, 200, 200);
        
        // Fill
        drawRect(context, x, y, width, height, fillColor);
        
        if (pressed) {
            // Pressed appearance (dark top/left, light bottom/right)
            drawRect(context, x, y, width, 1, darkColor); // Top
            drawRect(context, x, y, 1, height, darkColor); // Left
            drawRect(context, x, y + height - 1, width, 1, lightColor); // Bottom
            drawRect(context, x + width - 1, y, 1, height, lightColor); // Right
        } else {
            // Normal appearance (light top/left, dark bottom/right)
            drawRect(context, x, y, width, 1, lightColor); // Top
            drawRect(context, x, y, 1, height, lightColor); // Left
            drawRect(context, x, y + height - 1, width, 1, darkColor); // Bottom
            drawRect(context, x + width - 1, y, 1, height, darkColor); // Right
        }
    }
    
    /**
     * Draws a button with text.
     * @param context The draw context.
     * @param text The button text.
     * @param x The X position.
     * @param y The Y position.
     * @param width The width.
     * @param height The height.
     * @param pressed Whether the button appears pressed.
     */
    public static void drawButtonWithText(DrawContext context, String text, int x, int y, int width, int height, boolean pressed) {
        drawButton(context, x, y, width, height, pressed);
        
        if (text != null && !text.isEmpty()) {
            int textColor = rgb(0, 0, 0);
            int textX = x + width / 2;
            int textY = y + (height - getTextHeight()) / 2;
            drawCenteredText(context, text, textX, textY, textColor);
        }
    }
    
    /**
     * Draws a simple checkbox.
     * @param context The draw context.
     * @param x The X position.
     * @param y The Y position.
     * @param size The size of the checkbox.
     * @param checked Whether the checkbox is checked.
     */
    public static void drawCheckbox(DrawContext context, int x, int y, int size, boolean checked) {
        int backgroundColor = rgb(255, 255, 255);
        int borderColor = rgb(128, 128, 128);
        int checkColor = rgb(0, 128, 0);
        
        // Background
        drawRect(context, x, y, size, size, backgroundColor);
        
        // Border
        drawBorderedRect(context, x, y, size, size, backgroundColor, borderColor, 1);
        
        // Check mark
        if (checked) {
            int margin = size / 4;
            drawRect(context, x + margin, y + margin, size - 2 * margin, size - 2 * margin, checkColor);
        }
    }
}