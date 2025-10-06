package com.chosen.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Advanced block operations utility with enhanced safety checks and transactional support.
 * Provides sophisticated block manipulation capabilities with rollback support.
 */
public class AdvancedBlockOps {
    
    private static final Map<String, UndoPoint> undoHistory = new ConcurrentHashMap<>();
    private static final Map<String, OperationStats> operationStats = new ConcurrentHashMap<>();
    
    /**
     * Represents a single undo point for rollback operations.
     */
    public static class UndoPoint {
        private final Map<BlockPos, BlockState> blockStates;
        private final long timestamp;
        private final String operationId;
        
        public UndoPoint(Map<BlockPos, BlockState> blockStates, String operationId) {
            this.blockStates = new HashMap<>(blockStates);
            this.timestamp = System.currentTimeMillis();
            this.operationId = operationId;
        }
        
        public Map<BlockPos, BlockState> getBlockStates() {
            return new HashMap<>(blockStates);
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getOperationId() {
            return operationId;
        }
    }
    
    /**
     * Represents operation statistics for performance monitoring.
     */
    public static class OperationStats {
        private int operationsCount = 0;
        private long totalTime = 0;
        private long minTime = Long.MAX_VALUE;
        private long maxTime = 0;
        
        public void recordOperation(long timeMs) {
            operationsCount++;
            totalTime += timeMs;
            minTime = Math.min(minTime, timeMs);
            maxTime = Math.max(maxTime, timeMs);
        }
        
        public int getOperationsCount() { return operationsCount; }
        public long getTotalTime() { return totalTime; }
        public long getMinTime() { return minTime == Long.MAX_VALUE ? 0 : minTime; }
        public long getMaxTime() { return maxTime; }
        public double getAverageTime() { 
            return operationsCount > 0 ? (double) totalTime / operationsCount : 0.0; 
        }
    }
    
    /**
     * Result of a transactional block operation.
     */
    public static class TransactionResult {
        public final boolean success;
        public final int blocksChanged;
        public final long operationTime;
        public final String errorMessage;
        
        public TransactionResult(boolean success, int blocksChanged, long operationTime, String errorMessage) {
            this.success = success;
            this.blocksChanged = blocksChanged;
            this.operationTime = operationTime;
            this.errorMessage = errorMessage;
        }
    }
    
    /**
     * Creates an undo point for the specified area.
     * @param world The world.
     * @param area The area to create undo point for.
     * @param operationId Unique identifier for this operation.
     * @return The undo point ID.
     */
    public static String createUndoPoint(World world, List<BlockPos> area, String operationId) {
        Map<BlockPos, BlockState> blockStates = new HashMap<>();
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                blockStates.put(pos, WorldUtils.getBlockState(world, pos));
            }
        }
        
        String undoId = operationId + "_" + System.currentTimeMillis();
        undoHistory.put(undoId, new UndoPoint(blockStates, operationId));
        
        // Clean up old undo points (keep only last 100)
        if (undoHistory.size() > 100) {
            undoHistory.entrySet().removeIf(entry -> 
                System.currentTimeMillis() - entry.getValue().getTimestamp() > 3600000); // 1 hour
        }
        
        return undoId;
    }
    
    /**
     * Restores an undo point.
     * @param world The world.
     * @param undoId The undo point ID.
     * @return True if restoration was successful.
     */
    public static boolean restoreUndoPoint(World world, String undoId) {
        UndoPoint undoPoint = undoHistory.get(undoId);
        if (undoPoint == null) {
            return false;
        }
        
        int restored = 0;
        for (Map.Entry<BlockPos, BlockState> entry : undoPoint.getBlockStates().entrySet()) {
            if (WorldUtils.setBlockState(world, entry.getKey(), entry.getValue())) {
                restored++;
            }
        }
        
        undoHistory.remove(undoId);
        return restored > 0;
    }
    
    /**
     * Clears all undo history.
     */
    public static void clearUndoHistory() {
        undoHistory.clear();
    }
    
    /**
     * Validates if a block can be placed at the given position.
     * @param world The world.
     * @param pos The position.
     * @param state The block state to place.
     * @return Validation result with details.
     */
    public static ValidationResult validateBlockPlacement(World world, BlockPos pos, BlockState state) {
        if (!WorldUtils.isPositionLoaded(world, pos)) {
            return new ValidationResult(false, "Position not loaded");
        }
        
        BlockState currentState = WorldUtils.getBlockState(world, pos);
        
        if (!currentState.canReplace(null)) {
            return new ValidationResult(false, "Cannot replace current block");
        }
        
        if (!state.canPlaceAt(world, pos)) {
            return new ValidationResult(false, "Block cannot be placed at this position");
        }
        
        // Check for protected blocks
        if (isProtectedBlock(currentState)) {
            return new ValidationResult(false, "Cannot modify protected block");
        }
        
        return new ValidationResult(true, "Valid placement");
    }
    
    /**
     * Checks block integrity in an area.
     * @param world The world.
     * @param area The area to check.
     * @return Integrity check result.
     */
    public static IntegrityResult checkBlockIntegrity(World world, List<BlockPos> area) {
        List<BlockPos> corruptedBlocks = new ArrayList<>();
        List<BlockPos> unloadedChunks = new ArrayList<>();
        
        for (BlockPos pos : area) {
            if (!WorldUtils.isPositionLoaded(world, pos)) {
                unloadedChunks.add(pos);
            } else {
                BlockState state = WorldUtils.getBlockState(world, pos);
                if (state == null || state.isAir()) {
                    // This might be considered corrupted depending on context
                    corruptedBlocks.add(pos);
                }
            }
        }
        
        return new IntegrityResult(corruptedBlocks, unloadedChunks);
    }
    
    /**
     * Verifies area access for block operations.
     * @param world The world.
     * @param area The area to verify.
     * @return Access verification result.
     */
    public static AccessResult verifyAreaAccess(World world, List<BlockPos> area) {
        Set<Chunk> requiredChunks = new HashSet<>();
        List<BlockPos> inaccessibleBlocks = new ArrayList<>();
        
        for (BlockPos pos : area) {
            Chunk chunk = WorldUtils.getChunk(world, pos);
            if (chunk == null) {
                inaccessibleBlocks.add(pos);
            } else {
                requiredChunks.add(chunk);
            }
        }
        
        return new AccessResult(requiredChunks, inaccessibleBlocks);
    }
    
    /**
     * Safely fills an area with a block state.
     * @param world The world.
     * @param area The area to fill.
     * @param state The block state to place.
     * @param operationId Operation identifier.
     * @return Transaction result.
     */
    public static TransactionResult fillAreaSafely(World world, List<BlockPos> area, BlockState state, String operationId) {
        long startTime = System.currentTimeMillis();
        
        // Create undo point
        String undoId = createUndoPoint(world, area, operationId);
        
        try {
            // Validate access
            AccessResult accessResult = verifyAreaAccess(world, area);
            if (!accessResult.getInaccessibleBlocks().isEmpty()) {
                return new TransactionResult(false, 0, 0, 
                    "Cannot access " + accessResult.getInaccessibleBlocks().size() + " blocks");
            }
            
            int changed = 0;
            List<BlockPos> failedPositions = new ArrayList<>();
            
            for (BlockPos pos : area) {
                ValidationResult validation = validateBlockPlacement(world, pos, state);
                if (validation.isValid()) {
                    if (WorldUtils.setBlockState(world, pos, state)) {
                        changed++;
                    } else {
                        failedPositions.add(pos);
                    }
                } else {
                    failedPositions.add(pos);
                }
            }
            
            long operationTime = System.currentTimeMillis() - startTime;
            recordOperation("fillAreaSafely", operationTime);
            
            if (!failedPositions.isEmpty()) {
                // Rollback on partial failure
                restoreUndoPoint(world, undoId);
                return new TransactionResult(false, 0, operationTime, 
                    "Failed to place blocks at " + failedPositions.size() + " positions");
            }
            
            return new TransactionResult(true, changed, operationTime, null);
            
        } catch (Exception e) {
            // Rollback on exception
            restoreUndoPoint(world, undoId);
            return new TransactionResult(false, 0, System.currentTimeMillis() - startTime, 
                "Exception during operation: " + e.getMessage());
        }
    }
    
    /**
     * Safely replaces blocks in an area.
     * @param world The world.
     * @param area The area to replace blocks in.
     * @param predicate Predicate to match blocks to replace.
     * @param replacement The replacement block state.
     * @param operationId Operation identifier.
     * @return Transaction result.
     */
    public static TransactionResult replaceAreaSafely(World world, List<BlockPos> area, 
                                                    Predicate<BlockState> predicate, 
                                                    BlockState replacement, String operationId) {
        long startTime = System.currentTimeMillis();
        
        // Create undo point for blocks that will be changed
        List<BlockPos> blocksToChange = new ArrayList<>();
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState currentState = WorldUtils.getBlockState(world, pos);
                if (predicate.test(currentState)) {
                    blocksToChange.add(pos);
                }
            }
        }
        
        if (blocksToChange.isEmpty()) {
            return new TransactionResult(true, 0, 0, "No blocks to replace");
        }
        
        String undoId = createUndoPoint(world, blocksToChange, operationId);
        
        try {
            int changed = 0;
            List<BlockPos> failedPositions = new ArrayList<>();
            
            for (BlockPos pos : blocksToChange) {
                ValidationResult validation = validateBlockPlacement(world, pos, replacement);
                if (validation.isValid()) {
                    if (WorldUtils.setBlockState(world, pos, replacement)) {
                        changed++;
                    } else {
                        failedPositions.add(pos);
                    }
                } else {
                    failedPositions.add(pos);
                }
            }
            
            long operationTime = System.currentTimeMillis() - startTime;
            recordOperation("replaceAreaSafely", operationTime);
            
            if (!failedPositions.isEmpty()) {
                // Rollback on partial failure
                restoreUndoPoint(world, undoId);
                return new TransactionResult(false, 0, operationTime, 
                    "Failed to replace blocks at " + failedPositions.size() + " positions");
            }
            
            return new TransactionResult(true, changed, operationTime, null);
            
        } catch (Exception e) {
            // Rollback on exception
            restoreUndoPoint(world, undoId);
            return new TransactionResult(false, 0, System.currentTimeMillis() - startTime, 
                "Exception during operation: " + e.getMessage());
        }
    }
    
    /**
     * Safely clones an area to another location.
     * @param world The world.
     * @param sourceArea The source area.
     * @param targetPos The target position (top-left corner).
     * @param operationId Operation identifier.
     * @return Transaction result.
     */
    public static TransactionResult cloneAreaSafely(World world, List<BlockPos> sourceArea, 
                                                  BlockPos targetPos, String operationId) {
        long startTime = System.currentTimeMillis();
        
        // Calculate bounds
        Box sourceBounds = calculateBounds(sourceArea);
        if (sourceBounds == null) {
            return new TransactionResult(false, 0, 0, "Invalid source area");
        }
        
        // Calculate target area
        List<BlockPos> targetArea = new ArrayList<>();
        for (BlockPos sourcePos : sourceArea) {
            BlockPos relativePos = sourcePos.subtract(BlockPos.ofFloored(sourceBounds.minX, sourceBounds.minY, sourceBounds.minZ));
            targetArea.add(targetPos.add(relativePos));
        }
        
        // Create undo point
        String undoId = createUndoPoint(world, targetArea, operationId);
        
        try {
            // Validate target area access
            AccessResult accessResult = verifyAreaAccess(world, targetArea);
            if (!accessResult.getInaccessibleBlocks().isEmpty()) {
                return new TransactionResult(false, 0, 0, 
                    "Cannot access target area: " + accessResult.getInaccessibleBlocks().size() + " blocks inaccessible");
            }
            
            int cloned = 0;
            List<BlockPos> failedPositions = new ArrayList<>();
            
            for (int i = 0; i < sourceArea.size(); i++) {
                BlockPos sourcePos = sourceArea.get(i);
                BlockPos targetPos_i = targetArea.get(i);
                
                if (WorldUtils.isPositionLoaded(world, sourcePos) && WorldUtils.isPositionLoaded(world, targetPos_i)) {
                    BlockState sourceState = WorldUtils.getBlockState(world, sourcePos);
                    
                    ValidationResult validation = validateBlockPlacement(world, targetPos_i, sourceState);
                    if (validation.isValid()) {
                        if (WorldUtils.setBlockState(world, targetPos_i, sourceState)) {
                            cloned++;
                        } else {
                            failedPositions.add(targetPos_i);
                        }
                    } else {
                        failedPositions.add(targetPos_i);
                    }
                } else {
                    failedPositions.add(targetPos_i);
                }
            }
            
            long operationTime = System.currentTimeMillis() - startTime;
            recordOperation("cloneAreaSafely", operationTime);
            
            if (!failedPositions.isEmpty()) {
                // Rollback on partial failure
                restoreUndoPoint(world, undoId);
                return new TransactionResult(false, 0, operationTime, 
                    "Failed to clone " + failedPositions.size() + " blocks");
            }
            
            return new TransactionResult(true, cloned, operationTime, null);
            
        } catch (Exception e) {
            // Rollback on exception
            restoreUndoPoint(world, undoId);
            return new TransactionResult(false, 0, System.currentTimeMillis() - startTime, 
                "Exception during operation: " + e.getMessage());
        }
    }
    
    /**
     * Analyzes block patterns in an area.
     * @param world The world.
     * @param area The area to analyze.
     * @return Pattern analysis result.
     */
    public static PatternAnalysis analyzeBlockPattern(World world, List<BlockPos> area) {
        Map<BlockState, Integer> blockCounts = new HashMap<>();
        Map<Block, Integer> blockTypeCounts = new HashMap<>();
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState state = WorldUtils.getBlockState(world, pos);
                blockCounts.merge(state, 1, Integer::sum);
                blockTypeCounts.merge(state.getBlock(), 1, Integer::sum);
            }
        }
        
        return new PatternAnalysis(blockCounts, blockTypeCounts, area.size());
    }
    
    /**
     * Detects block structures in an area.
     * @param world The world.
     * @param area The area to analyze.
     * @return List of detected structures.
     */
    public static List<DetectedStructure> detectBlockStructures(World world, List<BlockPos> area) {
        List<DetectedStructure> structures = new ArrayList<>();
        
        // Simple structure detection - can be enhanced
        Map<Block, List<BlockPos>> blockGroups = new HashMap<>();
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState state = WorldUtils.getBlockState(world, pos);
                if (!state.isAir()) {
                    blockGroups.computeIfAbsent(state.getBlock(), k -> new ArrayList<>()).add(pos);
                }
            }
        }
        
        // Detect connected components as structures
        for (Map.Entry<Block, List<BlockPos>> entry : blockGroups.entrySet()) {
            List<List<BlockPos>> connectedComponents = findConnectedComponents(entry.getValue());
            for (List<BlockPos> component : connectedComponents) {
                if (component.size() >= 3) { // Minimum structure size
                    structures.add(new DetectedStructure(entry.getKey(), component));
                }
            }
        }
        
        return structures;
    }
    
    /**
     * Measures operation time and records statistics.
     * @param operationName The operation name.
     * @param timeMs The operation time in milliseconds.
     */
    private static void recordOperation(String operationName, long timeMs) {
        operationStats.computeIfAbsent(operationName, k -> new OperationStats()).recordOperation(timeMs);
    }
    
    /**
     * Gets operation statistics.
     * @param operationName The operation name.
     * @return Operation statistics.
     */
    public static OperationStats getOperationStats(String operationName) {
        return operationStats.getOrDefault(operationName, new OperationStats());
    }
    
    /**
     * Gets all operation statistics.
     * @return Map of all operation statistics.
     */
    public static Map<String, OperationStats> getAllOperationStats() {
        return new HashMap<>(operationStats);
    }
    
    // Helper classes and methods
    
    /**
     * Validation result for block placement.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    /**
     * Integrity check result.
     */
    public static class IntegrityResult {
        private final List<BlockPos> corruptedBlocks;
        private final List<BlockPos> unloadedChunks;
        
        public IntegrityResult(List<BlockPos> corruptedBlocks, List<BlockPos> unloadedChunks) {
            this.corruptedBlocks = corruptedBlocks;
            this.unloadedChunks = unloadedChunks;
        }
        
        public List<BlockPos> getCorruptedBlocks() { return corruptedBlocks; }
        public List<BlockPos> getUnloadedChunks() { return unloadedChunks; }
        public boolean isIntact() { return corruptedBlocks.isEmpty() && unloadedChunks.isEmpty(); }
    }
    
    /**
     * Access verification result.
     */
    public static class AccessResult {
        private final Set<Chunk> requiredChunks;
        private final List<BlockPos> inaccessibleBlocks;
        
        public AccessResult(Set<Chunk> requiredChunks, List<BlockPos> inaccessibleBlocks) {
            this.requiredChunks = requiredChunks;
            this.inaccessibleBlocks = inaccessibleBlocks;
        }
        
        public Set<Chunk> getRequiredChunks() { return requiredChunks; }
        public List<BlockPos> getInaccessibleBlocks() { return inaccessibleBlocks; }
        public boolean isAccessible() { return inaccessibleBlocks.isEmpty(); }
    }
    
    /**
     * Pattern analysis result.
     */
    public static class PatternAnalysis {
        private final Map<BlockState, Integer> blockCounts;
        private final Map<Block, Integer> blockTypeCounts;
        private final int totalBlocks;
        
        public PatternAnalysis(Map<BlockState, Integer> blockCounts, Map<Block, Integer> blockTypeCounts, int totalBlocks) {
            this.blockCounts = blockCounts;
            this.blockTypeCounts = blockTypeCounts;
            this.totalBlocks = totalBlocks;
        }
        
        public Map<BlockState, Integer> getBlockCounts() { return blockCounts; }
        public Map<Block, Integer> getBlockTypeCounts() { return blockTypeCounts; }
        public int getTotalBlocks() { return totalBlocks; }
    }
    
    /**
     * Detected structure information.
     */
    public static class DetectedStructure {
        private final Block blockType;
        private final List<BlockPos> positions;
        
        public DetectedStructure(Block blockType, List<BlockPos> positions) {
            this.blockType = blockType;
            this.positions = positions;
        }
        
        public Block getBlockType() { return blockType; }
        public List<BlockPos> getPositions() { return positions; }
        public int getSize() { return positions.size(); }
    }
    
    // Helper methods
    
    private static boolean isProtectedBlock(BlockState state) {
        // Add logic to identify protected blocks (e.g., bedrock, spawners, etc.)
        return state.isOf(Blocks.BEDROCK) || state.isOf(Blocks.SPAWNER);
    }
    
    private static Box calculateBounds(List<BlockPos> positions) {
        if (positions.isEmpty()) return null;
        
        int minX = positions.get(0).getX();
        int maxX = positions.get(0).getX();
        int minY = positions.get(0).getY();
        int maxY = positions.get(0).getY();
        int minZ = positions.get(0).getZ();
        int maxZ = positions.get(0).getZ();
        
        for (BlockPos pos : positions) {
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        
        return new Box(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
    }
    
    private static List<List<BlockPos>> findConnectedComponents(List<BlockPos> positions) {
        List<List<BlockPos>> components = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        
        for (BlockPos pos : positions) {
            if (!visited.contains(pos)) {
                List<BlockPos> component = new ArrayList<>();
                floodFill(positions, pos, visited, component);
                components.add(component);
            }
        }
        
        return components;
    }
    
    private static void floodFill(List<BlockPos> allPositions, BlockPos start, Set<BlockPos> visited, List<BlockPos> component) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            component.add(current);
            
            // Check all 6 adjacent positions
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.offset(direction);
                if (allPositions.contains(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
    }
}
