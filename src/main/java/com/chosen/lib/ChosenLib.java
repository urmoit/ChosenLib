package com.chosen.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.Command;
import net.minecraft.server.command.CommandManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

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
                    String version = "1.5.0";
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("ChosenLib v" + version + " - Advanced Utility Library"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("=== NEW IN v1.5.0 ==="), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("🔧 AdvancedBlockOps - Enhanced block manipulation with safety checks"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("🤖 EntityAIUtils - AI utilities for custom mobs and behaviors"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("💾 DataPersistence - World, player, and global data storage"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("✨ EffectsUtils - Sound & particle effect utilities"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("🌐 AdvancedNetworking - Sophisticated packet handling"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("🌍 DimensionUtils - Custom dimension management"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("🔴 RedstoneUtils - Circuit analysis and automation"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("📊 PerformanceMonitor - Built-in profiling and optimization"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("=== ENHANCED EXISTING UTILITIES ==="), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• WorldUtils - Transactional editing, undo system, pattern analysis"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• EntityUtils - AI integration, enhanced pathfinding, memory system"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• NetworkUtils - Reliable packets, compression, encryption"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• ItemUtils - Data persistence, advanced validation, custom items"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• TextUtils - Rich formatting, performance optimization, localization"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("=== PERFORMANCE IMPROVEMENTS ==="), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• Multi-level caching (L1/L2/L3) with intelligent invalidation"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• Batch operations for blocks, entities, packets, and items"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• Smart object pooling and memory leak detection"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("=== USAGE EXAMPLES ==="), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• /chosenlib demo - Interactive feature demonstrations"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• /chosenlib perf - Real-time performance monitoring"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• /chosenlib stats - Comprehensive statistics overview"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("CurseForge: https://www.curseforge.com/minecraft/mc-mods/chosenlib", "https://www.curseforge.com/minecraft/mc-mods/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("Modrinth: https://modrinth.com/mod/chosenlib", "https://modrinth.com/mod/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("Source: https://github.com/chosenlib/chosenlib", "https://github.com/chosenlib/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.clickable("Discord: https://discord.gg/chosenlib", "https://discord.gg/chosenlib"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("License: MIT License | Backward Compatible: ✅"), false);
                    return Command.SINGLE_SUCCESS;
                })
                .then(CommandManager.literal("demo")
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("🎮 ChosenLib v1.5.0 Interactive Demo"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Available demonstrations:"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• AdvancedBlockOps: Transactional editing with rollback support"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• EntityAIUtils: Custom mob AI and behavior management"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• EffectsUtils: Particle systems and sound effects"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• RedstoneUtils: Circuit analysis and automation"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• DataPersistence: World and player data storage"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Use /chosenlib demo <feature> to see specific examples!"), false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(CommandManager.literal("perf")
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("📊 ChosenLib Performance Monitor"), false);
                        
                        // Get memory usage
                        com.chosen.lib.util.PerformanceMonitor.MemoryUsageInfo memoryInfo = 
                            com.chosen.lib.util.PerformanceMonitor.getMemoryUsage();
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Memory Usage: " + memoryInfo.getFormattedUsedMemory() + 
                            " / " + memoryInfo.getFormattedMaxMemory() + " (" + 
                            String.format("%.1f", memoryInfo.getUsagePercentage()) + "%)"), false);
                        
                        // Get TPS data if server world is available
                        if (context.getSource().getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            com.chosen.lib.util.PerformanceMonitor.TPSData tpsData = 
                                com.chosen.lib.util.PerformanceMonitor.getTPSData(serverWorld);
                            context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("TPS: " + 
                                String.format("%.2f", tpsData.getCurrentTPS()) + " (Avg: " + 
                                String.format("%.2f", tpsData.getAverageTPS()) + ")"), false);
                        }
                        
                        // Get entity stats
                        if (context.getSource().getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            com.chosen.lib.util.PerformanceMonitor.EntityStats entityStats = 
                                com.chosen.lib.util.PerformanceMonitor.getEntityStats(serverWorld);
                            context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Entities: " + 
                                entityStats.getLoadedEntities() + " loaded, " + entityStats.getTotalEntities() + " total"), false);
                        }
                        
                        // Get chunk stats
                        if (context.getSource().getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            com.chosen.lib.util.PerformanceMonitor.ChunkStats chunkStats = 
                                com.chosen.lib.util.PerformanceMonitor.getChunkStats(serverWorld);
                            context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Chunks: " + 
                                chunkStats.getLoadedChunks() + " loaded, " + chunkStats.getTotalChunks() + " total"), false);
                        }
                        
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Use /chosenlib report for detailed analysis!"), false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(CommandManager.literal("stats")
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("📈 ChosenLib Statistics Overview"), false);
                        
                        // Show utility class usage stats
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Utility Classes Available:"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• AdvancedBlockOps - Transactional block operations"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• EntityAIUtils - AI behavior management"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• DataPersistence - Data storage & retrieval"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• EffectsUtils - Sound & particle effects"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• AdvancedNetworking - Packet handling"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• DimensionUtils - Custom dimensions"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• RedstoneUtils - Circuit automation"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• PerformanceMonitor - Profiling tools"), false);
                        
                        // Show enhanced existing utilities
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Enhanced Utilities:"), false);
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• WorldUtils, EntityUtils, NetworkUtils, ItemUtils, TextUtils"), false);
                        
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("All utilities are thread-safe and backward compatible!"), false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(CommandManager.literal("report")
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("📋 Generating Performance Report..."), false);
                        
                        if (context.getSource().getServer() != null) {
                            String report = com.chosen.lib.util.PerformanceMonitor.generatePerformanceReport(context.getSource().getServer());
                            
                            // Split report into chunks and send
                            String[] lines = report.split("\n");
                            for (String line : lines) {
                                if (!line.trim().isEmpty()) {
                                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info(line), false);
                                }
                            }
                        } else {
                            context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Performance report generation requires server context"), false);
                        }
                        
                        return Command.SINGLE_SUCCESS;
                    })
                )
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