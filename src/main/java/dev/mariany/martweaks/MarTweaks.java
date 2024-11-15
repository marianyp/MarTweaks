package dev.mariany.martweaks;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.config.MarTweaksConfig;
import dev.mariany.martweaks.event.block.UseBlockHandler;
import dev.mariany.martweaks.event.item.ModifyItemComponentsHandler;
import dev.mariany.martweaks.event.server.ServerTickHandler;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.item.ModItems;
import dev.mariany.martweaks.packet.Packets;
import dev.mariany.martweaks.packet.serverbound.ServerBoundPackets;
import dev.mariany.martweaks.util.GourdCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarTweaks implements ModInitializer {
    public static final String MOD_ID = "martweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MarTweaksConfig CONFIG = MarTweaksConfig.createAndLoad();

    @Override
    public void onInitialize() {
        Packets.register();
        ServerBoundPackets.init();

        ModGamerules.registerModGamerules();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();

        GourdCache.load();

        ServerTickEvents.START_SERVER_TICK.register(ServerTickHandler::onServerTick);
        UseBlockCallback.EVENT.register(UseBlockHandler::onUseBlock);
        DefaultItemComponentEvents.MODIFY.register(ModifyItemComponentsHandler::modify);
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}