package dev.mariany.martweaks.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UseEntityHandler {
    public static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity,
                                           @Nullable EntityHitResult entityHitResult) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!player.isSpectator()) {
            return handleItemFrame(entity, player, itemStack);
        }
        return ActionResult.PASS;
    }

    private static ActionResult handleItemFrame(Entity entity, PlayerEntity player, ItemStack itemStack) {
        if (itemStack.isEmpty() && player.isSneaking()) {
            if (entity instanceof ItemFrameEntity itemFrameEntity) {
                if (!itemFrameEntity.getHeldItemStack().isEmpty()) {
                    boolean isInvisible = itemFrameEntity.isInvisible();
                    itemFrameEntity.setInvisible(!isInvisible);
                    itemFrameEntity.playSound(itemFrameEntity.getRotateItemSound(), 1F, 1F);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}
