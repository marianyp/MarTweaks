package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.block.StemBlock;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StemBlock.class)
public interface StemBlockAccessor {
    @Accessor("gourdBlock")
    RegistryKey<Block> martweaks$gourdBlock();

    @Accessor("attachedStemBlock")
    RegistryKey<Block> martweaks$attachedStemBlock();
}
