package com.chosen.lib.client;

import com.chosen.lib.ChosenLib;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ChosenClient implements ClientModInitializer {
    private static KeyBinding debugKeyBinding;

    @Override
    public void onInitializeClient() {
        // Register key bindings
        debugKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chosenlib.debug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.chosenlib"
        ));

        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (debugKeyBinding.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("ChosenLib debug mode activated!"),
                            false
                    );
                }
            }
        });

        ChosenLib.LOGGER.info("ChosenLib client initialized");
    }

    public static KeyBinding getDebugKeyBinding() {
        return debugKeyBinding;
    }
}