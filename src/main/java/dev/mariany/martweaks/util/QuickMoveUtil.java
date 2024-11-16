package dev.mariany.martweaks.util;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class QuickMoveUtil {
    public static void quickMove(ServerPlayerEntity player, BlockPos blockPos, boolean useKnownItems,
                                 boolean includeHotbar) {
        ServerWorld world = player.getServerWorld();
        PlayerInventory playerInventory = player.getInventory();
        Inventory inventory = HopperBlockEntity.getInventoryAt(world, blockPos);

        if (inventory != null && inventory.canPlayerUse(player)) {
            int moved = moveItems(playerInventory, inventory, useKnownItems, includeHotbar);

            if (moved > 0) {
                ModUtils.sendSoundToClient(player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 0.5F,
                        MathHelper.nextFloat(world.random, 0.6F, 1.6F));
            }
        }
    }

    private static int moveItems(Inventory source, Inventory target, boolean useKnownItems, boolean includeHotbar) {
        int moved = 0;

        for (int sourceSlot = 0; sourceSlot < source.size(); sourceSlot++) {
            ItemStack sourceStack = source.getStack(sourceSlot);

            if (!includeHotbar && isHotbarIndex(source, sourceSlot)) {
                continue;
            }

            if (!sourceStack.isEmpty()) {
                for (int targetSlot = 0; targetSlot < target.size(); targetSlot++) {
                    ItemStack targetStack = target.getStack(targetSlot);

                    if (!useKnownItems || ItemStack.areItemsEqual(sourceStack, targetStack)) {
                        if (moveItemsBetweenInventories(source, target, sourceSlot)) {
                            ++moved;
                        }
                    }
                }
            }
        }

        return moved;
    }

    private static boolean isHotbarIndex(Inventory inventory, int index) {
        if (inventory instanceof PlayerInventory) {
            return PlayerInventory.isValidHotbarIndex(index);
        }

        return false;
    }

    private static boolean moveItemsBetweenInventories(Inventory source, Inventory target, int sourceSlot) {
        ItemStack sourceStack = source.getStack(sourceSlot);
        ItemStack remainder = insertItem(target, sourceStack);

        source.setStack(sourceSlot, remainder);

        return !ItemStack.areEqual(remainder, sourceStack);
    }

    private static ItemStack insertItem(Inventory inventory, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = stack.copy();
        addToExistingSlot(inventory, itemStack);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        addToNewSlot(inventory, itemStack);

        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }

    private static void addToExistingSlot(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
                transfer(inventory, stack, itemStack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }
    }

    private static void addToNewSlot(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) {
                inventory.setStack(i, stack.copyAndEmpty());
                return;
            }
        }
    }

    private static void transfer(Inventory inventory, ItemStack source, ItemStack target) {
        int maxCount = inventory.getMaxCount(target);
        int transferAmount = Math.min(source.getCount(), maxCount - target.getCount());
        if (transferAmount > 0) {
            target.increment(transferAmount);
            source.decrement(transferAmount);
        }
    }
}
