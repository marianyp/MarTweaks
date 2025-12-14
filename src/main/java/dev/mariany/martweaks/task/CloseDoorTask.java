package dev.mariany.martweaks.task;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.block.DoorFlaggable;
import dev.mariany.martweaks.gamerule.ModGamerules;
import dev.mariany.martweaks.mixin.accessor.FenceGateBlockAccessor;
import dev.mariany.martweaks.mixin.accessor.TrapdoorBlockAccessor;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CloseDoorTask {
    private static final BooleanProperty OPEN = Properties.OPEN;

    private final ServerWorld world;
    private final BlockPos pos;
    private final Block block;

    protected CloseDoorTask(ServerWorld world, BlockPos pos, Block block) {
        this.world = world;
        this.pos = pos;
        this.block = block;
    }

    public static void flag(ServerWorld serverWorld, BlockPos pos) {
        ((DoorFlaggable) serverWorld).marTweaks$flagDoorPos(pos);
    }

    public static void create(ServerWorld world, BlockPos pos) {
        int closeInTicks = world.getGameRules().get(ModGamerules.AUTO_CLOSE_IN_TICKS).get();
        boolean handleDoors = world.getGameRules().get(ModGamerules.AUTO_CLOSE_DOORS).get();
        boolean handleTrapdoors = world.getGameRules().get(ModGamerules.AUTO_CLOSE_TRAPDOORS).get();
        boolean handleFenceGates = world.getGameRules().get(ModGamerules.AUTO_CLOSE_FENCE_GATES).get();

        create(world, pos, closeInTicks, handleDoors, handleTrapdoors, handleFenceGates);
    }

    private static void create(
            ServerWorld world,
            BlockPos pos,
            int closeInTicks,
            boolean handleDoors,
            boolean handleTrapdoors,
            boolean handleFenceGates
    ) {
        if (isValidState(world, pos, handleDoors, handleTrapdoors, handleFenceGates)) {
            CloseDoorTask task = new CloseDoorTask(world, pos, world.getBlockState(pos).getBlock());
            MarTweaks.queueServerWork(closeInTicks, task::run);
        }
    }

    public static boolean isValidState(
            ServerWorld world,
            BlockPos pos,
            boolean handleDoors,
            boolean handleTrapdoors,
            boolean handleFenceGates
    ) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!state.contains(OPEN) || !state.get(OPEN)) {
            return false;
        }

        if (world.isReceivingRedstonePower(pos)) {
            return false;
        }

        if (handleDoors && block instanceof DoorBlock) {
            return true;
        }

        if (handleTrapdoors && block instanceof TrapdoorBlock) {
            return true;
        }

        return handleFenceGates && block instanceof FenceGateBlock;
    }

    private void run() {
        BlockState blockState = this.world.getBlockState(this.pos);

        if (blockState.isOf(this.block)) {
            if (this.needsClosing()) {
                this.closeDoor(blockState);
            }
        }
    }

    private boolean needsClosing() {
        return this.world.getBlockState(this.pos).get(OPEN);
    }

    private void closeDoor(BlockState blockState) {
        SoundEvent closeSound = null;
        if (this.block instanceof DoorBlock doorBlock) {
            doorBlock.setOpen(null, this.world, blockState, this.pos, false);
        } else {
            this.world.setBlockState(this.pos, blockState.with(OPEN, false));

            if (this.block instanceof TrapdoorBlock trapdoorBlock) {
                closeSound = ((TrapdoorBlockAccessor) trapdoorBlock).martweaks$blockSetType().trapdoorClose();
            }

            if (this.block instanceof FenceGateBlock fenceGateBlock) {
                closeSound = ((FenceGateBlockAccessor) fenceGateBlock).martweaks$type().fenceGateClose();
            }
        }

        if (closeSound != null) {
            playCloseSound(closeSound);
        }
    }

    private void playCloseSound(SoundEvent sound) {
        this.world.playSound(
                null, this.pos, sound, SoundCategory.BLOCKS, 1F,
                MathHelper.nextFloat(this.world.random, 0.9F, 1F)
        );
    }
}
