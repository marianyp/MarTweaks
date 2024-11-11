package dev.mariany.martweaks.packet.serverbound;

import dev.mariany.martweaks.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class ServerboundPackets {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(RemoveArrowPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getServerWorld();

            int stuckArrowCount = player.getStuckArrowCount();
            if (stuckArrowCount > 0) {
                player.setStuckArrowCount(stuckArrowCount - 1);
                player.giveItemStack(ModItems.BROKEN_ARROW.getDefaultStack());
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5F,
                        MathHelper.nextBetween(player.getRandom(), 0.6F, 3.4F));
            }
        });
    }
}
