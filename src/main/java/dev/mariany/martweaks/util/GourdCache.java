package dev.mariany.martweaks.util;

import dev.mariany.martweaks.mixin.accessor.StemBlockAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class GourdCache {
    private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Set<Pair<RegistryKey<Block>, RegistryKey<Block>>> gourds = new HashSet<>();

    public static void load() {
        for (Block block : Registries.BLOCK) {
            if (block instanceof StemBlock stemBlock) {
                RegistryKey<Block> gourd = ((StemBlockAccessor) stemBlock).martweaks$gourdBlock();
                RegistryKey<Block> stem = ((StemBlockAccessor) stemBlock).martweaks$attachedStemBlock();

                gourds.add(Pair.of(gourd, stem));
            }
        }
    }

    public static boolean isGourd(Block block) {
        Optional<RegistryKey<Block>> optionalRegistryKey = Registries.BLOCK.getKey(block);
        if (optionalRegistryKey.isPresent()) {
            RegistryKey<Block> registryKey = optionalRegistryKey.get();

            for (Pair<RegistryKey<Block>, RegistryKey<Block>> gourd : gourds) {
                if (gourd.getLeft().equals(registryKey)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isGourd(World world, BlockPos pos, BlockState blockState) {
        Block gourd = blockState.getBlock();
        RegistryKey<Block> gourdKey = Registries.BLOCK.getKey(gourd).orElseThrow();

        Optional<Pair<RegistryKey<Block>, RegistryKey<Block>>> optionalPair = gourds.stream()
                .filter(pair -> pair.getLeft().equals(gourdKey)).findFirst();

        if (optionalPair.isPresent()) {
            RegistryKey<Block> stemKey = optionalPair.get().getRight();

            for (Direction direction : Direction.values()) {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = world.getBlockState(offsetPos);
                Block offsetBlock = offsetState.getBlock();

                RegistryKey<Block> key = Registries.BLOCK.getKey(offsetBlock).orElseThrow();

                if (key.equals(stemKey)) {
                    if (offsetState.contains(FACING)) {
                        Direction facing = offsetState.get(FACING);
                        if (facing.equals(direction.getOpposite())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
