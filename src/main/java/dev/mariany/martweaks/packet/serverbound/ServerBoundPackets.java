package dev.mariany.martweaks.packet.serverbound;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.item.ModItems;
import dev.mariany.martweaks.util.QuickMoveUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ServerBoundPackets {
    public static void init() {
        // Remove Arrow
        ServerPlayNetworking.registerGlobalReceiver(RemoveArrowPayload.ID, (payload, context) -> {
            if (MarTweaks.CONFIG.arrowRecovery.enabled()) {
                ServerPlayerEntity player = context.player();
                ServerWorld world = player.getServerWorld();

                int stuckArrowCount = player.getStuckArrowCount();
                if (stuckArrowCount > 0) {
                    player.setStuckArrowCount(stuckArrowCount - 1);
                    player.giveItemStack(ModItems.BROKEN_ARROW.getDefaultStack());
                    world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS,
                            0.5F, MathHelper.nextBetween(player.getRandom(), 0.6F, 3.4F));
                }
            }
        });

        // Quick Move Ticket
        ServerPlayNetworking.registerGlobalReceiver(QuickMoveTicketPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            BlockPos blockPos = payload.pos();
            boolean useKnownItems = payload.useKnownItems();
            boolean includeHotbar = payload.shouldIncludeHotbar();

            QuickMoveUtil.quickMove(player, blockPos, useKnownItems, includeHotbar);
        });
    }
}
