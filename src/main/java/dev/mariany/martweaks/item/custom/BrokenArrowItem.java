package dev.mariany.martweaks.item.custom;

import dev.mariany.martweaks.MarTweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);

        if (player != null) {
            if (blockState.getBlock() instanceof FletchingTableBlock) {
                if (world instanceof ServerWorld serverWorld) {
                    repair(world, pos, stack);

                    if (MarTweaks.CONFIG.arrowRecovery.rewardRepairingBrokenArrow()) {
                        ExperienceOrbEntity.spawn(serverWorld, pos.up().toBottomCenterPos(),
                                MathHelper.nextBetween(random, 1, 3));
                    }

                    serverWorld.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.NEUTRAL, 0.5F,
                            MathHelper.nextBetween(random, 0.1F, 0.3F));
                }

                return ActionResult.SUCCESS_SERVER;
            }
        }

        return super.useOnBlock(context);
    }

    protected void repair(World world, BlockPos pos, ItemStack stack) {
        stack.decrement(1);
        ItemDispenserBehavior.spawnItem(world, Items.ARROW.getDefaultStack(), 2, Direction.UP,
                Vec3d.ofBottomCenter(pos).offset(Direction.UP, 1.2));
    }
}
