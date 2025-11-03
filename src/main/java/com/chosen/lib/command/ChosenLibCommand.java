package com.chosen.lib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ChosenLibCommand implements com.chosen.lib.command.Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("chosenlib")
            .executes(context -> {
                String version = "1.6.0";
                context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("ChosenLib v" + version + " - Advanced Utility Library"), false);
                context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("=== NEW IN v1.6.0 ==="), false);
                context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("📂 FileUtils - Safe file operations and data management"), false);
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
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.success("🎮 ChosenLib v1.6.0 Interactive Demo"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("Available demonstrations:"), false);
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• FileUtils: Safe file operations"), false);
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
                    context.getSource().sendFeedback(() -> com.chosen.lib.util.TextUtils.info("• FileUtils - Safe file operations"), false);
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
    }
}
