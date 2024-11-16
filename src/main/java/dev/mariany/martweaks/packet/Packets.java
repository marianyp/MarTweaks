package dev.mariany.martweaks.packet;

import dev.mariany.martweaks.packet.clientbound.EngagedPayload;
import dev.mariany.martweaks.packet.clientbound.RequestQuickMovePayload;
import dev.mariany.martweaks.packet.serverbound.QuickMoveTicketPayload;
import dev.mariany.martweaks.packet.serverbound.RemoveArrowPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class Packets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(EngagedPayload.ID, EngagedPayload.CODEC);
        registry.register(RequestQuickMovePayload.ID, RequestQuickMovePayload.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(RemoveArrowPayload.ID, RemoveArrowPayload.CODEC);
        registry.register(QuickMoveTicketPayload.ID, QuickMoveTicketPayload.CODEC);
    }
}
