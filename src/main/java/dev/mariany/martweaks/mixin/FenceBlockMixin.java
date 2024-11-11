package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.entity.decoration.ConnectedLeashKnotEntity;
import net.minecraft.block.FenceBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/LeadItem;attachHeldMobsToBlock(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/ActionResult;"))
    protected ActionResult onUse(PlayerEntity player, World world, BlockPos pos, Operation<ActionResult> original) {
        List<? extends LeashKnotEntity> leashedTo = ConnectedLeashKnotEntity.getLeashedTo(player).stream()
                .filter(LeashKnotEntity.class::isInstance).map(LeashKnotEntity.class::cast).toList();

        if (!leashedTo.isEmpty()) {
            ConnectedLeashKnotEntity connectedLeashKnot = ConnectedLeashKnotEntity.getOrCreate(world, pos);

            for (LeashKnotEntity leashKnot : leashedTo) {
                if (leashKnot.getAttachedBlockPos().equals(pos)) {
                    continue;
                }

                lookAt(leashKnot, connectedLeashKnot);

                if (leashKnot instanceof ConnectedLeashKnotEntity otherConnectedLeashKnot) {
                    otherConnectedLeashKnot.attachLeash(connectedLeashKnot, true);
                }
            }

            connectedLeashKnot.onPlace();
            world.emitGameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Emitter.of(player));
            return ActionResult.SUCCESS;
        }

        return original.call(player, world, pos);
    }

    @Unique
    private static void lookAt(Entity entity, Entity lookAt) {
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookAt.getPos());
        entity.velocityDirty = true;
    }
}
