package com.chosen.lib;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChosenLib implements ModInitializer {
    public static final String MOD_ID = "chosenlib";
    public static final String MOD_NAME = "ChosenLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static ChosenLib instance;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("{} v{} initialized", MOD_NAME, getVersion());
    }

    public static ChosenLib getInstance() {
        return instance;
    }

    public static String getVersion() {
        return ChosenLib.class.getPackage().getImplementationVersion();
    }
}