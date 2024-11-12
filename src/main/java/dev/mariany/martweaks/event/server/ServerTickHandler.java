package dev.mariany.martweaks.event.server;

import dev.mariany.martweaks.event.entity.ItemEntityTickHandler;
import dev.mariany.martweaks.event.entity.LivingEntityTickHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.function.Consumer;

public class ServerTickHandler {
    private static final List<Consumer<ServerWorld>> HANDLERS = List.of(LivingEntityTickHandler::onServerWorldTick,
            ItemEntityTickHandler::onServerWorldTick);

    public static void onServerTick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            for (Consumer<ServerWorld> handler : HANDLERS) {
                handler.accept(world);
            }
        }
    }
}
