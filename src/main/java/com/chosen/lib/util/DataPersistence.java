package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Data persistence utilities for storing and retrieving game data.
 * Provides world, player, and global data storage with compression and validation.
 */
public class DataPersistence {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_DIR = "chosenlib_data";
    private static final String BACKUP_DIR = "chosenlib_backups";
    private static final String COMPRESSION_EXTENSION = ".gz";
    private static final String JSON_EXTENSION = ".json";
    
    private static final Map<String, DataCache> dataCache = new ConcurrentHashMap<>();
    private static final Map<String, Long> lastModified = new ConcurrentHashMap<>();
    
    /**
     * Data cache for performance optimization.
     */
    private static class DataCache {
        private final JsonObject data;
        private final long timestamp;
        private final boolean compressed;
        
        public DataCache(JsonObject data, boolean compressed) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.compressed = compressed;
        }
        
        public JsonObject getData() { return data; }
        // public long getTimestamp() { return timestamp; }
        // public boolean isCompressed() { return compressed; }
        public boolean isExpired(long maxAge) {
            return System.currentTimeMillis() - timestamp > maxAge;
        }
    }
    
    /**
     * Data validation result.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final Map<String, Object> metadata;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.metadata = new HashMap<>();
        }
        
        public ValidationResult(boolean valid, String errorMessage, Map<String, Object> metadata) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.metadata = metadata;
        }
        
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
    
    /**
     * Data backup information.
     */
    public static class BackupInfo {
        private final String backupId;
        private final long timestamp;
        private final String originalPath;
        private final long size;
        private final boolean compressed;
        
        public BackupInfo(String backupId, long timestamp, String originalPath, long size, boolean compressed) {
            this.backupId = backupId;
            this.timestamp = timestamp;
            this.originalPath = originalPath;
            this.size = size;
            this.compressed = compressed;
        }
        
        public String getBackupId() { return backupId; }
        public long getTimestamp() { return timestamp; }
        public String getOriginalPath() { return originalPath; }
        public long getSize() { return size; }
        public boolean isCompressed() { return compressed; }
    }
    
    /**
     * Saves world-specific data.
     * @param world The world.
     * @param key The data key.
     * @param data The data to save.
     * @param compress Whether to compress the data.
     * @return True if save was successful.
     */
    public static boolean saveWorldData(ServerWorld world, String key, JsonObject data, boolean compress) {
        if (world == null || key == null || data == null) {
            return false;
        }
        
        try {
            String worldName = world.getRegistryKey().getValue().toString().replace(":", "_");
            Path dataPath = getWorldDataPath(worldName, key);
            return saveDataToFile(dataPath, data, compress);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to save world data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Loads world-specific data.
     * @param world The world.
     * @param key The data key.
     * @return The loaded data, or null if not found.
     */
    public static JsonObject loadWorldData(ServerWorld world, String key) {
        if (world == null || key == null) {
            return null;
        }
        
        try {
            String worldName = world.getRegistryKey().getValue().toString().replace(":", "_");
            Path dataPath = getWorldDataPath(worldName, key);
            return loadDataFromFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to load world data for key: " + key, e);
            return null;
        }
    }
    
    /**
     * Deletes world-specific data.
     * @param world The world.
     * @param key The data key.
     * @return True if deletion was successful.
     */
    public static boolean deleteWorldData(ServerWorld world, String key) {
        if (world == null || key == null) {
            return false;
        }
        
        try {
            String worldName = world.getRegistryKey().getValue().toString().replace(":", "_");
            Path dataPath = getWorldDataPath(worldName, key);
            return deleteDataFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to delete world data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Saves player-specific data.
     * @param player The player.
     * @param key The data key.
     * @param data The data to save.
     * @param compress Whether to compress the data.
     * @return True if save was successful.
     */
    public static boolean savePlayerData(ServerPlayerEntity player, String key, JsonObject data, boolean compress) {
        if (player == null || key == null || data == null) {
            return false;
        }
        
        try {
            String playerUuid = player.getUuid().toString();
            Path dataPath = getPlayerDataPath(playerUuid, key);
            return saveDataToFile(dataPath, data, compress);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to save player data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Loads player-specific data.
     * @param player The player.
     * @param key The data key.
     * @return The loaded data, or null if not found.
     */
    public static JsonObject loadPlayerData(ServerPlayerEntity player, String key) {
        if (player == null || key == null) {
            return null;
        }
        
        try {
            String playerUuid = player.getUuid().toString();
            Path dataPath = getPlayerDataPath(playerUuid, key);
            return loadDataFromFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to load player data for key: " + key, e);
            return null;
        }
    }
    
    /**
     * Deletes player-specific data.
     * @param player The player.
     * @param key The data key.
     * @return True if deletion was successful.
     */
    public static boolean deletePlayerData(ServerPlayerEntity player, String key) {
        if (player == null || key == null) {
            return false;
        }
        
        try {
            String playerUuid = player.getUuid().toString();
            Path dataPath = getPlayerDataPath(playerUuid, key);
            return deleteDataFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to delete player data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Saves global data (server-wide).
     * @param server The Minecraft server.
     * @param key The data key.
     * @param data The data to save.
     * @param compress Whether to compress the data.
     * @return True if save was successful.
     */
    public static boolean saveGlobalData(MinecraftServer server, String key, JsonObject data, boolean compress) {
        if (server == null || key == null || data == null) {
            return false;
        }
        
        try {
            Path dataPath = getGlobalDataPath(key);
            return saveDataToFile(dataPath, data, compress);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to save global data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Loads global data.
     * @param server The Minecraft server.
     * @param key The data key.
     * @return The loaded data, or null if not found.
     */
    public static JsonObject loadGlobalData(MinecraftServer server, String key) {
        if (server == null || key == null) {
            return null;
        }
        
        try {
            Path dataPath = getGlobalDataPath(key);
            return loadDataFromFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to load global data for key: " + key, e);
            return null;
        }
    }
    
    /**
     * Deletes global data.
     * @param server The Minecraft server.
     * @param key The data key.
     * @return True if deletion was successful.
     */
    public static boolean deleteGlobalData(MinecraftServer server, String key) {
        if (server == null || key == null) {
            return false;
        }
        
        try {
            Path dataPath = getGlobalDataPath(key);
            return deleteDataFile(dataPath);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to delete global data for key: " + key, e);
            return false;
        }
    }
    
    /**
     * Validates data integrity.
     * @param data The data to validate.
     * @return Validation result.
     */
    public static ValidationResult validateData(JsonObject data) {
        if (data == null) {
            return new ValidationResult(false, "Data is null");
        }
        
        try {
            // Check for required fields
            if (!data.has("version")) {
                return new ValidationResult(false, "Missing version field");
            }
            
            if (!data.has("timestamp")) {
                return new ValidationResult(false, "Missing timestamp field");
            }
            
            // Validate timestamp
            long timestamp = data.get("timestamp").getAsLong();
            long currentTime = System.currentTimeMillis();
            if (timestamp > currentTime || timestamp < (currentTime - 31536000000L)) { // 1 year
                return new ValidationResult(false, "Invalid timestamp");
            }
            
            // Check data size
            String jsonString = GSON.toJson(data);
            if (jsonString.length() > 10485760) { // 10MB limit
                return new ValidationResult(false, "Data too large");
            }
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("size", jsonString.length());
            metadata.put("version", data.get("version").getAsString());
            metadata.put("timestamp", timestamp);
            
            return new ValidationResult(true, "Data is valid", metadata);
            
        } catch (Exception e) {
            return new ValidationResult(false, "Validation error: " + e.getMessage());
        }
    }
    
    /**
     * Creates a backup of data.
     * @param originalPath The original data path.
     * @return Backup information.
     */
    public static BackupInfo backupData(Path originalPath) {
        if (originalPath == null || !Files.exists(originalPath)) {
            return null;
        }
        
        try {
            String backupId = "backup_" + System.currentTimeMillis();
            Path backupPath = getBackupPath(backupId);
            
            Files.createDirectories(backupPath.getParent());
            Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            
            long size = Files.size(backupPath);
            boolean compressed = originalPath.toString().endsWith(COMPRESSION_EXTENSION);
            
            return new BackupInfo(backupId, System.currentTimeMillis(), originalPath.toString(), size, compressed);
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to create backup for: " + originalPath, e);
            return null;
        }
    }
    
    /**
     * Restores data from a backup.
     * @param backupId The backup ID.
     * @param targetPath The target path to restore to.
     * @return True if restoration was successful.
     */
    public static boolean restoreData(String backupId, Path targetPath) {
        if (backupId == null || targetPath == null) {
            return false;
        }
        
        try {
            Path backupPath = getBackupPath(backupId);
            if (!Files.exists(backupPath)) {
                return false;
            }
            
            Files.createDirectories(targetPath.getParent());
            Files.copy(backupPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to restore backup: " + backupId, e);
            return false;
        }
    }
    
    /**
     * Compresses data using GZIP.
     * @param data The data to compress.
     * @return Compressed data as byte array.
     */
    public static byte[] compressData(String data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            
            gzos.write(data.getBytes("UTF-8"));
            gzos.finish();
            return baos.toByteArray();
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to compress data", e);
            return null;
        }
    }
    
    /**
     * Decompresses data using GZIP.
     * @param compressedData The compressed data.
     * @return Decompressed data as string.
     */
    public static String decompressData(byte[] compressedData) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzis, "UTF-8"))) {
            
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to decompress data", e);
            return null;
        }
    }
    
    /**
     * Checks if data is compressed.
     * @param data The data to check.
     * @return True if data is compressed.
     */
    public static boolean isCompressed(byte[] data) {
        if (data == null || data.length < 2) {
            return false;
        }
        
        // Check GZIP magic number
        return (data[0] & 0xFF) == 0x1F && (data[1] & 0xFF) == 0x8B;
    }
    
    /**
     * Migrates data to a new format.
     * @param data The data to migrate.
     * @param fromVersion Source version.
     * @param toVersion Target version.
     * @return Migrated data.
     */
    public static JsonObject migrateData(JsonObject data, String fromVersion, String toVersion) {
        if (data == null || fromVersion == null || toVersion == null) {
            return data;
        }
        
        try {
            JsonObject migratedData = data.deepCopy();
            
            // Add migration logic based on versions
            if ("1.0".equals(fromVersion) && "1.1".equals(toVersion)) {
                // Example migration from 1.0 to 1.1
                migratedData.addProperty("migrated_from", fromVersion);
                migratedData.addProperty("migrated_at", System.currentTimeMillis());
            }
            
            // Update version
            migratedData.addProperty("version", toVersion);
            migratedData.addProperty("last_migration", System.currentTimeMillis());
            
            return migratedData;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to migrate data from " + fromVersion + " to " + toVersion, e);
            return data;
        }
    }
    
    /**
     * Checks the data version.
     * @param data The data to check.
     * @return The data version, or null if not found.
     */
    public static String checkDataVersion(JsonObject data) {
        if (data == null || !data.has("version")) {
            return null;
        }
        
        return data.get("version").getAsString();
    }
    
    /**
     * Updates data format to current version.
     * @param data The data to update.
     * @param currentVersion The current version.
     * @return Updated data.
     */
    public static JsonObject updateDataFormat(JsonObject data, String currentVersion) {
        if (data == null) {
            return new JsonObject();
        }
        
        String dataVersion = checkDataVersion(data);
        if (dataVersion == null) {
            dataVersion = "1.0"; // Default version
        }
        
        if (!currentVersion.equals(dataVersion)) {
            data = migrateData(data, dataVersion, currentVersion);
        }
        
        // Ensure required fields exist
        if (!data.has("timestamp")) {
            data.addProperty("timestamp", System.currentTimeMillis());
        }
        
        return data;
    }
    
    // Helper methods
    
    private static Path getWorldDataPath(String worldName, String key) {
        return Paths.get(DATA_DIR, "worlds", worldName, key + JSON_EXTENSION);
    }
    
    private static Path getPlayerDataPath(String playerUuid, String key) {
        return Paths.get(DATA_DIR, "players", playerUuid, key + JSON_EXTENSION);
    }
    
    private static Path getGlobalDataPath(String key) {
        return Paths.get(DATA_DIR, "global", key + JSON_EXTENSION);
    }
    
    private static Path getBackupPath(String backupId) {
        return Paths.get(BACKUP_DIR, backupId + JSON_EXTENSION);
    }
    
    private static boolean saveDataToFile(Path dataPath, JsonObject data, boolean compress) {
        try {
            Files.createDirectories(dataPath.getParent());
            
            // Add metadata
            data.addProperty("version", "1.5.0");
            data.addProperty("timestamp", System.currentTimeMillis());
            
            String jsonString = GSON.toJson(data);
            
            if (compress) {
                byte[] compressedData = compressData(jsonString);
                if (compressedData != null) {
                    Files.write(dataPath.getParent().resolve(dataPath.getFileName() + COMPRESSION_EXTENSION), compressedData);
                } else {
                    return false;
                }
            } else {
                Files.writeString(dataPath, jsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            
            // Update cache
            String cacheKey = dataPath.toString();
            dataCache.put(cacheKey, new DataCache(data, compress));
            lastModified.put(cacheKey, System.currentTimeMillis());
            
            return true;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to save data to file: " + dataPath, e);
            return false;
        }
    }
    
    private static JsonObject loadDataFromFile(Path dataPath) {
        try {
            // Check cache first
            String cacheKey = dataPath.toString();
            DataCache cached = dataCache.get(cacheKey);
            if (cached != null && !cached.isExpired(300000)) { // 5 minute cache
                return cached.getData();
            }
            
            Path actualPath = dataPath;
            boolean compressed = false;
            
            // Check if compressed version exists
            Path compressedPath = dataPath.getParent().resolve(dataPath.getFileName() + COMPRESSION_EXTENSION);
            if (Files.exists(compressedPath)) {
                actualPath = compressedPath;
                compressed = true;
            }
            
            if (!Files.exists(actualPath)) {
                return null;
            }
            
            JsonObject data;
            if (compressed) {
                byte[] compressedData = Files.readAllBytes(actualPath);
                String jsonString = decompressData(compressedData);
                if (jsonString == null) {
                    return null;
                }
                data = GSON.fromJson(jsonString, JsonObject.class);
            } else {
                String jsonString = Files.readString(actualPath);
                data = GSON.fromJson(jsonString, JsonObject.class);
            }
            
            // Update cache
            dataCache.put(cacheKey, new DataCache(data, compressed));
            lastModified.put(cacheKey, System.currentTimeMillis());
            
            return data;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to load data from file: " + dataPath, e);
            return null;
        }
    }
    
    private static boolean deleteDataFile(Path dataPath) {
        try {
            boolean deleted = false;
            
            // Delete regular file
            if (Files.exists(dataPath)) {
                Files.delete(dataPath);
                deleted = true;
            }
            
            // Delete compressed file
            Path compressedPath = dataPath.getParent().resolve(dataPath.getFileName() + COMPRESSION_EXTENSION);
            if (Files.exists(compressedPath)) {
                Files.delete(compressedPath);
                deleted = true;
            }
            
            // Remove from cache
            String cacheKey = dataPath.toString();
            dataCache.remove(cacheKey);
            lastModified.remove(cacheKey);
            
            return deleted;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to delete data file: " + dataPath, e);
            return false;
        }
    }
}
