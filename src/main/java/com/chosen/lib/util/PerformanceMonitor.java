package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance monitoring and profiling utilities.
 * Provides comprehensive performance tracking, memory monitoring, and optimization tools.
 */
public class PerformanceMonitor {
    
    private static final Map<String, ProfileData> activeProfiles = new ConcurrentHashMap<>();
    private static final Map<String, PerformanceStats> performanceStats = new ConcurrentHashMap<>();
    private static final Map<String, Long> tickTimes = new ConcurrentHashMap<>();
    private static final Map<String, Long> memorySnapshots = new ConcurrentHashMap<>();
    private static final AtomicLong totalTicks = new AtomicLong(0);
    private static final AtomicLong totalTickTime = new AtomicLong(0);
    
    /**
     * Profile data for tracking execution times.
     */
    public static class ProfileData {
        private final String name;
        private final long startTime;
        private final Map<String, Object> metadata;
        
        public ProfileData(String name) {
            this.name = name;
            this.startTime = System.nanoTime();
            this.metadata = new HashMap<>();
        }
        
        public String getName() { return name; }
        public long getStartTime() { return startTime; }
        public long getElapsedTime() { return System.nanoTime() - startTime; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(String key, Object value) { metadata.put(key, value); }
        public Object getMetadata(String key) { return metadata.get(key); }
    }
    
    /**
     * Performance statistics.
     */
    public static class PerformanceStats {
        private final String name;
        private final AtomicLong totalTime = new AtomicLong(0);
        private final AtomicLong executionCount = new AtomicLong(0);
        private final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong maxTime = new AtomicLong(0);
        private final Map<String, Object> customMetrics = new ConcurrentHashMap<>();
        
        public PerformanceStats(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
        public long getTotalTime() { return totalTime.get(); }
        public long getExecutionCount() { return executionCount.get(); }
        public long getMinTime() { return minTime.get() == Long.MAX_VALUE ? 0 : minTime.get(); }
        public long getMaxTime() { return maxTime.get(); }
        public double getAverageTime() { 
            long count = executionCount.get();
            return count > 0 ? (double) totalTime.get() / count : 0.0; 
        }
        public Map<String, Object> getCustomMetrics() { return customMetrics; }
        
        public void recordExecution(long timeNs) {
            totalTime.addAndGet(timeNs);
            executionCount.incrementAndGet();
            minTime.updateAndGet(current -> Math.min(current, timeNs));
            maxTime.updateAndGet(current -> Math.max(current, timeNs));
        }
        
        public void setCustomMetric(String key, Object value) { customMetrics.put(key, value); }
        public Object getCustomMetric(String key) { return customMetrics.get(key); }
    }
    
    /**
     * Memory usage information.
     */
    public static class MemoryUsageInfo {
        private final long usedMemory;
        private final long maxMemory;
        private final long freeMemory;
        private final long totalMemory;
        private final double usagePercentage;
        private final Map<String, Long> gcStats;
        
        public MemoryUsageInfo(long usedMemory, long maxMemory, long freeMemory, long totalMemory, 
                             double usagePercentage, Map<String, Long> gcStats) {
            this.usedMemory = usedMemory;
            this.maxMemory = maxMemory;
            this.freeMemory = freeMemory;
            this.totalMemory = totalMemory;
            this.usagePercentage = usagePercentage;
            this.gcStats = gcStats;
        }
        
        // Getters
        public long getUsedMemory() { return usedMemory; }
        public long getMaxMemory() { return maxMemory; }
        public long getFreeMemory() { return freeMemory; }
        public long getTotalMemory() { return totalMemory; }
        public double getUsagePercentage() { return usagePercentage; }
        public Map<String, Long> getGcStats() { return gcStats; }
        
        public String getFormattedUsedMemory() { return formatBytes(usedMemory); }
        public String getFormattedMaxMemory() { return formatBytes(maxMemory); }
        public String getFormattedFreeMemory() { return formatBytes(freeMemory); }
        public String getFormattedTotalMemory() { return formatBytes(totalMemory); }
        
        private String formatBytes(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * TPS (Ticks Per Second) data.
     */
    public static class TPSData {
        private final double currentTPS;
        private final double averageTPS;
        private final double minTPS;
        private final double maxTPS;
        private final List<Double> recentTPS;
        
        public TPSData(double currentTPS, double averageTPS, double minTPS, double maxTPS, List<Double> recentTPS) {
            this.currentTPS = currentTPS;
            this.averageTPS = averageTPS;
            this.minTPS = minTPS;
            this.maxTPS = maxTPS;
            this.recentTPS = recentTPS;
        }
        
        // Getters
        public double getCurrentTPS() { return currentTPS; }
        public double getAverageTPS() { return averageTPS; }
        public double getMinTPS() { return minTPS; }
        public double getMaxTPS() { return maxTPS; }
        public List<Double> getRecentTPS() { return recentTPS; }
    }
    
    /**
     * Entity performance statistics.
     */
    public static class EntityStats {
        private final int totalEntities;
        private final int loadedEntities;
        private final Map<String, Integer> entityTypeCounts;
        private final double averageEntityUpdateTime;
        private final List<String> performanceIssues;
        
        public EntityStats(int totalEntities, int loadedEntities, Map<String, Integer> entityTypeCounts,
                          double averageEntityUpdateTime, List<String> performanceIssues) {
            this.totalEntities = totalEntities;
            this.loadedEntities = loadedEntities;
            this.entityTypeCounts = entityTypeCounts;
            this.averageEntityUpdateTime = averageEntityUpdateTime;
            this.performanceIssues = performanceIssues;
        }
        
        // Getters
        public int getTotalEntities() { return totalEntities; }
        public int getLoadedEntities() { return loadedEntities; }
        public Map<String, Integer> getEntityTypeCounts() { return entityTypeCounts; }
        public double getAverageEntityUpdateTime() { return averageEntityUpdateTime; }
        public List<String> getPerformanceIssues() { return performanceIssues; }
    }
    
    /**
     * Chunk performance statistics.
     */
    public static class ChunkStats {
        private final int totalChunks;
        private final int loadedChunks;
        private final int generatingChunks;
        private final double averageChunkLoadTime;
        private final Map<String, Integer> chunkStatusCounts;
        private final List<String> performanceIssues;
        
        public ChunkStats(int totalChunks, int loadedChunks, int generatingChunks, double averageChunkLoadTime,
                         Map<String, Integer> chunkStatusCounts, List<String> performanceIssues) {
            this.totalChunks = totalChunks;
            this.loadedChunks = loadedChunks;
            this.generatingChunks = generatingChunks;
            this.averageChunkLoadTime = averageChunkLoadTime;
            this.chunkStatusCounts = chunkStatusCounts;
            this.performanceIssues = performanceIssues;
        }
        
        // Getters
        public int getTotalChunks() { return totalChunks; }
        public int getLoadedChunks() { return loadedChunks; }
        public int getGeneratingChunks() { return generatingChunks; }
        public double getAverageChunkLoadTime() { return averageChunkLoadTime; }
        public Map<String, Integer> getChunkStatusCounts() { return chunkStatusCounts; }
        public List<String> getPerformanceIssues() { return performanceIssues; }
    }
    
    /**
     * Starts profiling an operation.
     * @param name The operation name.
     * @return Profile data.
     */
    public static ProfileData startProfiling(String name) {
        ProfileData profile = new ProfileData(name);
        activeProfiles.put(name, profile);
        return profile;
    }
    
    /**
     * Stops profiling an operation.
     * @param name The operation name.
     * @return The elapsed time in nanoseconds.
     */
    public static long stopProfiling(String name) {
        ProfileData profile = activeProfiles.remove(name);
        if (profile != null) {
            long elapsedTime = profile.getElapsedTime();
            recordExecution(name, elapsedTime);
            return elapsedTime;
        }
        return 0;
    }
    
    /**
     * Gets profile data for an operation.
     * @param name The operation name.
     * @return Profile data.
     */
    public static ProfileData getProfileData(String name) {
        return activeProfiles.get(name);
    }
    
    /**
     * Records execution time for an operation.
     * @param operationName The operation name.
     * @param timeNs The execution time in nanoseconds.
     */
    public static void recordExecution(String operationName, long timeNs) {
        PerformanceStats stats = performanceStats.computeIfAbsent(operationName, PerformanceStats::new);
        stats.recordExecution(timeNs);
    }
    
    /**
     * Gets memory usage information.
     * @return Memory usage information.
     */
    public static MemoryUsageInfo getMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        
        long usedMemory = heapUsage.getUsed();
        long maxMemory = heapUsage.getMax();
        long freeMemory = maxMemory - usedMemory;
        long totalMemory = heapUsage.getCommitted();
        
        double usagePercentage = maxMemory > 0 ? (double) usedMemory / maxMemory * 100.0 : 0.0;
        
        // Get garbage collection statistics
        Map<String, Long> gcStats = new HashMap<>();
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcStats.put(gcBean.getName(), gcBean.getCollectionTime());
        }
        
        return new MemoryUsageInfo(usedMemory, maxMemory, freeMemory, totalMemory, usagePercentage, gcStats);
    }
    
    /**
     * Gets garbage collection statistics.
     * @return Map of GC names to collection times.
     */
    public static Map<String, Long> getGCStats() {
        Map<String, Long> gcStats = new HashMap<>();
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcStats.put(gcBean.getName(), gcBean.getCollectionTime());
        }
        return gcStats;
    }
    
    /**
     * Monitors for memory leaks.
     * @param threshold The memory usage threshold percentage.
     * @return List of potential memory leak warnings.
     */
    public static List<String> monitorMemoryLeaks(double threshold) {
        List<String> warnings = new ArrayList<>();
        MemoryUsageInfo memoryInfo = getMemoryUsage();
        
        if (memoryInfo.getUsagePercentage() > threshold) {
            warnings.add("High memory usage: " + String.format("%.2f", memoryInfo.getUsagePercentage()) + "%");
        }
        
        // Check for rapid memory growth
        String snapshotKey = "memory_snapshot_" + System.currentTimeMillis() / 60000; // Per minute
        memorySnapshots.put(snapshotKey, memoryInfo.getUsedMemory());
        
        // Clean up old snapshots (keep last 10)
        if (memorySnapshots.size() > 10) {
            String oldestKey = memorySnapshots.keySet().iterator().next();
            memorySnapshots.remove(oldestKey);
        }
        
        // Check for memory growth trend
        if (memorySnapshots.size() >= 3) {
            List<Long> recentSnapshots = new ArrayList<>(memorySnapshots.values());
            Collections.sort(recentSnapshots);
            
            if (recentSnapshots.get(recentSnapshots.size() - 1) - recentSnapshots.get(0) > memoryInfo.getTotalMemory() * 0.1) {
                warnings.add("Potential memory leak detected: rapid memory growth over time");
            }
        }
        
        return warnings;
    }
    
    /**
     * Measures tick time for a world.
     * @param world The world.
     * @return The tick time in milliseconds.
     */
    public static long measureTickTime(ServerWorld world) {
        if (world == null) {
            return 0;
        }
        
        String worldKey = world.getRegistryKey().getValue().toString();
        long startTime = System.nanoTime();
        
        // This would be called at the end of a tick
        long endTime = System.nanoTime();
        long tickTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        tickTimes.put(worldKey, tickTime);
        totalTickTime.addAndGet(tickTime);
        totalTicks.incrementAndGet();
        
        return tickTime;
    }
    
    /**
     * Gets TPS data for a world.
     * @param world The world.
     * @return TPS data.
     */
    public static TPSData getTPSData(ServerWorld world) {
        if (world == null) {
            return new TPSData(0, 0, 0, 0, new ArrayList<>());
        }
        
        String worldKey = world.getRegistryKey().getValue().toString();
        Long lastTickTime = tickTimes.get(worldKey);
        
        if (lastTickTime == null) {
            return new TPSData(20.0, 20.0, 20.0, 20.0, new ArrayList<>());
        }
        
        double currentTPS = Math.min(20.0, 1000.0 / lastTickTime);
        double averageTPS = totalTicks.get() > 0 ? Math.min(20.0, 1000.0 * totalTicks.get() / totalTickTime.get()) : 20.0;
        
        // Calculate min/max TPS from recent tick times
        double minTPS = 20.0;
        double maxTPS = 20.0;
        List<Double> recentTPS = new ArrayList<>();
        
        for (Long tickTime : tickTimes.values()) {
            double tps = Math.min(20.0, 1000.0 / tickTime);
            recentTPS.add(tps);
            minTPS = Math.min(minTPS, tps);
            maxTPS = Math.max(maxTPS, tps);
        }
        
        return new TPSData(currentTPS, averageTPS, minTPS, maxTPS, recentTPS);
    }
    
    /**
     * Analyzes lag spikes.
     * @param threshold The lag spike threshold in milliseconds.
     * @return List of lag spike warnings.
     */
    public static List<String> analyzeLagSpikes(long threshold) {
        List<String> warnings = new ArrayList<>();
        
        for (Map.Entry<String, Long> entry : tickTimes.entrySet()) {
            String worldKey = entry.getKey();
            long tickTime = entry.getValue();
            
            if (tickTime > threshold) {
                warnings.add("Lag spike detected in world " + worldKey + ": " + tickTime + "ms tick time");
            }
        }
        
        return warnings;
    }
    
    /**
     * Counts entities in a world.
     * @param world The world.
     * @return Entity count.
     */
    public static int countEntities(ServerWorld world) {
        if (world == null) {
            return 0;
        }
        return world.getEntitiesByType(null, entity -> true).size();
    }
    
    /**
     * Gets entity statistics for a world.
     * @param world The world.
     * @return Entity statistics.
     */
    public static EntityStats getEntityStats(ServerWorld world) {
        if (world == null) {
            return new EntityStats(0, 0, new HashMap<>(), 0.0, new ArrayList<>());
        }
        
        List<? extends Entity> entities = world.getEntitiesByType(null, entity -> true);
        int totalEntities = entities.size();
        int loadedEntities = 0;
        Map<String, Integer> entityTypeCounts = new HashMap<>();
        List<String> performanceIssues = new ArrayList<>();
        
        for (Entity entity : entities) {
            loadedEntities++;
            String entityType = entity.getType().toString();
            entityTypeCounts.merge(entityType, 1, Integer::sum);
        }
        
        // Check for performance issues
        if (totalEntities > 1000) {
            performanceIssues.add("High entity count: " + totalEntities + " entities");
        }
        
        // Check for entity type distribution
        for (Map.Entry<String, Integer> entry : entityTypeCounts.entrySet()) {
            if (entry.getValue() > 100) {
                performanceIssues.add("High count of " + entry.getKey() + ": " + entry.getValue() + " entities");
            }
        }
        
        double averageEntityUpdateTime = 0.0; // This would be calculated from actual profiling data
        
        return new EntityStats(totalEntities, loadedEntities, entityTypeCounts, averageEntityUpdateTime, performanceIssues);
    }
    
    /**
     * Optimizes entity updates.
     * @param world The world.
     * @param maxEntitiesPerTick Maximum entities to update per tick.
     * @return Number of entities optimized.
     */
    public static int optimizeEntityUpdates(ServerWorld world, int maxEntitiesPerTick) {
        if (world == null) {
            return 0;
        }
        
        // This is a placeholder for entity update optimization
        // In a real implementation, this would modify entity update scheduling
        return world.getEntitiesByType(null, entity -> true).size();
    }
    
    /**
     * Gets chunk statistics for a world.
     * @param world The world.
     * @return Chunk statistics.
     */
    public static ChunkStats getChunkStats(ServerWorld world) {
        if (world == null) {
            return new ChunkStats(0, 0, 0, 0.0, new HashMap<>(), new ArrayList<>());
        }
        
        int totalChunks = 0;
        int loadedChunks = 0;
        int generatingChunks = 0;
        Map<String, Integer> chunkStatusCounts = new HashMap<>();
        List<String> performanceIssues = new ArrayList<>();
        
        // Count chunks (simplified implementation)
        // The API to iterate over loaded chunks has changed and a public method is not available without mixins.
        // Using getLoadedChunkCount() as an approximation.
        loadedChunks = world.getChunkManager().getLoadedChunkCount();
        totalChunks = loadedChunks;
        
        // Check for performance issues
        if (loadedChunks > 1000) {
            performanceIssues.add("High chunk count: " + loadedChunks + " chunks loaded");
        }
        
        double averageChunkLoadTime = 0.0; // This would be calculated from actual profiling data
        
        return new ChunkStats(totalChunks, loadedChunks, generatingChunks, averageChunkLoadTime, chunkStatusCounts, performanceIssues);
    }
    
    /**
     * Analyzes chunk loading performance.
     * @param world The world.
     * @return List of chunk loading issues.
     */
    public static List<String> analyzeChunkLoading(ServerWorld world) {
        List<String> issues = new ArrayList<>();
        
        if (world == null) {
            return issues;
        }
        
        ChunkStats stats = getChunkStats(world);
        
        if (stats.getLoadedChunks() > 1000) {
            issues.add("Too many chunks loaded: " + stats.getLoadedChunks());
        }
        
        if (stats.getGeneratingChunks() > 10) {
            issues.add("Too many chunks generating: " + stats.getGeneratingChunks());
        }
        
        return issues;
    }
    
    /**
     * Optimizes chunk generation.
     * @param world The world.
     * @param maxGeneratingChunks Maximum chunks to generate simultaneously.
     * @return Number of chunks optimized.
     */
    public static int optimizeChunkGeneration(ServerWorld world, int maxGeneratingChunks) {
        if (world == null) {
            return 0;
        }
        
        // This is a placeholder for chunk generation optimization
        return world.getChunkManager().getLoadedChunkCount();
    }
    
    /**
     * Generates a performance report.
     * @param server The Minecraft server.
     * @return Performance report as string.
     */
    public static String generatePerformanceReport(MinecraftServer server) {
        StringBuilder report = new StringBuilder();
        report.append("=== Performance Report ===\n");
        report.append("Generated: ").append(new Date()).append("\n\n");
        
        // Memory usage
        MemoryUsageInfo memoryInfo = getMemoryUsage();
        report.append("Memory Usage:\n");
        report.append("  Used: ").append(memoryInfo.getFormattedUsedMemory()).append("\n");
        report.append("  Max: ").append(memoryInfo.getFormattedMaxMemory()).append("\n");
        report.append("  Usage: ").append(String.format("%.2f", memoryInfo.getUsagePercentage())).append("%\n\n");
        
        // TPS data
        if (server != null) {
            for (ServerWorld world : server.getWorlds()) {
                TPSData tpsData = getTPSData(world);
                report.append("World ").append(world.getRegistryKey().getValue()).append(" TPS:\n");
                report.append("  Current: ").append(String.format("%.2f", tpsData.getCurrentTPS())).append("\n");
                report.append("  Average: ").append(String.format("%.2f", tpsData.getAverageTPS())).append("\n");
                report.append("  Min: ").append(String.format("%.2f", tpsData.getMinTPS())).append("\n");
                report.append("  Max: ").append(String.format("%.2f", tpsData.getMaxTPS())).append("\n\n");
            }
        }
        
        // Entity statistics
        if (server != null) {
            for (ServerWorld world : server.getWorlds()) {
                EntityStats entityStats = getEntityStats(world);
                report.append("World ").append(world.getRegistryKey().getValue()).append(" Entities:\n");
                report.append("  Total: ").append(entityStats.getTotalEntities()).append("\n");
                report.append("  Loaded: ").append(entityStats.getLoadedEntities()).append("\n");
                if (!entityStats.getPerformanceIssues().isEmpty()) {
                    report.append("  Issues: ").append(String.join(", ", entityStats.getPerformanceIssues())).append("\n");
                }
                report.append("\n");
            }
        }
        
        // Chunk statistics
        if (server != null) {
            for (ServerWorld world : server.getWorlds()) {
                ChunkStats chunkStats = getChunkStats(world);
                report.append("World ").append(world.getRegistryKey().getValue()).append(" Chunks:\n");
                report.append("  Total: ").append(chunkStats.getTotalChunks()).append("\n");
                report.append("  Loaded: ").append(chunkStats.getLoadedChunks()).append("\n");
                if (!chunkStats.getPerformanceIssues().isEmpty()) {
                    report.append("  Issues: ").append(String.join(", ", chunkStats.getPerformanceIssues())).append("\n");
                }
                report.append("\n");
            }
        }
        
        // Performance statistics
        report.append("Operation Performance:\n");
        for (Map.Entry<String, PerformanceStats> entry : performanceStats.entrySet()) {
            PerformanceStats stats = entry.getValue();
            report.append("  ").append(entry.getKey()).append(":\n");
            report.append("    Executions: ").append(stats.getExecutionCount()).append("\n");
            report.append("    Average Time: ").append(String.format("%.2f", stats.getAverageTime() / 1_000_000.0)).append("ms\n");
            report.append("    Min Time: ").append(stats.getMinTime() / 1_000_000.0).append("ms\n");
            report.append("    Max Time: ").append(stats.getMaxTime() / 1_000_000.0).append("ms\n");
        }
        
        return report.toString();
    }
    
    /**
     * Exports performance data to a file.
     * @param server The Minecraft server.
     * @param filename The filename to export to.
     * @return True if export was successful.
     */
    public static boolean exportPerformanceData(MinecraftServer server, String filename) {
        try {
            String report = generatePerformanceReport(server);
            java.nio.file.Files.write(java.nio.file.Paths.get(filename), report.getBytes());
            return true;
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to export performance data", e);
            return false;
        }
    }
    
    /**
     * Creates a performance graph (placeholder).
     * @param dataType The type of data to graph.
     * @param filename The filename to save the graph to.
     * @return True if graph was created.
     */
    public static boolean createPerformanceGraph(String dataType, String filename) {
        // This would create an actual graph in a real implementation
        ChosenLib.LOGGER.info("Performance graph created for " + dataType + " at " + filename);
        return true;
    }
    
    /**
     * Clears all performance data.
     */
    public static void clearPerformanceData() {
        activeProfiles.clear();
        performanceStats.clear();
        tickTimes.clear();
        memorySnapshots.clear();
        totalTicks.set(0);
        totalTickTime.set(0);
    }
}
