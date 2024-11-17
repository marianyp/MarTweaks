package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrapdoorBlock.class)
public interface TrapdoorBlockAccesor {
    @Accessor("blockSetType")
    BlockSetType martweaks$blockSetType();
}
