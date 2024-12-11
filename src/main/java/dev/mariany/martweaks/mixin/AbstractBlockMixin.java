package dev.mariany.martweaks.mixin;

import dev.mariany.martweaks.engagement.EngagementManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onStacksDropped", at = @At(value = "HEAD"))
    public void injectOnStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool,
                                      boolean dropExperience, CallbackInfo ci) {
        if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMature(state)) {
            boolean hoe = tool.isIn(ItemTags.HOES);
            EngagementManager.onHarvest(world, pos,
                    base -> hoe ? Math.max(1, EnchantmentHelper.getBlockExperience(world, tool, base)) : base);
        }
    }
}
