package dev.mariany.martweaks.packet.clientbound;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record RequestQuickMovePayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<RequestQuickMovePayload> ID = new CustomPayload.Id<>(
            MarTweaks.id("request_quick_move"));
    public static final PacketCodec<RegistryByteBuf, RequestQuickMovePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, RequestQuickMovePayload::pos, RequestQuickMovePayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
