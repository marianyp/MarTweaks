package dev.mariany.martweaks.packet.serverbound;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RemoveArrowPayload() implements CustomPayload {
    public static final CustomPayload.Id<RemoveArrowPayload> ID = new CustomPayload.Id<>(MarTweaks.id("remove_arrow"));
    public static final PacketCodec<RegistryByteBuf, RemoveArrowPayload> CODEC = PacketCodec.unit(
            new RemoveArrowPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
