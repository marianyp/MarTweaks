package dev.mariany.martweaks.packet.clientbound;

import dev.mariany.martweaks.client.MarTweaksClient;
import dev.mariany.martweaks.client.quickmove.QuickMoveState;
import dev.mariany.martweaks.packet.serverbound.QuickMoveTicketPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ClientBoundPackets {
    public static void init() {
        // Quick Move Requested
        ClientPlayNetworking.registerGlobalReceiver(RequestQuickMovePayload.ID, (payload, context) -> {
            PlayerEntity player = context.player();
            ItemStack stack = player.getMainHandStack();

            if (MarTweaksClient.CONFIG.quickMove.enabled()) {
                if (!MarTweaksClient.CONFIG.quickMove.requireEmptyHand() || stack.isEmpty()) {
                    QuickMoveState quickMoveState = MarTweaksClient.QUICK_MOVE_STATE.next(payload.pos());

                    BlockPos pos = quickMoveState.getPos();
                    boolean useKnownItems = quickMoveState.useKnownItems();
                    boolean shouldIncludeHotbar = quickMoveState.shouldIncludeHotbar();

                    if (pos != null) {
                        QuickMoveTicketPayload ticket = new QuickMoveTicketPayload(pos, useKnownItems,
                                shouldIncludeHotbar);
                        context.responseSender().sendPacket(ticket);
                    }
                }
            }
        });
    }
}
