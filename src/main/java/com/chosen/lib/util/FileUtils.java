package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * File System Utilities for safe file operations and data management.
 * Provides helpers for reading, writing, copying, moving, and deleting files and directories.
 */
public class FileUtils {

    /**
     * Reads the content of a file into a string.
     * @param path The path to the file.
     * @return The content of the file as a string, or null if an error occurs.
     */
    public static String readFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to read file: " + path, e);
            return null;
        }
    }

    /**
     * Writes content to a file. If the file does not exist, it will be created.
     * @param path The path to the file.
     * @param content The content to write.
     * @return True if the file was written successfully, false otherwise.
     */
    public static boolean writeFile(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to write to file: " + path, e);
            return false;
        }
    }

    /**
     * Copies a file from a source path to a destination path.
     * @param source The source file path.
     * @param destination The destination file path.
     * @return True if the file was copied successfully, false otherwise.
     */
    public static boolean copyFile(Path source, Path destination) {
        try {
            Files.createDirectories(destination.getParent());
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to copy file from " + source + " to " + destination, e);
            return false;
        }
    }

    /**
     * Moves a file from a source path to a destination path.
     * @param source The source file path.
     * @param destination The destination file path.
     * @return True if the file was moved successfully, false otherwise.
     */
    public static boolean moveFile(Path source, Path destination) {
        try {
            Files.createDirectories(destination.getParent());
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to move file from " + source + " to " + destination, e);
            return false;
        }
    }

    /**
     * Deletes a file.
     * @param path The path to the file.
     * @return True if the file was deleted successfully, false otherwise.
     */
    public static boolean deleteFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to delete file: " + path, e);
            return false;
        }
    }

    /**
     * Creates a directory at the specified path, including any necessary parent directories.
     * @param path The path to the directory.
     * @return True if the directory was created successfully, false otherwise.
     */
    public static boolean createDirectory(Path path) {
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to create directory: " + path, e);
            return false;
        }
    }

    /**
     * Lists all files and directories in a given directory.
     * @param path The path to the directory.
     * @return A list of paths, or an empty list if an error occurs or the directory is empty.
     */
    public static List<Path> listFiles(Path path) {
        List<Path> paths = new ArrayList<>();
        if (!Files.isDirectory(path)) {
            return paths;
        }
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(paths::add);
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to list files in directory: " + path, e);
        }
        return paths;
    }

    /**
     * Searches for files matching a glob pattern within a directory.
     * @param startPath The directory to start the search from.
     * @param globPattern The glob pattern to match (e.g., "*.json", "**\/*.java").
     * @return A list of matching paths, or an empty list if an error occurs.
     */
    public static List<Path> searchFiles(Path startPath, String globPattern) {
        List<Path> matchingFiles = new ArrayList<>();
        if (!Files.isDirectory(startPath)) {
            return matchingFiles;
        }
        try (Stream<Path> stream = Files.find(startPath, Integer.MAX_VALUE, (path, basicFileAttributes) -> {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
            return pathMatcher.matches(path.getFileName());
        })) {
            stream.forEach(matchingFiles::add);
        } catch (IOException e) {
            ChosenLib.LOGGER.error("Failed to search for files in directory: " + startPath, e);
        }
        return matchingFiles;
    }
}
