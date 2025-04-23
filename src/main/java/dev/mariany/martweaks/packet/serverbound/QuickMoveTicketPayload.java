package dev.mariany.martweaks.packet.serverbound;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record QuickMoveTicketPayload(BlockPos pos, boolean useKnownItems,
                                     boolean shouldIncludeHotbar) implements CustomPayload {
    public static final CustomPayload.Id<QuickMoveTicketPayload> ID = new CustomPayload.Id<>(
            MarTweaks.id("quick_move_ticket"));
    public static final PacketCodec<RegistryByteBuf, QuickMoveTicketPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, QuickMoveTicketPayload::pos, PacketCodecs.BOOLEAN,
            QuickMoveTicketPayload::useKnownItems, PacketCodecs.BOOLEAN, QuickMoveTicketPayload::shouldIncludeHotbar,
            QuickMoveTicketPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
