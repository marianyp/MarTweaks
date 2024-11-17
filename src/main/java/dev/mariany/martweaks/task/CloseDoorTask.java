package dev.mariany.martweaks.task;

import dev.mariany.martweaks.MarTweaks;
import dev.mariany.martweaks.block.DoorFlaggable;
import dev.mariany.martweaks.mixin.accessor.FenceGateBlockAccesor;
import dev.mariany.martweaks.mixin.accessor.TrapdoorBlockAccesor;
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

    public static void create(ServerWorld world, BlockPos pos, int closeInTicks) {
        create(world, pos, closeInTicks, true, true, true);
    }

    public static void create(ServerWorld world, BlockPos pos, int closeInTicks, boolean handleDoors,
                              boolean handleTrapdoors, boolean handleFenceGates) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (isValidState(blockState, handleDoors, handleTrapdoors, handleFenceGates)) {
            CloseDoorTask task = new CloseDoorTask(world, pos, block);
            MarTweaks.queueServerWork(closeInTicks, task::run);
        }
    }

    public static boolean isValidState(BlockState blockState, boolean handleDoors, boolean handleTrapdoors,
                                       boolean handleFenceGates) {
        Block block = blockState.getBlock();

        if (!blockState.contains(OPEN) || !blockState.get(OPEN)) {
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
                closeSound = ((TrapdoorBlockAccesor) trapdoorBlock).martweaks$blockSetType().trapdoorClose();
            }

            if (this.block instanceof FenceGateBlock fenceGateBlock) {
                closeSound = ((FenceGateBlockAccesor) fenceGateBlock).martweaks$type().fenceGateClose();
            }
        }

        if (closeSound != null) {
            playCloseSound(closeSound);
        }
    }

    private void playCloseSound(SoundEvent sound) {
        this.world.playSound(null, this.pos, sound, SoundCategory.BLOCKS, 1F,
                MathHelper.nextFloat(this.world.random, 0.9F, 1F));
    }
}
