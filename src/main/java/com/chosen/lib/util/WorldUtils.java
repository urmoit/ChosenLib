package com.chosen.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class for world and block manipulation operations.
 * Provides helpers for block placement, world queries, and spatial operations.
 */
public class WorldUtils {
    
    /**
     * Safely gets a block state at the given position.
     * @param world The world.
     * @param pos The block position.
     * @return The block state, or air if the position is not loaded.
     */
    public static BlockState getBlockState(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return Blocks.AIR.getDefaultState();
        }
        
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return Blocks.AIR.getDefaultState();
        }
        
        return world.getBlockState(pos);
    }
    
    /**
     * Safely sets a block state at the given position.
     * @param world The world.
     * @param pos The block position.
     * @param state The block state to set.
     * @param flags The update flags.
     * @return True if the block was successfully set.
     */
    public static boolean setBlockState(World world, BlockPos pos, BlockState state, int flags) {
        if (world == null || pos == null || state == null) {
            return false;
        }
        
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return false;
        }
        
        return world.setBlockState(pos, state, flags);
    }
    
    /**
     * Safely sets a block state with default flags (Block.NOTIFY_ALL).
     * @param world The world.
     * @param pos The block position.
     * @param state The block state to set.
     * @return True if the block was successfully set.
     */
    public static boolean setBlockState(World world, BlockPos pos, BlockState state) {
        return setBlockState(world, pos, state, Block.NOTIFY_ALL);
    }
    
    /**
     * Checks if a position is loaded in the world.
     * @param world The world.
     * @param pos The position to check.
     * @return True if the position is loaded.
     */
    public static boolean isPositionLoaded(World world, BlockPos pos) {
        return world != null && pos != null && world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4);
    }
    
    /**
     * Gets all block positions in a cubic area.
     * @param center The center position.
     * @param radius The radius of the cube.
     * @return A list of block positions.
     */
    public static List<BlockPos> getBlocksInCube(BlockPos center, int radius) {
        List<BlockPos> positions = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    positions.add(center.add(x, y, z));
                }
            }
        }
        
        return positions;
    }
    
    /**
     * Gets all block positions in a spherical area.
     * @param center The center position.
     * @param radius The radius of the sphere.
     * @return A list of block positions.
     */
    public static List<BlockPos> getBlocksInSphere(BlockPos center, double radius) {
        List<BlockPos> positions = new ArrayList<>();
        int intRadius = (int) Math.ceil(radius);
        
        for (int x = -intRadius; x <= intRadius; x++) {
            for (int y = -intRadius; y <= intRadius; y++) {
                for (int z = -intRadius; z <= intRadius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (center.getSquaredDistance(pos) <= radius * radius) {
                        positions.add(pos);
                    }
                }
            }
        }
        
        return positions;
    }
    
    /**
     * Gets all block positions in a rectangular area.
     * @param pos1 The first corner.
     * @param pos2 The second corner.
     * @return A list of block positions.
     */
    public static List<BlockPos> getBlocksInArea(BlockPos pos1, BlockPos pos2) {
        List<BlockPos> positions = new ArrayList<>();
        
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        
        return positions;
    }
    
    /**
     * Finds the highest solid block at the given X and Z coordinates.
     * @param world The world.
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The highest solid block position, or null if none found.
     */
    public static BlockPos getHighestBlock(World world, int x, int z) {
        if (world == null) {
            return null;
        }
        
        for (int y = world.getTopY() - 1; y >= world.getBottomY(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (isPositionLoaded(world, pos)) {
                BlockState state = world.getBlockState(pos);
                if (!state.isAir() && state.isSolidBlock(world, pos)) {
                    return pos;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Finds blocks matching a predicate in a given area.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @param predicate The block state predicate.
     * @return A list of matching block positions.
     */
    public static List<BlockPos> findBlocks(World world, BlockPos center, int radius, Predicate<BlockState> predicate) {
        List<BlockPos> matches = new ArrayList<>();
        
        for (BlockPos pos : getBlocksInCube(center, radius)) {
            if (isPositionLoaded(world, pos)) {
                BlockState state = getBlockState(world, pos);
                if (predicate.test(state)) {
                    matches.add(pos);
                }
            }
        }
        
        return matches;
    }
    
    /**
     * Finds the nearest block matching a predicate.
     * @param world The world.
     * @param center The center position.
     * @param maxRadius The maximum search radius.
     * @param predicate The block state predicate.
     * @return The nearest matching block position, or null if none found.
     */
    public static BlockPos findNearestBlock(World world, BlockPos center, int maxRadius, Predicate<BlockState> predicate) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (int radius = 1; radius <= maxRadius; radius++) {
            for (BlockPos pos : getBlocksInCube(center, radius)) {
                if (isPositionLoaded(world, pos)) {
                    BlockState state = getBlockState(world, pos);
                    if (predicate.test(state)) {
                        double distance = center.getSquaredDistance(pos);
                        if (distance < nearestDistance) {
                            nearest = pos;
                            nearestDistance = distance;
                        }
                    }
                }
            }
            
            if (nearest != null) {
                break; // Found something at this radius, no need to search further
            }
        }
        
        return nearest;
    }
    
    /**
     * Replaces blocks in an area matching a predicate.
     * @param world The world.
     * @param area The area to search.
     * @param predicate The block state predicate to match.
     * @param replacement The replacement block state.
     * @param flags The update flags.
     * @return The number of blocks replaced.
     */
    public static int replaceBlocks(World world, List<BlockPos> area, Predicate<BlockState> predicate, 
                                  BlockState replacement, int flags) {
        int replaced = 0;
        
        for (BlockPos pos : area) {
            if (isPositionLoaded(world, pos)) {
                BlockState state = getBlockState(world, pos);
                if (predicate.test(state)) {
                    if (setBlockState(world, pos, replacement, flags)) {
                        replaced++;
                    }
                }
            }
        }
        
        return replaced;
    }

    /**
     * Result of a transactional block replacement operation.
     */
    public static final class TransactionResult {
        public final int replaced;
        public final boolean rolledBack;

        public TransactionResult(int replaced, boolean rolledBack) {
            this.replaced = replaced;
            this.rolledBack = rolledBack;
        }
    }

    /**
     * Replaces blocks matching a predicate in an area. If any placement fails, rolls back all changes.
     * @param world The world
     * @param area Positions to attempt replacement in
     * @param predicate Predicate to match existing block states
     * @param replacement Replacement state to set
     * @param flags Update flags
     * @return TransactionResult with count and rollback flag
     */
    public static TransactionResult replaceBlocksTransactional(
            World world,
            List<BlockPos> area,
            Predicate<BlockState> predicate,
            BlockState replacement,
            int flags
    ) {
        List<BlockPos> changedPositions = new ArrayList<>();
        List<BlockState> previousStates = new ArrayList<>();

        for (BlockPos pos : area) {
            if (!isPositionLoaded(world, pos)) {
                // rollback if a required chunk is not loaded
                rollback(world, changedPositions, previousStates, flags);
                return new TransactionResult(0, true);
            }

            BlockState current = getBlockState(world, pos);
            if (predicate.test(current)) {
                previousStates.add(current);
                changedPositions.add(pos);
                boolean ok = setBlockState(world, pos, replacement, flags);
                if (!ok) {
                    // rollback everything
                    rollback(world, changedPositions, previousStates, flags);
                    return new TransactionResult(0, true);
                }
            }
        }

        return new TransactionResult(changedPositions.size(), false);
    }

    private static void rollback(World world, List<BlockPos> positions, List<BlockState> previousStates, int flags) {
        for (int i = 0; i < positions.size(); i++) {
            BlockPos pos = positions.get(i);
            BlockState prev = previousStates.get(i);
            setBlockState(world, pos, prev, flags);
        }
    }

    /**
     * Applies a batch of exact edits transactionally. If any edit fails, reverts all applied edits.
     * @param world The world
     * @param edits Pairs of position and desired state
     * @param flags Update flags
     * @return TransactionResult
     */
    public static TransactionResult applyBatchEditsTransactional(World world, List<PositionEdit> edits, int flags) {
        List<BlockPos> changedPositions = new ArrayList<>();
        List<BlockState> previousStates = new ArrayList<>();

        for (PositionEdit edit : edits) {
            if (!isPositionLoaded(world, edit.position)) {
                rollback(world, changedPositions, previousStates, flags);
                return new TransactionResult(0, true);
            }

            BlockState prev = getBlockState(world, edit.position);
            previousStates.add(prev);
            changedPositions.add(edit.position);
            if (!setBlockState(world, edit.position, edit.state, flags)) {
                rollback(world, changedPositions, previousStates, flags);
                return new TransactionResult(0, true);
            }
        }

        return new TransactionResult(changedPositions.size(), false);
    }

    /**
     * Represents a single positional edit of a block state.
     */
    public static final class PositionEdit {
        public final BlockPos position;
        public final BlockState state;

        public PositionEdit(BlockPos position, BlockState state) {
            this.position = position;
            this.state = state;
        }
    }
    
    /**
     * Gets the biome at a given position.
     * @param world The world.
     * @param pos The position.
     * @return The biome, or null if the position is not loaded.
     */
    public static Biome getBiome(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return null;
        }
        
        return world.getBiome(pos).value();
    }
    
    /**
     * Gets the light level at a given position.
     * @param world The world.
     * @param pos The position.
     * @return The light level (0-15).
     */
    public static int getLightLevel(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return 0;
        }
        
        return world.getLightLevel(pos);
    }
    
    /**
     * Gets the sky light level at a given position.
     * @param world The world.
     * @param pos The position.
     * @return The sky light level (0-15).
     */
    public static int getSkyLightLevel(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return 0;
        }
        
        return world.getLightLevel(net.minecraft.world.LightType.SKY, pos);
    }
    
    /**
     * Gets the block light level at a given position.
     * @param world The world.
     * @param pos The position.
     * @return The block light level (0-15).
     */
    public static int getBlockLightLevel(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return 0;
        }
        
        return world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
    }
    
    /**
     * Checks if a position is exposed to sky.
     * @param world The world.
     * @param pos The position.
     * @return True if the position can see the sky.
     */
    public static boolean canSeeSky(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return false;
        }
        
        return world.isSkyVisible(pos);
    }
    
    /**
     * Gets all adjacent block positions (6 directions).
     * @param pos The center position.
     * @return A list of adjacent positions.
     */
    public static List<BlockPos> getAdjacentPositions(BlockPos pos) {
        List<BlockPos> adjacent = new ArrayList<>();
        
        for (Direction direction : Direction.values()) {
            adjacent.add(pos.offset(direction));
        }
        
        return adjacent;
    }
    
    /**
     * Checks if a block can be placed at the given position.
     * @param world The world.
     * @param pos The position.
     * @param state The block state to place.
     * @return True if the block can be placed.
     */
    public static boolean canPlaceBlock(World world, BlockPos pos, BlockState state) {
        if (!isPositionLoaded(world, pos)) {
            return false;
        }
        
        BlockState currentState = getBlockState(world, pos);
        return currentState.canReplace(null) && state.canPlaceAt(world, pos);
    }
    
    /**
     * Spawns an item stack at the given position.
     * @param world The world.
     * @param pos The position.
     * @param stack The item stack to spawn.
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack stack) {
        if (world instanceof ServerWorld serverWorld && !stack.isEmpty()) {
            Block.dropStack(serverWorld, pos, stack);
        }
    }
    
    /**
     * Gets the chunk at the given position.
     * @param world The world.
     * @param pos The position.
     * @return The chunk, or null if not loaded.
     */
    public static Chunk getChunk(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return null;
        }
        
        return world.getChunk(pos);
    }
    
    /**
     * Gets the chunk position for a block position.
     * @param pos The block position.
     * @return The chunk position.
     */
    public static ChunkPos getChunkPos(BlockPos pos) {
        return new ChunkPos(pos);
    }
    
    /**
     * Checks if a chunk is loaded.
     * @param world The world.
     * @param chunkPos The chunk position.
     * @return True if the chunk is loaded.
     */
    public static boolean isChunkLoaded(World world, ChunkPos chunkPos) {
        return world != null && world.isChunkLoaded(chunkPos.x, chunkPos.z);
    }
    
    /**
     * Gets the fluid state at a given position.
     * @param world The world.
     * @param pos The position.
     * @return The fluid state.
     */
    public static FluidState getFluidState(World world, BlockPos pos) {
        if (!isPositionLoaded(world, pos)) {
            return net.minecraft.fluid.Fluids.EMPTY.getDefaultState();
        }
        
        return world.getFluidState(pos);
    }
    
    /**
     * Checks if a position contains a fluid.
     * @param world The world.
     * @param pos The position.
     * @return True if the position contains a fluid.
     */
    public static boolean hasFluid(World world, BlockPos pos) {
        return !getFluidState(world, pos).isEmpty();
    }
    
    /**
     * Gets entities in a box area.
     * @param world The world.
     * @param box The bounding box.
     * @param entityClass The entity class to filter by.
     * @param <T> The entity type.
     * @return A list of entities.
     */
    public static <T extends Entity> List<T> getEntitiesInBox(World world, Box box, Class<T> entityClass) {
        return world.getEntitiesByClass(entityClass, box, entity -> true);
    }
    
    /**
     * Gets entities around a position.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @param entityClass The entity class to filter by.
     * @param <T> The entity type.
     * @return A list of entities.
     */
    public static <T extends Entity> List<T> getEntitiesAround(World world, Vec3d center, double radius, Class<T> entityClass) {
        Box box = new Box(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        return getEntitiesInBox(world, box, entityClass);
    }
    
    /**
     * Gets the nearest entity of a specific type.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @param entityClass The entity class.
     * @param <T> The entity type.
     * @return The nearest entity, or null if none found.
     */
    public static <T extends Entity> T getNearestEntity(World world, Vec3d center, double radius, Class<T> entityClass) {
        List<T> entities = getEntitiesAround(world, center, radius, entityClass);
        
        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (T entity : entities) {
            double distance = entity.getPos().distanceTo(center);
            if (distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }
        
        return nearest;
    }
}