package com.chosen.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import com.chosen.lib.Config;

public class ChosenLib implements ModInitializer {
    public static final String MOD_ID = "chosenlib";
    public static final String MOD_NAME = "ChosenLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static ChosenLib instance;

    public static final Path CONFIG_PATH = Path.of("config/chosenlib.json");
    public static Config CONFIG;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("{} v{} initialized", MOD_NAME, getVersion());
        loadConfig();
        // Register custom commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("chosenlib")
                .executes(context -> {
                    String version = "1.3.0";
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("ChosenLib v" + version), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("CurseForge: https://www.curseforge.com/minecraft/mc-mods/chosenlib", "https://www.curseforge.com/minecraft/mc-mods/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Modrinth: (Coming Soon)"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("Source: https://www.curseforge.com/minecraft/mc-mods/chosenlib", "https://www.curseforge.com/minecraft/mc-mods/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("Discord: https://discord.gg/yourinvite", "https://discord.gg/yourinvite"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("License: MIT License"), false);
                    return Command.SINGLE_SUCCESS;
                })
            );
        });
    }

    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                CONFIG = new Config();
                saveConfig();
            } else {
                String json = Files.readString(CONFIG_PATH);
                CONFIG = GSON.fromJson(json, Config.class);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config!", e);
            CONFIG = new Config();
        }
    }

    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(CONFIG);
            Files.writeString(CONFIG_PATH, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to save config!", e);
        }
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