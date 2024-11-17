package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FenceGateBlock.class)
public interface FenceGateBlockAccesor {
    @Accessor("type")
    WoodType martweaks$type();
}
