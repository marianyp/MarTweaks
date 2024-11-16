package dev.mariany.martweaks.event.block;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.config.MarTweaksConfigModel;
import dev.mariany.martweaks.util.ModUtils;
import io.wispforest.owo.config.ConfigSynchronizer;
import io.wispforest.owo.config.Option;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public class AttackBlockHandler {
    private static final MarTweaksConfigModel.QuickMove.Behavior DISABLED = MarTweaksConfigModel.QuickMove.Behavior.DISABLED;
    private static final MarTweaksConfigModel.QuickMove.Behavior MATCH = MarTweaksConfigModel.QuickMove.Behavior.MATCH;

    public static ActionResult onAttack(PlayerEntity player, World world, Hand hand, BlockPos blockPos,
                                        Direction direction) {
        return handleStorageDeposit(player, blockPos);
    }

    public static ActionResult handleStorageDeposit(PlayerEntity player, BlockPos blockPos) {
        PlayerInventory playerInventory = player.getInventory();

        MarTweaksConfigModel.QuickMove.Behavior behavior = MarTweaks.CONFIG.quickMove.behavior();

        if (behavior != DISABLED && player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld world = serverPlayer.getServerWorld();

            Map<Option.Key, ?> playerConfig = ConfigSynchronizer.getClientOptions(serverPlayer, MarTweaks.CONFIG);

            if (playerConfig != null) {
                Object syncedValue = playerConfig.get(MarTweaks.CONFIG.keys.quickMove_behavior);
                if (syncedValue instanceof MarTweaksConfigModel.QuickMove.Behavior playerBehavior) {
                    behavior = playerBehavior;
                }
            }

            if (player.isSneaking() && player.getMainHandStack().isEmpty()) {
                Inventory inventory = HopperBlockEntity.getInventoryAt(world, blockPos);
                if (inventory != null) {
                    boolean match = behavior.equals(MATCH);
                    int moved = moveItems(playerInventory, inventory, match);

                    int maxMoved = playerInventory.size();
                    float normalizedMoved = Math.min((float) moved / maxMoved, 1.0F);
                    float pitch = 2.0F - normalizedMoved * 1.5F; // Scale to range [0.5F, 2.0F]

                    if (moved > 0) {
                        ModUtils.sendSoundToClient(player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 0.5F,
                                pitch);
                    }
                }
            }
        }

        return ActionResult.PASS;
    }

    public static boolean isHotbarIndex(Inventory inventory, int index) {
        if (!MarTweaks.CONFIG.quickMove.includeHotbar()) {
            if (inventory instanceof PlayerInventory) {
                return PlayerInventory.isValidHotbarIndex(index);
            }
        }

        return false;
    }

    public static int moveItems(Inventory source, Inventory target, boolean matching) {
        int moved = 0;

        for (int sourceSlot = 0; sourceSlot < source.size(); sourceSlot++) {
            ItemStack sourceStack = source.getStack(sourceSlot);

            if (isHotbarIndex(source, sourceSlot)) {
                continue;
            }

            if (!sourceStack.isEmpty()) {
                for (int targetSlot = 0; targetSlot < target.size(); targetSlot++) {
                    ItemStack targetStack = target.getStack(targetSlot);

                    if (!matching || ItemStack.areItemsEqual(sourceStack, targetStack)) {
                        if (moveItemsBetweenInventories(source, target, sourceSlot)) {
                            ++moved;
                        }
                    }
                }
            }
        }

        return moved;
    }

    private static boolean moveItemsBetweenInventories(Inventory source, Inventory target, int sourceSlot) {
        ItemStack sourceStack = source.getStack(sourceSlot);
        ItemStack remainder = insertItem(target, sourceStack);

        source.setStack(sourceSlot, remainder);

        return !ItemStack.areEqual(remainder, sourceStack);
    }

    public static ItemStack insertItem(Inventory inventory, ItemStack stack) {
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
