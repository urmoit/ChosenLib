package com.chosen.lib.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Utility class for networking and packet operations.
 * Provides helpers for sending packets, managing network data, and communication.
 */
public class NetworkUtils {

    /**
     * Creates a new packet buffer.
     * @return A new PacketByteBuf instance.
     */
    public static PacketByteBuf createBuffer() {
        return PacketByteBufs.create();
    }

    /**
     * Sends a custom payload packet to a specific player.
     * @param player The target player.
     * @param payload The custom payload to send.
     * @return True if the packet was sent successfully.
     */
    public static boolean sendToPlayer(ServerPlayerEntity player, CustomPayload payload) {
        try {
            ServerPlayNetworking.send(player, payload);
            return true;
        } catch (Exception e) {
            // Log error if needed
        }
        return false;
    }

    /**
     * Sends a custom payload packet to multiple players.
     * @param players The target players.
     * @param payload The custom payload to send.
     * @return The number of players the packet was sent to.
     */
    public static int sendToPlayers(Collection<ServerPlayerEntity> players, CustomPayload payload) {
        int sent = 0;
        for (ServerPlayerEntity player : players) {
            if (sendToPlayer(player, payload)) {
                sent++;
            }
        }
        return sent;
    }

    /**
     * Sends a custom payload packet to all players in a world.
     * @param world The target world.
     * @param payload The custom payload to send.
     * @return The number of players the packet was sent to.
     */
    public static int sendToWorld(ServerWorld world, CustomPayload payload) {
        return sendToPlayers(world.getPlayers(), payload);
    }

    /**
     * Sends a custom payload packet to all players within a radius of a position.
     * @param world The world.
     * @param center The center position.
     * @param radius The radius.
     * @param payload The custom payload to send.
     * @return The number of players the packet was sent to.
     */
    public static int sendToPlayersInRadius(ServerWorld world, Vec3d center, double radius,
                                          CustomPayload payload) {
        List<ServerPlayerEntity> nearbyPlayers = world.getPlayers(player ->
            player.getPos().distanceTo(center) <= radius);
        return sendToPlayers(nearbyPlayers, payload);
    }

    /**
     * Sends a custom payload packet to all players within a radius of a block position.
     * @param world The world.
     * @param center The center block position.
     * @param radius The radius.
     * @param payload The custom payload to send.
     * @return The number of players the packet was sent to.
     */
    public static int sendToPlayersInRadius(ServerWorld world, BlockPos center, double radius,
                                          CustomPayload payload) {
        return sendToPlayersInRadius(world, Vec3d.ofCenter(center), radius, payload);
    }

    /**
     * Checks if a player can receive a specific packet type.
     * @param player The player.
     * @param channelId The packet channel identifier.
     * @return True if the player can receive the packet.
     */
    public static boolean canSendToPlayer(ServerPlayerEntity player, Identifier channelId) {
        return ServerPlayNetworking.canSend(player, channelId);
    }

    /**
     * Writes a string to a packet buffer safely.
     * @param buffer The packet buffer.
     * @param string The string to write.
     */
    public static void writeString(PacketByteBuf buffer, String string) {
        if (string == null) {
            buffer.writeString("");
        } else {
            buffer.writeString(string);
        }
    }

    /**
     * Reads a string from a packet buffer safely.
     * @param buffer The packet buffer.
     * @return The string, or empty string if null.
     */
    public static String readString(PacketByteBuf buffer) {
        try {
            return buffer.readString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Writes a Text component to a packet buffer.
     * @param buffer The packet buffer.
     * @param text The text component.
     */
    public static void writeText(PacketByteBuf buffer, Text text) {
        if (text == null) {
            buffer.writeString(Text.empty().getString());
        } else {
            buffer.writeString(text.getString());
        }
    }

    /**
     * Reads a Text component from a packet buffer.
     * @param buffer The packet buffer.
     * @return The text component.
     */
    public static Text readText(PacketByteBuf buffer) {
        try {
            return Text.literal(buffer.readString());
        } catch (Exception e) {
            return Text.empty();
        }
    }

    /**
     * Writes a BlockPos to a packet buffer.
     * @param buffer The packet buffer.
     * @param pos The block position.
     */
    public static void writeBlockPos(PacketByteBuf buffer, BlockPos pos) {
        if (pos == null) {
            buffer.writeBlockPos(BlockPos.ORIGIN);
        } else {
            buffer.writeBlockPos(pos);
        }
    }

    /**
     * Reads a BlockPos from a packet buffer.
     * @param buffer The packet buffer.
     * @return The block position.
     */
    public static BlockPos readBlockPos(PacketByteBuf buffer) {
        try {
            return buffer.readBlockPos();
        } catch (Exception e) {
            return BlockPos.ORIGIN;
        }
    }

    /**
     * Writes a Vec3d to a packet buffer.
     * @param buffer The packet buffer.
     * @param vec The vector.
     */
    public static void writeVec3d(PacketByteBuf buffer, Vec3d vec) {
        if (vec == null) {
            buffer.writeDouble(0.0);
            buffer.writeDouble(0.0);
            buffer.writeDouble(0.0);
        } else {
            buffer.writeDouble(vec.x);
            buffer.writeDouble(vec.y);
            buffer.writeDouble(vec.z);
        }
    }

    /**
     * Reads a Vec3d from a packet buffer.
     * @param buffer The packet buffer.
     * @return The vector.
     */
    public static Vec3d readVec3d(PacketByteBuf buffer) {
        try {
            double x = buffer.readDouble();
            double y = buffer.readDouble();
            double z = buffer.readDouble();
            return new Vec3d(x, y, z);
        } catch (Exception e) {
            return Vec3d.ZERO;
        }
    }

    /**
     * Writes a UUID to a packet buffer.
     * @param buffer The packet buffer.
     * @param uuid The UUID.
     */
    public static void writeUuid(PacketByteBuf buffer, UUID uuid) {
        if (uuid == null) {
            buffer.writeUuid(new UUID(0, 0));
        } else {
            buffer.writeUuid(uuid);
        }
    }

    /**
     * Reads a UUID from a packet buffer.
     * @param buffer The packet buffer.
     * @return The UUID.
     */
    public static UUID readUuid(PacketByteBuf buffer) {
        try {
            return buffer.readUuid();
        } catch (Exception e) {
            return new UUID(0, 0);
        }
    }

    /**
     * Writes an Identifier to a packet buffer.
     * @param buffer The packet buffer.
     * @param identifier The identifier.
     */
    public static void writeIdentifier(PacketByteBuf buffer, Identifier identifier) {
        if (identifier == null) {
            buffer.writeIdentifier(Identifier.of("minecraft", "air"));
        } else {
            buffer.writeIdentifier(identifier);
        }
    }

    /**
     * Reads an Identifier from a packet buffer.
     * @param buffer The packet buffer.
     * @return The identifier.
     */
    public static Identifier readIdentifier(PacketByteBuf buffer) {
        try {
            return buffer.readIdentifier();
        } catch (Exception e) {
            return Identifier.of("minecraft", "air");
        }
    }

    /**
     * Writes a boolean array to a packet buffer.
     * @param buffer The packet buffer.
     * @param array The boolean array.
     */
    public static void writeBooleanArray(PacketByteBuf buffer, boolean[] array) {
        if (array == null) {
            buffer.writeVarInt(0);
        } else {
            buffer.writeVarInt(array.length);
            for (boolean value : array) {
                buffer.writeBoolean(value);
            }
        }
    }

    /**
     * Reads a boolean array from a packet buffer.
     * @param buffer The packet buffer.
     * @return The boolean array.
     */
    public static boolean[] readBooleanArray(PacketByteBuf buffer) {
        try {
            int length = buffer.readVarInt();
            boolean[] array = new boolean[length];
            for (int i = 0; i < length; i++) {
                array[i] = buffer.readBoolean();
            }
            return array;
        } catch (Exception e) {
            return new boolean[0];
        }
    }

    /**
     * Writes an integer array to a packet buffer.
     * @param buffer The packet buffer.
     * @param array The integer array.
     */
    public static void writeIntArray(PacketByteBuf buffer, int[] array) {
        if (array == null) {
            buffer.writeVarInt(0);
        } else {
            buffer.writeVarInt(array.length);
            for (int value : array) {
                buffer.writeVarInt(value);
            }
        }
    }

    /**
     * Reads an integer array from a packet buffer.
     * @param buffer The packet buffer.
     * @return The integer array.
     */
    public static int[] readIntArray(PacketByteBuf buffer) {
        try {
            int length = buffer.readVarInt();
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = buffer.readVarInt();
            }
            return array;
        } catch (Exception e) {
            return new int[0];
        }
    }

    /**
     * Writes a string array to a packet buffer.
     * @param buffer The packet buffer.
     * @param array The string array.
     */
    public static void writeStringArray(PacketByteBuf buffer, String[] array) {
        if (array == null) {
            buffer.writeVarInt(0);
        } else {
            buffer.writeVarInt(array.length);
            for (String value : array) {
                writeString(buffer, value);
            }
        }
    }

    /**
     * Reads a string array from a packet buffer.
     * @param buffer The packet buffer.
     * @return The string array.
     */
    public static String[] readStringArray(PacketByteBuf buffer) {
        try {
            int length = buffer.readVarInt();
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = readString(buffer);
            }
            return array;
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Creates a packet buffer with pre-written data using a consumer.
     * @param writer The consumer that writes data to the buffer.
     * @return The created packet buffer.
     */
    public static PacketByteBuf createBuffer(Consumer<PacketByteBuf> writer) {
        PacketByteBuf buffer = createBuffer();
        writer.accept(buffer);
        return buffer;
    }

    /**
     * Safely executes a buffer operation, catching any exceptions.
     * @param buffer The packet buffer.
     * @param operation The operation to perform.
     * @return True if the operation succeeded.
     */
    public static boolean safeBufferOperation(PacketByteBuf buffer, Consumer<PacketByteBuf> operation) {
        try {
            operation.accept(buffer);
            return true;
        } catch (Exception e) {
            // Log error if needed
            return false;
        }
    }

    /**
     * Gets the remaining readable bytes in a buffer.
     * @param buffer The packet buffer.
     * @return The number of readable bytes remaining.
     */
    public static int getReadableBytes(PacketByteBuf buffer) {
        return buffer.readableBytes();
    }

    /**
     * Checks if a buffer has readable bytes remaining.
     * @param buffer The packet buffer.
     * @return True if there are readable bytes remaining.
     */
    public static boolean hasReadableBytes(PacketByteBuf buffer) {
        return buffer.readableBytes() > 0;
    }

    /**
     * Skips a specified number of bytes in a buffer.
     * @param buffer The packet buffer.
     * @param bytes The number of bytes to skip.
     * @return True if the bytes were skipped successfully.
     */
    public static boolean skipBytes(PacketByteBuf buffer, int bytes) {
        try {
            if (buffer.readableBytes() >= bytes) {
                buffer.skipBytes(bytes);
                return true;
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return false;
    }

    /**
     * Resets the reader index of a buffer to the beginning.
     * @param buffer The packet buffer.
     */
    public static void resetReaderIndex(PacketByteBuf buffer) {
        buffer.resetReaderIndex();
    }

    /**
     * Marks the current reader index for later reset.
     * @param buffer The packet buffer.
     */
    public static void markReaderIndex(PacketByteBuf buffer) {
        buffer.markReaderIndex();
    }

    /**
     * Creates a simple packet with just a string message.
     * @param message The message to include.
     * @return The packet buffer.
     */
    public static PacketByteBuf createSimplePacket(String message) {
        return createBuffer(buffer -> writeString(buffer, message));
    }

    /**
     * Creates a position packet with a block position.
     * @param pos The block position.
     * @return The packet buffer.
     */
    public static PacketByteBuf createPositionPacket(BlockPos pos) {
        return createBuffer(buffer -> writeBlockPos(buffer, pos));
    }

    /**
     * Creates a vector packet with a Vec3d.
     * @param vec The vector.
     * @return The packet buffer.
     */
    public static PacketByteBuf createVectorPacket(Vec3d vec) {
        return createBuffer(buffer -> writeVec3d(buffer, vec));
    }

    /**
     * Creates a UUID packet.
     * @param uuid The UUID.
     * @return The packet buffer.
     */
    public static PacketByteBuf createUuidPacket(UUID uuid) {
        return createBuffer(buffer -> writeUuid(buffer, uuid));
    }
}
