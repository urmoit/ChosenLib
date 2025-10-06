package com.chosen.lib.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON-based persistence helpers for per-world and per-player data.
 * Not intended for large data; use for small config/state.
 */
public final class DataUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private DataUtils() {}

    public static Path getWorldDataDir(MinecraftServer server) {
        return server.getSavePath(net.minecraft.util.WorldSavePath.ROOT).resolve("chosenlib_data");
    }

    public static Path getPlayerDataDir(MinecraftServer server, ServerPlayerEntity player) {
        return getWorldDataDir(server).resolve("players").resolve(player.getUuidAsString());
    }

    public static Path writeWorldJson(MinecraftServer server, String filename, Object data) throws IOException {
        Path dir = getWorldDataDir(server);
        Files.createDirectories(dir);
        Path file = dir.resolve(filename);
        Files.writeString(file, GSON.toJson(data), StandardCharsets.UTF_8);
        return file;
    }

    public static <T> T readWorldJson(MinecraftServer server, String filename, Class<T> type, T defaultValue) throws IOException {
        Path file = getWorldDataDir(server).resolve(filename);
        if (!Files.exists(file)) return defaultValue;
        String json = Files.readString(file, StandardCharsets.UTF_8);
        T value = GSON.fromJson(json, type);
        return value != null ? value : defaultValue;
    }

    public static Path writePlayerJson(MinecraftServer server, ServerPlayerEntity player, String filename, Object data) throws IOException {
        Path dir = getPlayerDataDir(server, player);
        Files.createDirectories(dir);
        Path file = dir.resolve(filename);
        Files.writeString(file, GSON.toJson(data), StandardCharsets.UTF_8);
        return file;
    }

    public static <T> T readPlayerJson(MinecraftServer server, ServerPlayerEntity player, String filename, Class<T> type, T defaultValue) throws IOException {
        Path file = getPlayerDataDir(server, player).resolve(filename);
        if (!Files.exists(file)) return defaultValue;
        String json = Files.readString(file, StandardCharsets.UTF_8);
        T value = GSON.fromJson(json, type);
        return value != null ? value : defaultValue;
    }
}




