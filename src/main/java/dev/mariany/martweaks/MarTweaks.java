package dev.mariany.martweaks;

import dev.mariany.martweaks.block.ModBlocks;
import dev.mariany.martweaks.config.MarTweaksConfig;
import dev.mariany.martweaks.event.block.AttackBlockHandler;
import dev.mariany.martweaks.event.block.UseBlockHandler;
import dev.mariany.martweaks.event.entity.UseEntityHandler;
import dev.mariany.martweaks.event.item.ModifyItemComponentsHandler;
import dev.mariany.martweaks.event.server.ServerTickHandler;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.item.ModItems;
import dev.mariany.martweaks.packet.Packets;
import dev.mariany.martweaks.packet.serverbound.ServerBoundPackets;
import dev.mariany.martweaks.util.GourdCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarTweaks implements ModInitializer {
    public static final String MOD_ID = "martweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MarTweaksConfig CONFIG = MarTweaksConfig.createAndLoad();

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void onInitialize() {
        Packets.register();
        ServerBoundPackets.init();

        ModGamerules.registerModGamerules();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();

        GourdCache.load();

        ServerTickEvents.START_SERVER_TICK.register(ServerTickHandler::onServerTick);
        ServerTickEvents.END_SERVER_TICK.register(MarTweaks::handleWorkQueue);
        UseBlockCallback.EVENT.register(UseBlockHandler::onUseBlock);
        UseEntityCallback.EVENT.register(UseEntityHandler::onUseEntity);
        DefaultItemComponentEvents.MODIFY.register(ModifyItemComponentsHandler::modify);
        AttackBlockCallback.EVENT.register(AttackBlockHandler::onAttack);
    }

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    private static void handleWorkQueue(MinecraftServer minecraftServer) {
        List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
        workQueue.forEach(work -> {
            work.setValue(work.getValue() - 1);
            if (work.getValue() == 0)
                actions.add(work);
        });
        actions.forEach(e -> e.getKey().run());
        workQueue.removeAll(actions);
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}