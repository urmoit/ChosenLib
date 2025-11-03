package com.chosen.lib.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the registration of commands.
 */
public class CommandManager {
    private static final List<Command> commands = new ArrayList<>();

    /**
     * Registers a command to be initialized with the server.
     * @param command The command to register.
     */
    public static void registerCommand(Command command) {
        commands.add(command);
    }

    /**
     * Initializes the command manager and registers all queued commands.
     * This should be called in the mod's onInitialize method.
     */
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            for (Command command : commands) {
                command.register(dispatcher);
            }
        });
    }
}
