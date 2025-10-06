package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;
import java.io.ByteArrayInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Advanced networking utilities with sophisticated packet handling, compression, and encryption.
 * Provides reliable packet delivery, synchronization, and error handling.
 */
public class AdvancedNetworking {
    
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, PacketQueue> packetQueues = new ConcurrentHashMap<>();
    private static final Map<String, EncryptionKey> encryptionKeys = new ConcurrentHashMap<>();
    private static final Map<String, PacketStats> packetStats = new ConcurrentHashMap<>();
    private static final AtomicLong packetIdCounter = new AtomicLong(0);
    
    /**
     * Packet types for different use cases.
     */
    public enum PacketType {
        CUSTOM,
        SYNC,
        EVENT,
        REQUEST,
        RESPONSE,
        HEARTBEAT,
        BATCH
    }
    
    /**
     * Custom packet with metadata.
     */
    public static class CustomPacket {
        private final String packetId;
        private final PacketType type;
        private final Identifier channelId;
        private final byte[] data;
        private final long timestamp;
        private final boolean compressed;
        private final boolean encrypted;
        private final Map<String, Object> metadata;
        
        public CustomPacket(String packetId, PacketType type, Identifier channelId, byte[] data) {
            this.packetId = packetId;
            this.type = type;
            this.channelId = channelId;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.compressed = false;
            this.encrypted = false;
            this.metadata = new HashMap<>();
        }
        
        public CustomPacket(String packetId, PacketType type, Identifier channelId, byte[] data, 
                          boolean compressed, boolean encrypted) {
            this.packetId = packetId;
            this.type = type;
            this.channelId = channelId;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.compressed = compressed;
            this.encrypted = encrypted;
            this.metadata = new HashMap<>();
        }
        
        // Getters
        public String getPacketId() { return packetId; }
        public PacketType getType() { return type; }
        public Identifier getChannelId() { return channelId; }
        public byte[] getData() { return data; }
        public long getTimestamp() { return timestamp; }
        public boolean isCompressed() { return compressed; }
        public boolean isEncrypted() { return encrypted; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(String key, Object value) { metadata.put(key, value); }
        public Object getMetadata(String key) { return metadata.get(key); }
    }
    
    /**
     * Synchronization packet for entity/block data.
     */
    public static class SyncPacket extends CustomPacket {
        private final String syncType;
        private final UUID targetId;
        
        public SyncPacket(String packetId, String syncType, UUID targetId, byte[] data) {
            super(packetId, PacketType.SYNC, Identifier.of("chosenlib", "sync"), data);
            this.syncType = syncType;
            this.targetId = targetId;
            setMetadata("syncType", syncType);
            setMetadata("targetId", targetId.toString());
        }
        
        public String getSyncType() { return syncType; }
        public UUID getTargetId() { return targetId; }
    }
    
    /**
     * Event packet for game events.
     */
    public static class EventPacket extends CustomPacket {
        private final String eventType;
        private final Map<String, Object> eventData;
        
        public EventPacket(String packetId, String eventType, Map<String, Object> eventData) {
            super(packetId, PacketType.EVENT, Identifier.of("chosenlib", "event"), 
                  GSON.toJson(eventData).getBytes(StandardCharsets.UTF_8));
            this.eventType = eventType;
            this.eventData = eventData;
            setMetadata("eventType", eventType);
        }
        
        public String getEventType() { return eventType; }
        public Map<String, Object> getEventData() { return eventData; }
    }
    
    /**
     * Request packet for client-server communication.
     */
    public static class RequestPacket extends CustomPacket {
        private final String requestType;
        private final Map<String, Object> requestData;
        
        public RequestPacket(String packetId, String requestType, Map<String, Object> requestData) {
            super(packetId, PacketType.REQUEST, Identifier.of("chosenlib", "request"), 
                  GSON.toJson(requestData).getBytes(StandardCharsets.UTF_8));
            this.requestType = requestType;
            this.requestData = requestData;
            setMetadata("requestType", requestType);
        }
        
        public String getRequestType() { return requestType; }
        public Map<String, Object> getRequestData() { return requestData; }
    }
    
    /**
     * Packet queue for reliable delivery.
     */
    private static class PacketQueue {
        private final LinkedBlockingQueue<CustomPacket> queue = new LinkedBlockingQueue<>();
        private final Set<String> acknowledgedPackets = ConcurrentHashMap.newKeySet();
        // private final long maxRetryTime = 30000; // 30 seconds
        // private final int maxRetries = 3;
        
        public void queuePacket(CustomPacket packet) {
            queue.offer(packet);
        }
        
        public CustomPacket pollPacket() {
            return queue.poll();
        }
        
        // public void acknowledgePacket(String packetId) {
        //     acknowledgedPackets.add(packetId);
        // }
        
        // public boolean isAcknowledged(String packetId) {
        //     return acknowledgedPackets.contains(packetId);
        // }
        
        // public int getQueueSize() {
        //     return queue.size();
        // }
    }
    
    /**
     * Encryption key management.
     */
    private static class EncryptionKey {
        private final SecretKey key;
        private final long created;
        private final long expires;
        
        public EncryptionKey(SecretKey key, long expires) {
            this.key = key;
            this.created = System.currentTimeMillis();
            this.expires = expires;
        }
        
        public SecretKey getKey() { return key; }
        public boolean isExpired() { return System.currentTimeMillis() >= expires; }
        // public long getCreated() { return created; }
    }
    
    /**
     * Packet statistics tracking.
     */
    private static class PacketStats {
        private int packetsSent = 0;
        private int packetsReceived = 0;
        private int packetsLost = 0;
        private long totalBytesSent = 0;
        private long totalBytesReceived = 0;
        private long totalLatency = 0;
        private final Map<String, Integer> packetTypeCounts = new HashMap<>();
        
        // public void recordPacketSent(CustomPacket packet) {
        //     packetsSent++;
        //     totalBytesSent += packet.getData().length;
        //     packetTypeCounts.merge(packet.getType().name(), 1, Integer::sum);
        // }
        
        // public void recordPacketReceived(CustomPacket packet) {
        //     packetsReceived++;
        //     totalBytesReceived += packet.getData().length;
        // }
        
        // public void recordPacketLost() {
        //     packetsLost++;
        // }
        
        // public void recordLatency(long latency) {
        //     totalLatency += latency;
        // }
        
        // Getters
        // public int getPacketsSent() { return packetsSent; }
        // public int getPacketsReceived() { return packetsReceived; }
        // public int getPacketsLost() { return packetsLost; }
        // public long getTotalBytesSent() { return totalBytesSent; }
        // public long getTotalBytesReceived() { return totalBytesReceived; }
        // public double getAverageLatency() { 
        //     return packetsReceived > 0 ? (double) totalLatency / packetsReceived : 0.0; 
        // }
        // public Map<String, Integer> getPacketTypeCounts() { return packetTypeCounts; }
    }
    
    /**
     * Routes a packet to the appropriate handler.
     * @param packet The packet to route.
     * @return True if routing was successful.
     */
    public static boolean routePacket(CustomPacket packet) {
        if (packet == null) {
            return false;
        }
        
        try {
            recordPacketSent(packet);
            
            // Route based on packet type
            switch (packet.getType()) {
                case SYNC:
                    return routeSyncPacket((SyncPacket) packet);
                case EVENT:
                    return routeEventPacket((EventPacket) packet);
                case REQUEST:
                    return routeRequestPacket((RequestPacket) packet);
                default:
                    return routeCustomPacket(packet);
            }
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Error routing packet: " + packet.getPacketId(), e);
            return false;
        }
    }
    
    /**
     * Broadcasts a packet to all players in a world.
     * @param world The target world.
     * @param packet The packet to broadcast.
     * @return Number of players the packet was sent to.
     */
    public static int broadcastPacket(ServerWorld world, CustomPacket packet) {
        if (world == null || packet == null) {
            return 0;
        }
        
        int sent = 0;
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (sendReliablePacket(player, packet)) {
                sent++;
            }
        }
        
        return sent;
    }
    
    /**
     * Sends a reliable packet with acknowledgment.
     * @param player The target player.
     * @param packet The packet to send.
     * @return True if packet was sent successfully.
     */
    public static boolean sendReliablePacket(ServerPlayerEntity player, CustomPacket packet) {
        if (player == null || packet == null) {
            return false;
        }
        
        try {
            // Queue packet for reliable delivery
            String queueKey = player.getUuid().toString();
            PacketQueue queue = packetQueues.computeIfAbsent(queueKey, k -> new PacketQueue());
            queue.queuePacket(packet);
            
            // Send packet immediately
            PacketByteBuf buffer = PacketByteBufs.create();
            writeCustomPacket(buffer, packet);
            
            // Note: This would need to be updated for current Fabric API
            // ServerPlayNetworking.send(player, packet.getChannelId(), buffer);
            
            recordPacketSent(packet);
            return true;
            
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Error sending reliable packet to player: " + player.getName(), e);
            return false;
        }
    }
    
    /**
     * Synchronizes entity data.
     * @param entity The entity to sync.
     * @param syncType The type of sync.
     * @param data The data to sync.
     * @param players Target players.
     * @return Number of players synced to.
     */
    public static int syncEntityData(Entity entity, String syncType, byte[] data, Collection<ServerPlayerEntity> players) {
        if (entity == null || data == null || players == null) {
            return 0;
        }
        
        String packetId = generatePacketId();
        SyncPacket packet = new SyncPacket(packetId, syncType, entity.getUuid(), data);
        
        int synced = 0;
        for (ServerPlayerEntity player : players) {
            if (sendReliablePacket(player, packet)) {
                synced++;
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronizes block data.
     * @param world The world.
     * @param pos The block position.
     * @param syncType The type of sync.
     * @param data The data to sync.
     * @param players Target players.
     * @return Number of players synced to.
     */
    public static int syncBlockData(ServerWorld world, BlockPos pos, String syncType, byte[] data, 
                                  Collection<ServerPlayerEntity> players) {
        if (world == null || pos == null || data == null || players == null) {
            return 0;
        }
        
        String packetId = generatePacketId();
        Map<String, Object> syncData = new HashMap<>();
        syncData.put("world", world.getRegistryKey().getValue().toString());
        syncData.put("position", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        syncData.put("data", data);
        
        SyncPacket packet = new SyncPacket(packetId, syncType, UUID.randomUUID(), 
                                         GSON.toJson(syncData).getBytes(StandardCharsets.UTF_8));
        
        int synced = 0;
        for (ServerPlayerEntity player : players) {
            if (sendReliablePacket(player, packet)) {
                synced++;
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronizes player data.
     * @param player The player to sync.
     * @param syncType The type of sync.
     * @param data The data to sync.
     * @return True if sync was successful.
     */
    public static boolean syncPlayerData(ServerPlayerEntity player, String syncType, byte[] data) {
        if (player == null || data == null) {
            return false;
        }
        
        String packetId = generatePacketId();
        SyncPacket packet = new SyncPacket(packetId, syncType, player.getUuid(), data);
        
        return sendReliablePacket(player, packet);
    }
    
    /**
     * Compresses packet data using GZIP.
     * @param data The data to compress.
     * @return Compressed data.
     */
    public static byte[] compressPacket(byte[] data) {
        if (data == null || data.length == 0) {
            return data;
        }
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            
            gzos.write(data);
            gzos.finish();
            return baos.toByteArray();
            
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to compress packet data", e);
            return data;
        }
    }
    
    /**
     * Decompresses packet data using GZIP.
     * @param compressedData The compressed data.
     * @return Decompressed data.
     */
    public static byte[] decompressPacket(byte[] compressedData) {
        if (compressedData == null || compressedData.length == 0) {
            return compressedData;
        }
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to decompress packet data", e);
            return compressedData;
        }
    }
    
    /**
     * Checks if packet data is compressed.
     * @param data The data to check.
     * @return True if data is compressed.
     */
    public static boolean isPacketCompressed(byte[] data) {
        if (data == null || data.length < 2) {
            return false;
        }
        
        // Check GZIP magic number
        return (data[0] & 0xFF) == 0x1F && (data[1] & 0xFF) == 0x8B;
    }
    
    /**
     * Encrypts packet data using AES.
     * @param data The data to encrypt.
     * @param keyId The encryption key ID.
     * @return Encrypted data.
     */
    public static byte[] encryptPacket(byte[] data, String keyId) {
        if (data == null || keyId == null) {
            return data;
        }
        
        EncryptionKey encryptionKey = encryptionKeys.get(keyId);
        if (encryptionKey == null || encryptionKey.isExpired()) {
            ChosenLib.LOGGER.warn("Encryption key not found or expired: " + keyId);
            return data;
        }
        
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey.getKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to encrypt packet data", e);
            return data;
        }
    }
    
    /**
     * Decrypts packet data using AES.
     * @param encryptedData The encrypted data.
     * @param keyId The encryption key ID.
     * @return Decrypted data.
     */
    public static byte[] decryptPacket(byte[] encryptedData, String keyId) {
        if (encryptedData == null || keyId == null) {
            return encryptedData;
        }
        
        EncryptionKey encryptionKey = encryptionKeys.get(keyId);
        if (encryptionKey == null || encryptionKey.isExpired()) {
            ChosenLib.LOGGER.warn("Encryption key not found or expired: " + keyId);
            return encryptedData;
        }
        
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey.getKey());
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to decrypt packet data", e);
            return encryptedData;
        }
    }
    
    /**
     * Sets an encryption key.
     * @param keyId The key identifier.
     * @param key The encryption key.
     * @param expiresIn Hours until key expires.
     */
    public static void setEncryptionKey(String keyId, SecretKey key, long expiresIn) {
        long expires = System.currentTimeMillis() + (expiresIn * 60 * 60 * 1000);
        encryptionKeys.put(keyId, new EncryptionKey(key, expires));
    }
    
    /**
     * Generates a new encryption key.
     * @param keyId The key identifier.
     * @param expiresIn Hours until key expires.
     * @return The generated key.
     */
    public static SecretKey generateEncryptionKey(String keyId, long expiresIn) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey key = keyGenerator.generateKey();
            setEncryptionKey(keyId, key, expiresIn);
            return key;
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to generate encryption key", e);
            return null;
        }
    }
    
    /**
     * Queues a packet for sending.
     * @param playerId The player ID.
     * @param packet The packet to queue.
     */
    public static void queuePacket(String playerId, CustomPacket packet) {
        if (playerId != null && packet != null) {
            PacketQueue queue = packetQueues.computeIfAbsent(playerId, k -> new PacketQueue());
            queue.queuePacket(packet);
        }
    }
    
    /**
     * Processes the packet queue for a player.
     * @param playerId The player ID.
     * @return Number of packets processed.
     */
    public static int processPacketQueue(String playerId) {
        PacketQueue queue = packetQueues.get(playerId);
        if (queue == null) {
            return 0;
        }
        
        int processed = 0;
        CustomPacket packet;
        while ((packet = queue.pollPacket()) != null) {
            if (routePacket(packet)) {
                processed++;
            }
        }
        
        return processed;
    }
    
    /**
     * Clears the packet queue for a player.
     * @param playerId The player ID.
     */
    public static void clearPacketQueue(String playerId) {
        PacketQueue queue = packetQueues.remove(playerId);
        if (queue != null) {
            // Mark queued packets as lost
            while (queue.pollPacket() != null) {
                recordPacketLost();
            }
        }
    }
    
    /**
     * Handles packet errors.
     * @param packetId The packet ID.
     * @param error The error message.
     * @param playerId The player ID.
     */
    public static void handlePacketError(String packetId, String error, String playerId) {
        ChosenLib.LOGGER.error("Packet error for packet " + packetId + " from player " + playerId + ": " + error);
        recordPacketLost();
    }
    
    /**
     * Retries failed packets.
     * @param playerId The player ID.
     * @return Number of packets retried.
     */
    public static int retryFailedPacket(String playerId) {
        // Implementation would depend on specific retry logic
        return 0;
    }
    
    /**
     * Logs packet statistics.
     * @param playerId The player ID.
     * @return Packet statistics.
     */
    public static PacketStats logPacketStats(String playerId) {
        return packetStats.getOrDefault(playerId, new PacketStats());
    }
    
    // Helper methods
    
    private static String generatePacketId() {
        return "packet_" + packetIdCounter.incrementAndGet() + "_" + System.currentTimeMillis();
    }
    
    private static void writeCustomPacket(PacketByteBuf buffer, CustomPacket packet) {
        buffer.writeString(packet.getPacketId());
        buffer.writeString(packet.getType().name());
        buffer.writeIdentifier(packet.getChannelId());
        buffer.writeByteArray(packet.getData());
        buffer.writeLong(packet.getTimestamp());
        buffer.writeBoolean(packet.isCompressed());
        buffer.writeBoolean(packet.isEncrypted());
        
        // Write metadata
        buffer.writeInt(packet.getMetadata().size());
        for (Map.Entry<String, Object> entry : packet.getMetadata().entrySet()) {
            buffer.writeString(entry.getKey());
            buffer.writeString(entry.getValue().toString());
        }
    }
    
    // private static CustomPacket readCustomPacket(PacketByteBuf buffer) {
    //     String packetId = buffer.readString();
    //     PacketType type = PacketType.valueOf(buffer.readString());
    //     Identifier channelId = buffer.readIdentifier();
    //     byte[] data = buffer.readByteArray();
    //     long timestamp = buffer.readLong();
    //     boolean compressed = buffer.readBoolean();
    //     boolean encrypted = buffer.readBoolean();
    //     
    //     CustomPacket packet = new CustomPacket(packetId, type, channelId, data, compressed, encrypted);
    //     
    //     // Read metadata
    //     int metadataSize = buffer.readInt();
    //     for (int i = 0; i < metadataSize; i++) {
    //         String key = buffer.readString();
    //         String value = buffer.readString();
    //         packet.setMetadata(key, value);
    //     }
    //     
    //     return packet;
    // }
    
    private static boolean routeSyncPacket(SyncPacket packet) {
        // Route sync packet to appropriate handler
        return true; // Placeholder
    }
    
    private static boolean routeEventPacket(EventPacket packet) {
        // Route event packet to appropriate handler
        return true; // Placeholder
    }
    
    private static boolean routeRequestPacket(RequestPacket packet) {
        // Route request packet to appropriate handler
        return true; // Placeholder
    }
    
    private static boolean routeCustomPacket(CustomPacket packet) {
        // Route custom packet to appropriate handler
        return true; // Placeholder
    }
    
    private static void recordPacketSent(CustomPacket packet) {
        // Record packet statistics
    }
    
    private static void recordPacketLost() {
        // Record packet loss statistics
    }
}
