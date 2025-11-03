package com.chosen.lib.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Represents a command that can be registered with the CommandManager.
 */
public interface Command {
    /**
     * Registers the command with the given dispatcher.
     * @param dispatcher The command dispatcher.
     */
    void register(CommandDispatcher<ServerCommandSource> dispatcher);
}
