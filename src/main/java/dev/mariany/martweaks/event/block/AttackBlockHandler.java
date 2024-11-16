package dev.mariany.martweaks.event.block;

import dev.mariany.martweaks.mixin.accessor.ServerPlayerInteractionManagerAccesor;
import dev.mariany.martweaks.packet.clientbound.RequestQuickMovePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AttackBlockHandler {
    public static ActionResult onAttack(PlayerEntity player, World world, Hand hand, BlockPos blockPos,
                                        Direction direction) {
        return handleStorageDeposit(player, blockPos);
    }

    public static ActionResult handleStorageDeposit(PlayerEntity player, BlockPos blockPos) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (serverPlayer.isSneaking() && serverPlayer.getMainHandStack().isEmpty()) {
                ServerPlayerInteractionManagerAccesor interactionManager = (ServerPlayerInteractionManagerAccesor) serverPlayer.interactionManager;
                int breakProgress = interactionManager.martweaks$blockBreakingProgress();

                if (breakProgress <= 0) {
                    ServerPlayNetworking.send(serverPlayer, new RequestQuickMovePayload(blockPos));
                }
            }
        }

        return ActionResult.PASS;
    }
}
