package dev.mariany.martweaks.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SunflowerBlock extends TallFlowerBlock {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public SunflowerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(FACING, Direction.EAST));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);

        if (state != null) {
            state = state.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }

        return state;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockPos blockPos = pos.up();
        world.setBlockState(blockPos, withWaterloggedState(world, blockPos, state.with(HALF, DoubleBlockHalf.UPPER)),
                Block.NOTIFY_ALL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        dropStack(world, pos, Blocks.SUNFLOWER.asItem().getDefaultStack());
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState originalState, PlayerEntity player) {
        super.onBreak(world, pos, originalState, player);
        return convertToVanillaState(originalState);
    }

    private static BlockState convertToVanillaState(BlockState originalState) {
        BlockState blockState = Blocks.SUNFLOWER.getDefaultState();

        for (Property<?> property : originalState.getProperties()) {
            blockState = applyProperty(blockState, originalState, property);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState blockState, BlockState originalState,
                                                                      Property<T> property) {
        T value = originalState.get(property);
        return blockState.withIfExists(property, value);
    }
}
