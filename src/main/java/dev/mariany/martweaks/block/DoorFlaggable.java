package dev.mariany.martweaks.block;

import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface DoorFlaggable {
    void marTweaks$flagDoorPos(BlockPos pos);
    Set<BlockPos> marTweaks$getFlags();
}
