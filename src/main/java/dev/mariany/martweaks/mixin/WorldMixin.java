package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.block.DoorFlaggable;
import dev.mariany.martweaks.task.CloseDoorTask;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mixin(World.class)
public class WorldMixin implements DoorFlaggable {
    @Unique
    @Nullable
    private final Set<BlockPos> flaggedDoorPositions = new HashSet<>();

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At(value = "RETURN"))
    public void injectSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
                                    CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;

        if (cir.getReturnValue()) {
            if (world instanceof ServerWorld serverWorld && serverWorld.isPosLoaded(pos.getX(), pos.getZ())) {
                if (!state.contains(DoorBlock.HALF) || state.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
                    Set<BlockPos> flaggedDoorPositions = marTweaks$getFlags();
                    boolean flagged = flaggedDoorPositions.stream().anyMatch(flaggedPos -> flaggedPos.equals(pos));

                    if (!flagged && !state.isAir()) {
                        CloseDoorTask.create(serverWorld, pos, 30);
                    } else {
                        flaggedDoorPositions.remove(pos);
                    }
                }
            }
        }
    }

    @Override
    public void marTweaks$flagDoorPos(BlockPos pos) {
        if (this.flaggedDoorPositions != null) {
            this.flaggedDoorPositions.add(pos);
        }
    }

    @Override
    public Set<BlockPos> marTweaks$getFlags() {
        return Objects.requireNonNullElseGet(this.flaggedDoorPositions, Set::of);
    }
}
