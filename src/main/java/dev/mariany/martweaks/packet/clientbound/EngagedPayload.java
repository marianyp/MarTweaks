package dev.mariany.martweaks.packet.clientbound;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EngagedPayload() implements CustomPayload {
    public static final CustomPayload.Id<EngagedPayload> ID = new CustomPayload.Id<>(MarTweaks.id("engaged"));
    public static final PacketCodec<RegistryByteBuf, EngagedPayload> CODEC = PacketCodec.unit(new EngagedPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
