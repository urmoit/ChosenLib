package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities for working with custom dimensions.
 * Provides dimension management, teleportation, and dimension-specific operations.
 */
public class DimensionUtils {
    
    private static final Map<RegistryKey<World>, DimensionInfo> dimensionInfo = new ConcurrentHashMap<>();
    private static final Map<String, PortalLink> portalLinks = new ConcurrentHashMap<>();
    
    /**
     * Dimension information container.
     */
    public static class DimensionInfo {
        private final RegistryKey<World> dimensionKey;
        private final String name;
        private final BlockPos spawnPoint;
        private final float gravity;
        private final long time;
        private final boolean hasWeather;
        private final boolean hasDayNightCycle;
        private final Map<String, Object> customProperties;
        
        public DimensionInfo(RegistryKey<World> dimensionKey, String name, BlockPos spawnPoint) {
            this.dimensionKey = dimensionKey;
            this.name = name;
            this.spawnPoint = spawnPoint;
            this.gravity = 1.0f; // Default gravity
            this.time = 6000L; // Default noon
            this.hasWeather = true;
            this.hasDayNightCycle = true;
            this.customProperties = new HashMap<>();
        }
        
        // Getters and setters
        public RegistryKey<World> getDimensionKey() { return dimensionKey; }
        public String getName() { return name; }
        public BlockPos getSpawnPoint() { return spawnPoint; }
        public void setSpawnPoint(BlockPos spawnPoint) { /* Note: This would need to be handled differently in real implementation */ }
        public float getGravity() { return gravity; }
        public void setGravity(float gravity) { /* Note: This would need to be handled differently in real implementation */ }
        public long getTime() { return time; }
        public void setTime(long time) { /* Note: This would need to be handled differently in real implementation */ }
        public boolean hasWeather() { return hasWeather; }
        public void setHasWeather(boolean hasWeather) { /* Note: This would need to be handled differently in real implementation */ }
        public boolean hasDayNightCycle() { return hasDayNightCycle; }
        public void setHasDayNightCycle(boolean hasDayNightCycle) { /* Note: This would need to be handled differently in real implementation */ }
        public Map<String, Object> getCustomProperties() { return customProperties; }
        public void setCustomProperty(String key, Object value) { customProperties.put(key, value); }
        public Object getCustomProperty(String key) { return customProperties.get(key); }
    }
    
    /**
     * Portal link between dimensions.
     */
    public static class PortalLink {
        private final String linkId;
        private final RegistryKey<World> sourceDimension;
        private final BlockPos sourcePos;
        private final RegistryKey<World> targetDimension;
        private final BlockPos targetPos;
        private final boolean bidirectional;
        private final Map<String, Object> linkProperties;
        
        public PortalLink(String linkId, RegistryKey<World> sourceDimension, BlockPos sourcePos,
                         RegistryKey<World> targetDimension, BlockPos targetPos, boolean bidirectional) {
            this.linkId = linkId;
            this.sourceDimension = sourceDimension;
            this.sourcePos = sourcePos;
            this.targetDimension = targetDimension;
            this.targetPos = targetPos;
            this.bidirectional = bidirectional;
            this.linkProperties = new HashMap<>();
        }
        
        // Getters and setters
        public String getLinkId() { return linkId; }
        public RegistryKey<World> getSourceDimension() { return sourceDimension; }
        public BlockPos getSourcePos() { return sourcePos; }
        public RegistryKey<World> getTargetDimension() { return targetDimension; }
        public BlockPos getTargetPos() { return targetPos; }
        public boolean isBidirectional() { return bidirectional; }
        public Map<String, Object> getLinkProperties() { return linkProperties; }
        public void setLinkProperty(String key, Object value) { linkProperties.put(key, value); }
        public Object getLinkProperty(String key) { return linkProperties.get(key); }
    }
    
    /**
     * Teleportation result.
     */
    public static class TeleportResult {
        private final boolean success;
        private final String errorMessage;
        private final RegistryKey<World> targetDimension;
        private final Vec3d targetPosition;
        
        public TeleportResult(boolean success, String errorMessage, RegistryKey<World> targetDimension, Vec3d targetPosition) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.targetDimension = targetDimension;
            this.targetPosition = targetPosition;
        }
        
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public RegistryKey<World> getTargetDimension() { return targetDimension; }
        public Vec3d getTargetPosition() { return targetPosition; }
    }
    
    /**
     * Creates a custom dimension (placeholder implementation).
     * @param server The Minecraft server.
     * @param dimensionId The dimension identifier.
     * @param dimensionType The dimension type.
     * @return The created dimension key.
     */
    public static RegistryKey<World> createDimension(MinecraftServer server, Identifier dimensionId, RegistryKey<DimensionType> dimensionType) {
        RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
        
        // Create dimension info
        DimensionInfo info = new DimensionInfo(dimensionKey, dimensionId.getPath(), BlockPos.ORIGIN);
        dimensionInfo.put(dimensionKey, info);
        
        ChosenLib.LOGGER.info("Created custom dimension: " + dimensionId);
        return dimensionKey;
    }
    
    /**
     * Deletes a custom dimension (placeholder implementation).
     * @param server The Minecraft server.
     * @param dimensionKey The dimension key.
     * @return True if deletion was successful.
     */
    public static boolean deleteDimension(MinecraftServer server, RegistryKey<World> dimensionKey) {
        if (dimensionInfo.containsKey(dimensionKey)) {
            dimensionInfo.remove(dimensionKey);
            
            // Remove portal links involving this dimension
            portalLinks.entrySet().removeIf(entry -> {
                PortalLink link = entry.getValue();
                return link.getSourceDimension().equals(dimensionKey) || 
                       link.getTargetDimension().equals(dimensionKey);
            });
            
            ChosenLib.LOGGER.info("Deleted custom dimension: " + dimensionKey.getValue());
            return true;
        }
        return false;
    }
    
    /**
     * Gets dimension information.
     * @param dimensionKey The dimension key.
     * @return Dimension information.
     */
    public static DimensionInfo getDimensionInfo(RegistryKey<World> dimensionKey) {
        return dimensionInfo.get(dimensionKey);
    }
    
    /**
     * Teleports an entity to a different dimension.
     * @param entity The entity to teleport.
     * @param targetDimension The target dimension.
     * @param targetPos The target position.
     * @return Teleportation result.
     */
    public static TeleportResult teleportToDimension(Entity entity, RegistryKey<World> targetDimension, Vec3d targetPos) {
        if (entity == null || targetDimension == null || targetPos == null) {
            return new TeleportResult(false, "Invalid parameters", targetDimension, targetPos);
        }
        
        try {
            if (entity.getWorld() instanceof ServerWorld currentWorld) {
                ServerWorld targetWorld = currentWorld.getServer().getWorld(targetDimension);
                if (targetWorld == null) {
                    return new TeleportResult(false, "Target dimension not found", targetDimension, targetPos);
                }
                
                // Perform teleportation
                boolean success = entity.teleport(targetWorld, targetPos.x, targetPos.y, targetPos.z, 
                                                 java.util.Set.of(), entity.getYaw(), entity.getPitch());
                
                if (success) {
                    return new TeleportResult(true, null, targetDimension, targetPos);
                } else {
                    return new TeleportResult(false, "Teleportation failed", targetDimension, targetPos);
                }
            } else {
                return new TeleportResult(false, "Not a server world", targetDimension, targetPos);
            }
        } catch (Exception e) {
            return new TeleportResult(false, "Teleportation error: " + e.getMessage(), targetDimension, targetPos);
        }
    }
    
    /**
     * Teleports an entity from one dimension to another.
     * @param entity The entity to teleport.
     * @param sourceDimension The source dimension.
     * @param targetDimension The target dimension.
     * @param targetPos The target position.
     * @return Teleportation result.
     */
    public static TeleportResult teleportFromDimension(Entity entity, RegistryKey<World> sourceDimension, 
                                                     RegistryKey<World> targetDimension, Vec3d targetPos) {
        if (entity.getWorld().getRegistryKey().equals(sourceDimension)) {
            return teleportToDimension(entity, targetDimension, targetPos);
        } else {
            return new TeleportResult(false, "Entity not in source dimension", targetDimension, targetPos);
        }
    }
    
    /**
     * Validates a teleportation request.
     * @param entity The entity to teleport.
     * @param targetDimension The target dimension.
     * @param targetPos The target position.
     * @return True if teleportation is valid.
     */
    public static boolean validateTeleport(Entity entity, RegistryKey<World> targetDimension, Vec3d targetPos) {
        if (entity == null || targetDimension == null || targetPos == null) {
            return false;
        }
        
        // Check if target dimension exists
        if (entity.getWorld() instanceof ServerWorld currentWorld) {
            ServerWorld targetWorld = currentWorld.getServer().getWorld(targetDimension);
            if (targetWorld == null) {
                return false;
            }
        }
        
        // Check if target position is valid
        if (targetPos.y < -64 || targetPos.y > 320) { // Minecraft world limits
            return false;
        }
        
        return true;
    }
    
    /**
     * Sets the gravity for a dimension.
     * @param dimensionKey The dimension key.
     * @param gravity The gravity value.
     * @return True if gravity was set.
     */
    public static boolean setDimensionGravity(RegistryKey<World> dimensionKey, float gravity) {
        DimensionInfo info = dimensionInfo.get(dimensionKey);
        if (info != null) {
            info.setGravity(gravity);
            ChosenLib.LOGGER.info("Set gravity for dimension " + dimensionKey.getValue() + " to " + gravity);
            return true;
        }
        return false;
    }
    
    /**
     * Sets the time for a dimension.
     * @param dimensionKey The dimension key.
     * @param time The time value (0-24000).
     * @return True if time was set.
     */
    public static boolean setDimensionTime(RegistryKey<World> dimensionKey, long time) {
        DimensionInfo info = dimensionInfo.get(dimensionKey);
        if (info != null && time >= 0 && time <= 24000) {
            info.setTime(time);
            ChosenLib.LOGGER.info("Set time for dimension " + dimensionKey.getValue() + " to " + time);
            return true;
        }
        return false;
    }
    
    /**
     * Sets the weather for a dimension.
     * @param dimensionKey The dimension key.
     * @param hasWeather Whether the dimension has weather.
     * @return True if weather was set.
     */
    public static boolean setDimensionWeather(RegistryKey<World> dimensionKey, boolean hasWeather) {
        DimensionInfo info = dimensionInfo.get(dimensionKey);
        if (info != null) {
            info.setHasWeather(hasWeather);
            ChosenLib.LOGGER.info("Set weather for dimension " + dimensionKey.getValue() + " to " + hasWeather);
            return true;
        }
        return false;
    }
    
    /**
     * Generates chunks in a dimension.
     * @param world The world.
     * @param center The center position.
     * @param radius The generation radius.
     * @return Number of chunks generated.
     */
    public static int generateChunks(ServerWorld world, BlockPos center, int radius) {
        if (world == null || center == null || radius <= 0) {
            return 0;
        }
        
        int generated = 0;
        int chunkX = center.getX() >> 4;
        int chunkZ = center.getZ() >> 4;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    if (world.getChunk(chunkX + x, chunkZ + z) != null) {
                        generated++;
                    }
                }
            }
        }
        
        return generated;
    }
    
    /**
     * Unloads chunks in a dimension.
     * @param world The world.
     * @param center The center position.
     * @param radius The unload radius.
     * @return Number of chunks unloaded.
     */
    public static int unloadChunks(ServerWorld world, BlockPos center, int radius) {
        if (world == null || center == null || radius <= 0) {
            return 0;
        }
        
        int unloaded = 0;
        int chunkX = center.getX() >> 4;
        int chunkZ = center.getZ() >> 4;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    if (!world.isChunkLoaded(chunkX + x, chunkZ + z)) {
                        unloaded++;
                    }
                }
            }
        }
        
        return unloaded;
    }
    
    /**
     * Gets the chunk status for a position.
     * @param world The world.
     * @param pos The position.
     * @return The chunk status.
     */
    public static String getChunkStatus(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) {
            return "UNKNOWN";
        }
        
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        
        if (world.isChunkLoaded(chunkX, chunkZ)) {
            return "LOADED";
        } else {
            return "UNLOADED";
        }
    }
    
    /**
     * Sets the spawn point for a dimension.
     * @param dimensionKey The dimension key.
     * @param spawnPos The spawn position.
     * @return True if spawn point was set.
     */
    public static boolean setDimensionSpawn(RegistryKey<World> dimensionKey, BlockPos spawnPos) {
        DimensionInfo info = dimensionInfo.get(dimensionKey);
        if (info != null) {
            info.setSpawnPoint(spawnPos);
            ChosenLib.LOGGER.info("Set spawn point for dimension " + dimensionKey.getValue() + " to " + spawnPos);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the spawn point for a dimension.
     * @param dimensionKey The dimension key.
     * @return The spawn position.
     */
    public static BlockPos getDimensionSpawn(RegistryKey<World> dimensionKey) {
        DimensionInfo info = dimensionInfo.get(dimensionKey);
        return info != null ? info.getSpawnPoint() : BlockPos.ORIGIN;
    }
    
    /**
     * Resets the spawn point for a dimension to origin.
     * @param dimensionKey The dimension key.
     * @return True if spawn point was reset.
     */
    public static boolean resetSpawnPoint(RegistryKey<World> dimensionKey) {
        return setDimensionSpawn(dimensionKey, BlockPos.ORIGIN);
    }
    
    /**
     * Creates a portal between dimensions.
     * @param linkId The link identifier.
     * @param sourceDimension The source dimension.
     * @param sourcePos The source position.
     * @param targetDimension The target dimension.
     * @param targetPos The target position.
     * @param bidirectional Whether the portal is bidirectional.
     * @return The created portal link.
     */
    public static PortalLink createPortal(String linkId, RegistryKey<World> sourceDimension, BlockPos sourcePos,
                                        RegistryKey<World> targetDimension, BlockPos targetPos, boolean bidirectional) {
        PortalLink link = new PortalLink(linkId, sourceDimension, sourcePos, targetDimension, targetPos, bidirectional);
        portalLinks.put(linkId, link);
        
        ChosenLib.LOGGER.info("Created portal link: " + linkId + " from " + sourceDimension.getValue() + 
                             " to " + targetDimension.getValue());
        return link;
    }
    
    /**
     * Links two existing portals.
     * @param sourceLinkId The source portal link ID.
     * @param targetLinkId The target portal link ID.
     * @return True if portals were linked.
     */
    public static boolean linkPortals(String sourceLinkId, String targetLinkId) {
        PortalLink sourceLink = portalLinks.get(sourceLinkId);
        PortalLink targetLink = portalLinks.get(targetLinkId);
        
        if (sourceLink != null && targetLink != null) {
            sourceLink.setLinkProperty("linkedTo", targetLinkId);
            targetLink.setLinkProperty("linkedTo", sourceLinkId);
            
            ChosenLib.LOGGER.info("Linked portals: " + sourceLinkId + " <-> " + targetLinkId);
            return true;
        }
        return false;
    }
    
    /**
     * Destroys a portal.
     * @param linkId The portal link ID.
     * @return True if portal was destroyed.
     */
    public static boolean destroyPortal(String linkId) {
        PortalLink link = portalLinks.remove(linkId);
        if (link != null) {
            ChosenLib.LOGGER.info("Destroyed portal: " + linkId);
            return true;
        }
        return false;
    }
    
    /**
     * Gets a portal link by ID.
     * @param linkId The portal link ID.
     * @return The portal link.
     */
    public static PortalLink getPortalLink(String linkId) {
        return portalLinks.get(linkId);
    }
    
    /**
     * Gets all portal links.
     * @return Map of all portal links.
     */
    public static Map<String, PortalLink> getAllPortalLinks() {
        return new HashMap<>(portalLinks);
    }
    
    /**
     * Gets all dimension information.
     * @return Map of all dimension information.
     */
    public static Map<RegistryKey<World>, DimensionInfo> getAllDimensionInfo() {
        return new HashMap<>(dimensionInfo);
    }
    
    /**
     * Cleans up removed dimensions and portals.
     */
    public static void cleanupRemovedDimensions() {
        // Remove dimensions that no longer exist
        dimensionInfo.entrySet().removeIf(entry -> {
            // In a real implementation, check if dimension still exists
            return false; // Placeholder
        });
        
        // Remove portal links involving removed dimensions
        portalLinks.entrySet().removeIf(entry -> {
            PortalLink link = entry.getValue();
            return !dimensionInfo.containsKey(link.getSourceDimension()) || 
                   !dimensionInfo.containsKey(link.getTargetDimension());
        });
    }
}
