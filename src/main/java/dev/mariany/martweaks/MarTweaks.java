package dev.mariany.martweaks;

import dev.mariany.martweaks.event.server.ServerTickHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarTweaks implements ModInitializer {
    public static final String MOD_ID = "martweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(ServerTickHandler::onServerTick);
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}