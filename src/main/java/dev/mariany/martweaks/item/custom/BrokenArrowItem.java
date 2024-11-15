package dev.mariany.martweaks.item.custom;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BrokenArrowItem extends Item {
    public BrokenArrowItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Random random = world.getRandom();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);

        if (player != null) {
            if (blockState.getBlock() instanceof FletchingTableBlock fletchingTable) {
                if (world instanceof ServerWorld serverWorld) {
                    ItemStack repairedArrow = repair(fletchingTable, stack, player);
                    player.setStackInHand(hand, repairedArrow);

                    if (MarTweaks.CONFIG.arrowRecovery.rewardRepairingBrokenArrow()) {
                        ExperienceOrbEntity.spawn(serverWorld, pos.up().toBottomCenterPos(),
                                MathHelper.nextBetween(random, 1, 3));
                    }

                    serverWorld.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.NEUTRAL, 0.5F,
                            MathHelper.nextBetween(random, 0.1F, 0.3F));
                }

                return ActionResult.success(world.isClient);
            }
        }

        return super.useOnBlock(context);
    }

    protected ItemStack repair(Block fletchingTable, ItemStack stack, PlayerEntity player) {
        player.incrementStat(Stats.USED.getOrCreateStat(fletchingTable.asItem()));
        return ItemUsage.exchangeStack(stack, player, Items.ARROW.getDefaultStack());
    }
}
