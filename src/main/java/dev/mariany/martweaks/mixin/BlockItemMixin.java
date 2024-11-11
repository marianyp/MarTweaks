package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @WrapOperation(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;"))
    protected BlockState wrapGetPlacementState(Block instance, ItemPlacementContext ctx,
                                               Operation<BlockState> original) {
        BlockItem blockItem = (BlockItem) (Object) this;
        Block block = blockItem.getBlock();

        if (block.equals(Blocks.SUNFLOWER)) {
            return ModBlocks.SUNFLOWER.getPlacementState(ctx);
        }

        return original.call(instance, ctx);
    }
}
